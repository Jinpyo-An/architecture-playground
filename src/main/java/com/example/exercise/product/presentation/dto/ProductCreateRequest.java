package com.example.exercise.product.presentation.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 생성 정보")
public record ProductCreateRequest(
        @Schema(description = "판매자 ID", example = "11111111-1111-1111-1111-111111111111")
        String sellerId,
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
        @Schema(description = "등록자 ID", example = "22222222-2222-2222-2222-222222222222")
        String creatorId
) {
}
