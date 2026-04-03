package com.example.foodelivery.dto;

import com.example.foodelivery.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateDto {
    @NotNull
    private OrderStatus orderStatus;
}