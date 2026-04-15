package com.example.foodelivery.dto;

import com.example.foodelivery.entity.PaymentMethod;
import com.example.foodelivery.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private Long id;

    @NotNull
    private Long orderId;

    @NotNull
    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String stripePaymentIntentId;

    private String clientSecret;

    @NotNull
    private BigDecimal amount;

    private LocalDateTime createdAt;
}