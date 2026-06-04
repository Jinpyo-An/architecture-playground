package com.example.exercise.settlement.domain.model;

import java.math.BigDecimal;

public record SettlementItemTotals(
        BigDecimal totalGrossAmount,
        BigDecimal totalFeeAmount,
        BigDecimal totalRefundAmount,
        BigDecimal totalSettlementAmount
) {
}
