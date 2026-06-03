package com.example.exercise.seller.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "판매자 생성 정보")
public record SellerCreateRequest(
        @Schema(description = "판매자 이메일", example = "seller@example.com")
        String email,
        @Schema(description = "판매자명", example = "홍길동 상점")
        String name,
        @Schema(description = "사업자 번호", example = "123-45-67890")
        String businessNumber,
        @Schema(description = "판매자 상태", example = "ACTIVE")
        String status,
        @Schema(description = "등록자 ID", example = "22222222-2222-2222-2222-222222222222")
        String creatorId
) {
}
