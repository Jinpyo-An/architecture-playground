package com.example.exercise.settlement.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "정산 배치잡 실행 요청")
public record RunSettlementJobRequest(
        @Schema(description = "정산 기준일", example = "2026-06-03")
        LocalDate settlementDate,
        @Schema(description = "요청 수행자 ID(UUID)")
        UUID actorId
) {
}
