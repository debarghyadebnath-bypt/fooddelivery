package com.example.foodelivery.dto;

import com.example.foodelivery.entity.CartStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartDto {
    private Long id;
    private Long userId;
    private CartStatus status;
    private LocalDateTime createdAt;
    private List<CartItemDto> items;
    private BigDecimal totalAmount;
}