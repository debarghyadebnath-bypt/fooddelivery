package com.example.foodelivery.dto;

import com.example.foodelivery.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentStatusUpdateDto {
    @NotNull
    private PaymentStatus paymentStatus;
}