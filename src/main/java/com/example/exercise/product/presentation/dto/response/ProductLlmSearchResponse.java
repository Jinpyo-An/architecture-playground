package com.example.exercise.product.presentation.dto.response;

import com.example.exercise.product.domain.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "상품 LLM 검색 응답")
public record ProductLlmSearchResponse(
        @Schema(description = "검색 질문")
        String question,
        @Schema(description = "LLM 답변")
        String answer,
        @Schema(description = "검색된 상품 목록")
        List<Product> products
) {
}
