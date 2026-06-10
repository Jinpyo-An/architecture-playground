package com.example.exercise.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
@Schema(description = "상품 수정 정보")
public record ProductUpdateRequest(
        @Schema(description = "상품명", example = "맥북 프로 14")
        String name,
        @Schema(description = "상품 설명", example = "M3 칩셋, 16GB RAM")
        String description,
        @Schema(description = "가격", example = "2590000")
        BigDecimal price,
        @Schema(description = "재고", example = "10")
        Integer stock,
        @Schema(description = "상태", example = "ACTIVE")
        String status,
        @Schema(description = "수정자 ID", example = "33333333-3333-3333-3333-333333333333")
        String modifierId
) {
}
