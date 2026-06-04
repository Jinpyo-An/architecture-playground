package com.example.exercise.settlement.presentation.dto.response;

import com.example.exercise.settlement.application.dto.SettlementBatchResult;
import com.example.exercise.settlement.domain.model.SettlementStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "정산 배치 응답")
public record SettlementBatchResponse(
        @Schema(description = "정산 배치 ID(UUID)")
        UUID id,
        @Schema(description = "정산 기준일")
        LocalDate settlementDate,
        @Schema(description = "총 주문 금액")
        BigDecimal totalGrossAmount,
        @Schema(description = "총 수수료 금액")
        BigDecimal totalFeeAmount,
        @Schema(description = "총 환불 금액")
        BigDecimal totalRefundAmount,
        @Schema(description = "총 정산 금액")
        BigDecimal totalSettlementAmount,
        @Schema(description = "정산 상태")
        SettlementStatus status,
        @Schema(description = "생성자 ID(UUID)")
        UUID regId,
        @Schema(description = "생성 일시")
        LocalDateTime regDt,
        @Schema(description = "정산 항목")
        List<SettlementItemResponse> items
) {

    public static SettlementBatchResponse from(SettlementBatchResult result) {
        return new SettlementBatchResponse(
                result.id(),
                result.settlementDate(),
                result.totalGrossAmount(),
                result.totalFeeAmount(),
                result.totalRefundAmount(),
                result.totalSettlementAmount(),
                result.status(),
                result.regId(),
                result.regDt(),
                result.items().stream()
                        .map(SettlementItemResponse::from)
                        .toList()
        );
    }
}
