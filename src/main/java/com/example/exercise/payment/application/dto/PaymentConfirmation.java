package com.example.exercise.payment.application.dto;

import java.time.LocalDateTime;

public record PaymentConfirmation(
        String paymentKey,
        String orderId,
        Long totalAmount,
        String method,
        LocalDateTime approvedAt,
        LocalDateTime requestedAt
) {
}
