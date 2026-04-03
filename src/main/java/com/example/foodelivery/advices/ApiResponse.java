package com.example.foodelivery.advices;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private ApiError apiError;
    private T data;

    public ApiResponse( ) {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(ApiError apiError) {
        this();
        this.apiError = apiError;
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
    }
}
