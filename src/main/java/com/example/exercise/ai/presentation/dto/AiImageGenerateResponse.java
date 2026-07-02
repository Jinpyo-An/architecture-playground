package com.example.exercise.ai.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI 이미지 생성 응답")
public record AiImageGenerateResponse(
        @Schema(description = "사용한 프롬프트")
        String prompt,
        @Schema(description = "이미지 포맷")
        String format,
        @Schema(description = "Base64 인코딩 이미지")
        String imageBase64
) {
}
