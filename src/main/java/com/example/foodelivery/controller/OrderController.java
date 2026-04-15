package com.example.foodelivery.controller;

import com.example.foodelivery.advices.ApiResponse;
import com.example.foodelivery.dto.OrderDto;
import com.example.foodelivery.entity.User;
import com.example.foodelivery.service.OrderService;
import com.example.foodelivery.utils.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<OrderDto> placeOrder(@AuthenticationPrincipal User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new IllegalStateException("Authenticated user is required to place an order");
        }

        OrderDto orderDto = orderService.placeOrder(authenticatedUser.getId());
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderDto>>> getAll(@AuthenticationPrincipal User authenticatedUser,
                                                                      @RequestParam(defaultValue = "0") int pageNo,
                                                                      @RequestParam(defaultValue = "10") int pageSize) {
        if (authenticatedUser == null) {
            throw new IllegalStateException("Authenticated user is required to view orders");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        PageResponse<OrderDto> orderPageResponse = orderService.getAll(authenticatedUser.getId(), pageable);
        return new ResponseEntity<>(new ApiResponse<>(orderPageResponse), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@AuthenticationPrincipal User authenticatedUser,
                                            @PathVariable Long id) {
        if (authenticatedUser == null) {
            throw new IllegalStateException("Authenticated user is required to view orders");
        }

        OrderDto orderDto = orderService.getById(authenticatedUser.getId(), id);
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }
}