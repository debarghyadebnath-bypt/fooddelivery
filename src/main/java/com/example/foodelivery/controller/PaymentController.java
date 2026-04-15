package com.example.foodelivery.controller;

import com.example.foodelivery.dto.PaymentDto;
import com.example.foodelivery.entity.User;
import com.example.foodelivery.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<PaymentDto> confirmPayment(@AuthenticationPrincipal User authenticatedUser,
                                                     @PathVariable Long orderId) {
        if (authenticatedUser == null) {
            throw new IllegalStateException("Authenticated user is required to confirm a payment");
        }

        PaymentDto paymentDto = paymentService.confirmPayment(authenticatedUser.getId(), orderId);
        return new ResponseEntity<>(paymentDto, HttpStatus.OK);
    }
}