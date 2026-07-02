package com.example.exercise.product.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 LLM 검색 요청")
public record ProductLlmSearchRequest(
        @Schema(description = "검색 질문", example = "영상 편집용 노트북 추천해줘")
        String question,
        @Schema(description = "반환할 상품 수", example = "3")
        Integer size
) {
}
