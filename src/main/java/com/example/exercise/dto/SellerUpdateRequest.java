package com.example.exercise.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "판매자 수정 정보")
public record SellerUpdateRequest(
        @Schema(description = "판매자 이메일", example = "seller@example.com")
        String email,
        @Schema(description = "판매자명", example = "홍길동 상점")
        String name,
        @Schema(description = "사업자 번호", example = "123-45-67890")
        String businessNumber,
        @Schema(description = "판매자 상태", example = "ACTIVE")
        String status,
        @Schema(description = "수정자 ID", example = "33333333-3333-3333-3333-333333333333")
        String modifierId
) {
}
