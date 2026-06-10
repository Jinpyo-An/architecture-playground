package com.example.exercise.search.infrastructure;

import com.example.exercise.search.infrastructure.dto.SearchKeywordDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

// ElasticsearchRepository를 확장해 검색어 기록 CRUD/저장 기본 기능 제공
public interface SearchKeywordRepository extends ElasticsearchRepository<SearchKeywordDocument, String> {
}
