package com.example.exercise.payment.application.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentConfirmedEvent(
        UUID paymentId,
        String orderId,
        String paymentKey,
        Long amount,
        LocalDateTime approvedAt
) {
}
