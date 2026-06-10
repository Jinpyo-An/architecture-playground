package com.example.exercise.search.presentation.dto.response;

// 인기검색어 항목 1건(키워드 + 검색 횟수)
public record PopularKeywordItemResponse(
        String keyword,
        long count
) {
}
