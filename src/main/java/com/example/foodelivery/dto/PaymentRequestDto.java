package com.example.foodelivery.dto;

import com.example.foodelivery.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    @NotNull
    private Long orderId;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    private BigDecimal amount;
}