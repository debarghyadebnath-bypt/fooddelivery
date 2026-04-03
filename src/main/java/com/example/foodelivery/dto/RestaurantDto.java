package com.example.foodelivery.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RestaurantDto {
    private Long id;

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String address;

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal rating = BigDecimal.ZERO;

    private Long ownerId;

    private LocalDateTime createdAt;
}