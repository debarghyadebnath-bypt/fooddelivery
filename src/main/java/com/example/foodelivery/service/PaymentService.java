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
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          ModelMapper modelMapper,
                          @Value("${stripe.api.key}") String stripeApiKey) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public PaymentDto createPaymentIntent(Order order) {
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

            PaymentDto paymentDto = modelMapper.map(payment, PaymentDto.class);
            paymentDto.setClientSecret(paymentIntent.getClientSecret());
            return paymentDto;
        } catch (StripeException ex) {
            throw new IllegalStateException("Unable to create Stripe payment intent", ex);
        }
    }

    @Transactional
    public PaymentDto confirmPayment(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED && order.getOrderStatus() == OrderStatus.CONFIRMED) {
            return modelMapper.map(payment, PaymentDto.class);
        }

        if (payment.getStripePaymentIntentId() == null) {
            throw new IllegalStateException("Stripe payment intent is missing for order id: " + orderId);
        }

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(payment.getStripePaymentIntentId());
            if (!"succeeded".equalsIgnoreCase(paymentIntent.getStatus())) {
                throw new IllegalStateException("Payment has not succeeded yet");
            }

            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment = paymentRepository.save(payment);

            order.setOrderStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            order.setPayment(payment);

            return modelMapper.map(payment, PaymentDto.class);
        } catch (StripeException ex) {
            throw new IllegalStateException("Unable to verify Stripe payment intent", ex);
        }
    }

    private long toStripeAmount(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
}