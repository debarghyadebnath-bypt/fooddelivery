package com.example.foodelivery.controller;

import com.example.foodelivery.advices.ApiResponse;
import com.example.foodelivery.dto.OrderDto;
import com.example.foodelivery.dto.OrderStatusUpdateDto;
import com.example.foodelivery.entity.User;
import com.example.foodelivery.service.OrderService;
import com.example.foodelivery.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {
    private final OrderService orderService;

    public DeliveryController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderDto>>> getOrders(@AuthenticationPrincipal User authenticatedUser,
                                                                         @RequestParam(defaultValue = "0") int pageNo,
                                                                         @RequestParam(defaultValue = "10") int pageSize) {
        if (authenticatedUser == null) {
            throw new IllegalStateException("Authenticated user is required to view delivery orders");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        PageResponse<OrderDto> orderPageResponse = orderService.getDeliveryOrders(pageable);
        return new ResponseEntity<>(new ApiResponse<>(orderPageResponse), HttpStatus.OK);
    }

    @PutMapping("/order-status")
    public ResponseEntity<OrderDto> updateOrderStatus(@AuthenticationPrincipal User authenticatedUser,
                                                      @Valid @RequestBody OrderStatusUpdateDto orderStatusUpdateDto) {
        if (authenticatedUser == null) {
            throw new IllegalStateException("Authenticated user is required to update order status");
        }

        OrderDto orderDto = orderService.updateOrderStatus(orderStatusUpdateDto.getOrderId(), orderStatusUpdateDto.getOrderStatus());
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }
}