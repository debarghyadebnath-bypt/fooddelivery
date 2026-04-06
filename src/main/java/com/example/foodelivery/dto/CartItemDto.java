package com.example.foodelivery.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItemDto {
    private Long id;

    private Long cartId;

    @NotNull
    private Long menuItemId;

    @NotNull
    @Min(1)
    private Integer quantity;

    private BigDecimal price;

    private LocalDateTime createdAt;
}