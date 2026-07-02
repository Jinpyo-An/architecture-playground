package com.example.exercise.ai.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI 이미지 생성 요청")
public record AiImageGenerateRequest(
        @Schema(description = "이미지 생성 프롬프트", example = "노을지는 바다를 바라보는 흰 고양이")
        String prompt,
        @Schema(description = "이미지 크기", example = "1024x1024")
        String size
) {
}
