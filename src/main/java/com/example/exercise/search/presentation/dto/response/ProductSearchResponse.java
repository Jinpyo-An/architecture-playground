package com.example.exercise.search.presentation.dto.response;



import com.example.exercise.search.infrastructure.dto.ProductDocument;

import java.util.List;

// 검색 결과 총 건수와 문서 리스트를 단순 래핑
public record ProductSearchResponse(long total, List<ProductDocument> items) {
}
