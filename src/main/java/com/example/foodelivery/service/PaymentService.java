package com.example.foodelivery.service;

import com.example.foodelivery.dto.PaymentDto;
import com.example.foodelivery.entity.Order;
import com.example.foodelivery.entity.OrderStatus;
import com.example.foodelivery.entity.Payment;
import com.example.foodelivery.entity.PaymentMethod;
import com.example.foodelivery.entity.PaymentStatus;
import com.example.foodelivery.exceptions.ResourceNotFoundException;
import com.example.foodelivery.repository.OrderRepository;
import com.example.foodelivery.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final String stripeWebhookSecret;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          ModelMapper modelMapper,
                          ObjectMapper objectMapper,
                          @Value("${stripe.api.key}") String stripeApiKey,
                          @Value("${stripe.webhook.secret:}") String stripeWebhookSecret) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.stripeWebhookSecret = stripeWebhookSecret;
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public PaymentDto createPaymentIntent(Order order) {
        log.info("Creating Stripe payment intent for orderId={} amount={}", order.getId(), order.getTotalAmount());
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElseGet(Payment::new);
        payment.setOrder(order);
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotalAmount());

        payment = paymentRepository.save(payment);

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(toStripeAmount(order.getTotalAmount()))
                    .setCurrency("usd")
                    .putMetadata("orderId", order.getId().toString())
                    .putMetadata("paymentId", payment.getId().toString())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            payment.setStripePaymentIntentId(paymentIntent.getId());
            payment = paymentRepository.save(payment);
            order.setPayment(payment);

            log.info("Stripe payment intent created: intentId={} paymentId={}", paymentIntent.getId(), payment.getId());
            PaymentDto paymentDto = modelMapper.map(payment, PaymentDto.class);
            paymentDto.setClientSecret(paymentIntent.getClientSecret());
            return paymentDto;
        } catch (StripeException ex) {
            log.error("Unable to create Stripe payment intent for orderId={}", order.getId(), ex);
            throw new IllegalStateException("Unable to create Stripe payment intent", ex);
        }
    }

    @Transactional
    public PaymentDto confirmPayment(Long userId, Long orderId) {
        log.info("Confirming payment for orderId={}", orderId);
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED && order.getOrderStatus() == OrderStatus.CONFIRMED) {
            log.info("Payment already completed for orderId={}", orderId);
            return modelMapper.map(payment, PaymentDto.class);
        }

        if (payment.getStripePaymentIntentId() == null) {
            log.warn("Stripe payment intent id missing for orderId={}", orderId);
            throw new IllegalStateException("Stripe payment intent is missing for order id: " + orderId);
        }

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(payment.getStripePaymentIntentId());
            log.debug("Retrieved Stripe payment intent {} status={}", paymentIntent.getId(), paymentIntent.getStatus());
            if (!"succeeded".equalsIgnoreCase(paymentIntent.getStatus())) {
                log.info("Stripe payment intent not succeeded yet: intentId={} status={}", paymentIntent.getId(), paymentIntent.getStatus());
                throw new IllegalStateException("Payment has not succeeded yet");
            }

            return completePayment(order, payment);
        } catch (StripeException ex) {
            log.error("Unable to verify Stripe payment intent for orderId={}", orderId, ex);
            throw new IllegalStateException("Unable to verify Stripe payment intent", ex);
        }
    }

    @Transactional
    public void handleStripeWebhook(String payload, String stripeSignature) {
        log.info("Received Stripe webhook event payload={}", payload);
        if (stripeWebhookSecret == null || stripeWebhookSecret.isBlank()) {
            log.error("Stripe webhook secret is not configured");
            throw new IllegalStateException("Stripe webhook secret is not configured");
        }

        Event event;
        try {
            // Verify the webhook signature before trusting the event payload.
            event = Webhook.constructEvent(payload, stripeSignature, stripeWebhookSecret);
            log.debug("Stripe webhook signature verified for event id={}", event.getId());
        } catch (Exception ex) {
            log.error("Unable to verify Stripe webhook signature", ex);
            throw new IllegalStateException("Unable to verify Stripe webhook", ex);
        }

        log.info("Stripe webhook event type={}", event.getType());
        if (!"payment_intent.succeeded".equals(event.getType())) {
            log.info("Ignoring Stripe event type={}", event.getType());
            return;
        }

        PaymentIntent paymentIntent = resolvePaymentIntent(event, payload);
        String paymentIntentId = paymentIntent.getId();
        String orderIdValue = paymentIntent.getMetadata().get("orderId");
        String paymentIdValue = paymentIntent.getMetadata().get("paymentId");

        log.info("Processing payment_intent.succeeded intentId={} orderId={} paymentId={}",
                paymentIntentId, orderIdValue, paymentIdValue);

        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> {
                    log.error("Payment record not found for Stripe intent id={}", paymentIntentId);
                    return new ResourceNotFoundException(
                            "Payment not found for Stripe payment intent id: " + paymentIntentId);
                });

        Order order = payment.getOrder();
        if (order == null && orderIdValue != null && !orderIdValue.isBlank()) {
            order = orderRepository.findById(Long.valueOf(orderIdValue))
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderIdValue));
        }

        if (order == null) {
            throw new IllegalStateException("Order could not be resolved for Stripe payment intent id: " + paymentIntentId);
        }

        completePayment(order, payment);
    }

    private PaymentIntent resolvePaymentIntent(Event event, String payload) {
        Object eventObject = event.getDataObjectDeserializer().getObject().orElse(null);
        if (eventObject instanceof PaymentIntent paymentIntent) {
            return paymentIntent;
        }

        log.warn("Unable to deserialize Stripe payment intent from webhook event id={}, falling back to raw payload", event.getId());

        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            JsonNode objectNode = rootNode.path("data").path("object");
            String intentId = objectNode.path("id").asText(null);
            if (intentId == null || intentId.isBlank()) {
                throw new IllegalStateException("Stripe payment intent id not found in webhook payload");
            }

            log.info("Fallback resolved Stripe payment intent id={} from raw payload", intentId);
            return PaymentIntent.retrieve(intentId);
        } catch (Exception ex) {
            log.error("Unable to resolve Stripe payment intent from raw payload for event id={}", event.getId(), ex);
            throw new IllegalStateException("Unable to resolve Stripe payment intent", ex);
        }
    }

    private PaymentDto completePayment(Order order, Payment payment) {
        // Keep the order and payment rows in sync when Stripe says the charge succeeded.
        log.info("Completing paymentId={} for orderId={}", payment.getId(), order.getId());
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment = paymentRepository.save(payment);

        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setPayment(payment);
        orderRepository.save(order);

        log.info("Payment completed and order confirmed: orderId={} paymentId={}", order.getId(), payment.getId());
        return modelMapper.map(payment, PaymentDto.class);
    }

    private long toStripeAmount(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
}