package com.example.exercise.search.infrastructure;


import com.example.exercise.search.infrastructure.dto.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

// ElasticsearchRepository를 확장해 CRUD/검색 기본 기능 제공
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
}
