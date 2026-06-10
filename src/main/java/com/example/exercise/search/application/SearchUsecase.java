package com.example.exercise.search.application;

import com.example.exercise.search.infrastructure.dto.ProductDocument;
import com.example.exercise.search.presentation.dto.request.IndexConfigRequest;
import com.example.exercise.search.presentation.dto.request.ProductIndexRequest;
import com.example.exercise.search.presentation.dto.response.IndexStatusResponse;
import com.example.exercise.search.presentation.dto.response.IndexUpdateResponse;
import com.example.exercise.search.presentation.dto.response.ProductSearchResponse;
import com.example.exercise.search.presentation.dto.response.ProductSuggestResponse;
import org.springframework.data.domain.Pageable;

public interface SearchUsecase {
    ProductDocument indexProduct(ProductIndexRequest request);
    IndexUpdateResponse applyProductIndexConfig(IndexConfigRequest request);
    IndexStatusResponse getProductIndexStatus();
    ProductSearchResponse searchProducts(String keyword, String category, Pageable pageable);
    ProductSuggestResponse suggestProducts(String keyword, int size);
}
