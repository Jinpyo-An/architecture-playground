package com.example.exercise.search.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

// Elasticsearch 문서: 사용자가 입력한 검색어를 search-keywords 인덱스에 1건씩 기록
@Getter
@Document(indexName = "search-keywords")
public class SearchKeywordDocument {

    @Id
    private String id;

    // 사용자가 실제 입력한 원본 값
    @Field(type = FieldType.Keyword)
    private String keyword;

    // trim + lowercase 처리된 집계용 값
    @Field(type = FieldType.Keyword)
    private String normalizedKeyword;

    @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "uuuu-MM-dd'T'HH:mm:ssX")
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    private Instant searchedAt;

    public SearchKeywordDocument() {
    }

    public SearchKeywordDocument(String id, String keyword, String normalizedKeyword, Instant searchedAt) {
        this.id = id;
        this.keyword = keyword;
        this.normalizedKeyword = normalizedKeyword;
        this.searchedAt = searchedAt;
    }
}
