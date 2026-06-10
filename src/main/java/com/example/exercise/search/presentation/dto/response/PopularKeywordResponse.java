package com.example.exercise.search.presentation.dto.response;

import java.util.List;

// 인기검색어 목록 응답
public record PopularKeywordResponse(
        List<PopularKeywordItemResponse> items
) {
}
