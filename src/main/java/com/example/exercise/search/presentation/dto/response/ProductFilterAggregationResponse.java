package com.example.exercise.search.presentation.dto.response;

import java.util.List;

public record ProductFilterAggregationResponse(
        List<ProductFilterBucketResponse> brands,
        List<ProductFilterBucketResponse> categories,
        List<ProductFilterBucketResponse> priceRanges
) {
}