package com.example.exercise.search.presentation.controller;

import com.example.exercise.search.application.SearchKeywordUsecase;
import com.example.exercise.search.presentation.dto.response.PopularKeywordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인기검색어")
// 인기검색어 조회 전용 컨트롤러
@RestController
@RequestMapping("/api/search/keywords")
@RequiredArgsConstructor
public class SearchKeywordController {

    private final SearchKeywordUsecase searchKeywordUsecase;

    @Operation(
            summary = "인기검색어 조회",
            description = "최근 기간 기준으로 많이 검색된 키워드를 반환합니다."
    )
    @GetMapping("/popular")
    public PopularKeywordResponse getPopularKeywords(
            @Parameter(description = "조회 기간(일)", example = "7")
            @RequestParam(defaultValue = "7") int days,
            @Parameter(description = "최대 반환 개수", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        return searchKeywordUsecase.getPopularKeywords(days, size);
    }
}
