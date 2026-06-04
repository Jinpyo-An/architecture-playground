package com.example.exercise.settlement.presentation.dto.response;

import com.example.exercise.settlement.application.dto.SettlementItemResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "정산 항목 응답")
public record SettlementItemResponse(
        @Schema(description = "정산 항목 ID(UUID)")
        UUID id,
        @Schema(description = "주문 ID(UUID)")
        UUID orderId,
        @Schema(description = "주문 번호")
        String orderNo,
        @Schema(description = "판매자 ID(UUID)")
        UUID sellerId,
        @Schema(description = "주문 금액")
        BigDecimal grossAmount,
        @Schema(description = "수수료 금액")
        BigDecimal feeAmount,
        @Schema(description = "환불 금액")
        BigDecimal refundAmount,
        @Schema(description = "정산 금액")
        BigDecimal settlementAmount
) {

    public static SettlementItemResponse from(SettlementItemResult result) {
        return new SettlementItemResponse(
                result.id(),
                result.orderId(),
                result.orderNo(),
                result.sellerId(),
                result.grossAmount(),
                result.feeAmount(),
                result.refundAmount(),
                result.settlementAmount()
        );
    }
}
