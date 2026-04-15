package com.example.foodelivery.service;

import com.example.foodelivery.dto.OrderDto;
import com.example.foodelivery.dto.PageableDto;
import com.example.foodelivery.dto.PaymentDto;
import com.example.foodelivery.entity.Cart;
import com.example.foodelivery.entity.CartItem;
import com.example.foodelivery.entity.CartStatus;
import com.example.foodelivery.entity.MenuItem;
import com.example.foodelivery.entity.Order;
import com.example.foodelivery.entity.OrderStatus;
import com.example.foodelivery.entity.Restaurant;
import com.example.foodelivery.exceptions.ResourceNotFoundException;
import com.example.foodelivery.repository.CartItemRepository;
import com.example.foodelivery.repository.CartRepository;
import com.example.foodelivery.repository.OrderRepository;
import com.example.foodelivery.service.CartService;
import com.example.foodelivery.service.PaymentService;
import com.example.foodelivery.utils.PageResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        CartService cartService,
                        PaymentService paymentService,
                        ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public OrderDto placeOrder(Long userId) {
        Cart cart = cartService.getActiveCart(userId);
        List<CartItem> cartItems = new ArrayList<>(cart.getCartItems());

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        validateSingleRestaurant(cartItems);

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Restaurant restaurant = cartItems.get(0).getMenuItem().getRestaurant();
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setRestaurant(restaurant);
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        PaymentDto paymentDto = paymentService.createPaymentIntent(savedOrder);

        cartItemRepository.deleteAll(cartItems);
        cart.setStatus(CartStatus.ORDERED);
        cartRepository.save(cart);

        OrderDto orderDto = modelMapper.map(savedOrder, OrderDto.class);
        orderDto.setPayment(paymentDto);
        return orderDto;
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderDto> getAll(Long userId, Pageable pageable) {
        Page<OrderDto> orderDtos = orderRepository.findByUserId(userId, pageable)
                .map(order -> modelMapper.map(order, OrderDto.class));

        return new PageResponse<>(orderDtos.getContent(),
                new PageableDto(pageable.getPageNumber(), pageable.getPageSize()),
                orderDtos.getTotalElements(), orderDtos.getTotalPages(),
                orderDtos.getSize(), orderDtos.getNumberOfElements());
    }

        @Transactional(readOnly = true)
        public PageResponse<OrderDto> getByRestaurantId(Long restaurantId, Pageable pageable) {
        Page<OrderDto> orderDtos = orderRepository.findByRestaurantId(restaurantId, pageable)
            .map(order -> modelMapper.map(order, OrderDto.class));

        return new PageResponse<>(orderDtos.getContent(),
            new PageableDto(pageable.getPageNumber(), pageable.getPageSize()),
            orderDtos.getTotalElements(), orderDtos.getTotalPages(),
            orderDtos.getSize(), orderDtos.getNumberOfElements());
        }

        @Transactional(readOnly = true)
        public PageResponse<OrderDto> getDeliveryOrders(Pageable pageable) {
        List<OrderStatus> deliveryStatuses = List.of(
            OrderStatus.PLACED,
            OrderStatus.CONFIRMED,
            OrderStatus.PREPARING,
            OrderStatus.OUT_FOR_DELIVERY
        );

        Page<OrderDto> orderDtos = orderRepository.findByOrderStatusIn(deliveryStatuses, pageable)
            .map(order -> modelMapper.map(order, OrderDto.class));

        return new PageResponse<>(orderDtos.getContent(),
            new PageableDto(pageable.getPageNumber(), pageable.getPageSize()),
            orderDtos.getTotalElements(), orderDtos.getTotalPages(),
            orderDtos.getSize(), orderDtos.getNumberOfElements());
        }

    @Transactional(readOnly = true)
    public OrderDto getById(Long userId, Long id) {
        Order order = orderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return modelMapper.map(order, OrderDto.class);
    }

        @Transactional
        public OrderDto updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setOrderStatus(orderStatus);
        Order updatedOrder = orderRepository.save(order);
        return modelMapper.map(updatedOrder, OrderDto.class);
        }

    private void validateSingleRestaurant(List<CartItem> cartItems) {
        Long restaurantId = cartItems.get(0).getMenuItem().getRestaurant().getId();

        boolean mixedRestaurants = cartItems.stream()
                .map(CartItem::getMenuItem)
                .map(MenuItem::getRestaurant)
                .map(Restaurant::getId)
                .anyMatch(id -> !restaurantId.equals(id));

        if (mixedRestaurants) {
            throw new IllegalStateException("Cart items must belong to a single restaurant to place an order");
        }
    }
}