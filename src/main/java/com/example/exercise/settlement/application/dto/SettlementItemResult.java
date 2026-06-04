package com.example.exercise.settlement.application.dto;

import com.example.exercise.settlement.domain.model.SettlementItem;

import java.math.BigDecimal;
import java.util.UUID;

public record SettlementItemResult(
        UUID id,
        UUID orderId,
        String orderNo,
        UUID sellerId,
        BigDecimal grossAmount,
        BigDecimal feeAmount,
        BigDecimal refundAmount,
        BigDecimal settlementAmount
) {

    public static SettlementItemResult from(SettlementItem item) {
        return new SettlementItemResult(
                item.getId(),
                item.getOrderId(),
                item.getOrderNo(),
                item.getSellerId(),
                item.getGrossAmount(),
                item.getFeeAmount(),
                item.getRefundAmount(),
                item.getSettlementAmount()
        );
    }
}
