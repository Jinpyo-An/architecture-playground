package com.example.exercise.settlement.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SettlementOrder(
        UUID orderId,
        String orderNo,
        UUID sellerId,
        BigDecimal grossAmount,
        BigDecimal feeAmount,
        BigDecimal refundAmount,
        BigDecimal netAmount,
        LocalDateTime paidAt
) {
}
