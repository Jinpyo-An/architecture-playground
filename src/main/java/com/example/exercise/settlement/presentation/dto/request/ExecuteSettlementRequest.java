package com.example.exercise.settlement.presentation.dto.request;

import com.example.exercise.settlement.application.dto.ExecuteSettlementCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "정산 실행 요청")
public record ExecuteSettlementRequest(
        @Schema(description = "정산 기준일", example = "2026-06-03")
        LocalDate settlementDate,
        @Schema(description = "요청 수행자 ID(UUID)")
        UUID actorId
) {

    public ExecuteSettlementCommand toCommand() {
        return new ExecuteSettlementCommand(settlementDate, actorId);
    }
}
