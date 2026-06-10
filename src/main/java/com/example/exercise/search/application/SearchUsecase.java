package com.example.exercise.search.application;

import com.example.exercise.search.infrastructure.dto.ProductDocument;
import com.example.exercise.search.presentation.dto.request.IndexConfigRequest;
import com.example.exercise.search.presentation.dto.request.ProductIndexRequest;
import com.example.exercise.search.presentation.dto.response.*;
import org.springframework.data.domain.Pageable;

public interface SearchUsecase {
    ProductDocument indexProduct(ProductIndexRequest request);
    IndexUpdateResponse applyProductIndexConfig(IndexConfigRequest request);
    IndexStatusResponse getProductIndexStatus();
    ProductSearchResponse searchProducts(String keyword, String category, Pageable pageable);
    ProductSuggestResponse suggestProducts(String keyword, int size);
    ProductFilterAggregationResponse aggregateProductFilters(String keyword);
}
