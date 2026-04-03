package com.example.foodelivery.dto;

import com.example.foodelivery.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private PaymentDto payment;
}