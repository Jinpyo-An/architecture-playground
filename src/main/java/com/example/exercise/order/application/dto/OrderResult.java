package com.example.exercise.order.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResult(
        UUID id,
        String orderNo,
        UUID buyerId,
        UUID sellerId,
        UUID productId,
        Integer quantity,
        BigDecimal grossAmount,
        BigDecimal feeAmount,
        BigDecimal refundAmount,
        BigDecimal netAmount,
        String status,
        LocalDateTime paidAt,
        Boolean settled,
        UUID settlementBatchId,
        UUID regId,
        LocalDateTime regDt,
        UUID modifyId,
        LocalDateTime modifyDt
) {
}
