package com.example.exercise.search.presentation.dto.response;

import java.util.List;

public record ProductSuggestResponse(
        List<ProductSuggestItemResponse> items
) {
}