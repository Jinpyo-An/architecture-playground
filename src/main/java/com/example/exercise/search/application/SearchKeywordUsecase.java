package com.example.exercise.search.application;

import com.example.exercise.search.presentation.dto.response.PopularKeywordResponse;

// 검색어 기록과 인기검색어 집계의 진입점 인터페이스
public interface SearchKeywordUsecase {

    // 검색어 1건을 search-keywords 인덱스에 기록
    void recordKeyword(String keyword);

    // 최근 days일 동안의 인기검색어 상위 size개를 조회
    PopularKeywordResponse getPopularKeywords(int days, int size);
}
