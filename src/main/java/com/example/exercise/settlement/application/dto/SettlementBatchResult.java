package com.example.exercise.settlement.application.dto;

import com.example.exercise.settlement.domain.model.SettlementBatch;
import com.example.exercise.settlement.domain.model.SettlementStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SettlementBatchResult(
        UUID id,
        UUID sellerId,
        LocalDate settlementDate,
        BigDecimal totalGrossAmount,
        BigDecimal totalFeeAmount,
        BigDecimal totalRefundAmount,
        BigDecimal totalSettlementAmount,
        SettlementStatus status,
        UUID regId,
        LocalDateTime regDt,
        List<SettlementItemResult> items
) {

    public static SettlementBatchResult from(SettlementBatch batch) {
        return new SettlementBatchResult(
                batch.getId(),
                batch.getSellerId(),
                batch.getSettlementDate(),
                batch.getTotalGrossAmount(),
                batch.getTotalFeeAmount(),
                batch.getTotalRefundAmount(),
                batch.getTotalSettlementAmount(),
                batch.getStatus(),
                batch.getRegId(),
                batch.getRegDt(),
                batch.getItems().stream()
                        .map(SettlementItemResult::from)
                        .toList()
        );
    }
}
