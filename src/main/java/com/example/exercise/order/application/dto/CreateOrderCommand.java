package com.example.exercise.order.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOrderCommand(
        String orderNo,
        UUID buyerId,
        UUID sellerId,
        UUID productId,
        Integer quantity,
        BigDecimal grossAmount,
        BigDecimal feeAmount,
        BigDecimal refundAmount,
        String status,
        LocalDateTime paidAt,
        UUID actorId
) {
}
