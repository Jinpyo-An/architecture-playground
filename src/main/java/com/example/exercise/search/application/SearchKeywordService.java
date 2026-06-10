package com.example.exercise.search.application;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import com.example.exercise.search.infrastructure.SearchKeywordRepository;
import com.example.exercise.search.infrastructure.dto.SearchKeywordDocument;
import com.example.exercise.search.presentation.dto.response.PopularKeywordItemResponse;
import com.example.exercise.search.presentation.dto.response.PopularKeywordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 검색어 기록 저장과 인기검색어 집계를 담당
@Service
@RequiredArgsConstructor
public class SearchKeywordService implements SearchKeywordUsecase {

    private final ElasticsearchOperations operations;
    private final SearchKeywordRepository repository;

    @Override
    public void recordKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return;
        }

        String trimmed = keyword.trim();
        SearchKeywordDocument document = new SearchKeywordDocument(
                null,
                trimmed,
                trimmed.toLowerCase(),
                Instant.now()
        );
        repository.save(document);
    }

    @Override
    public PopularKeywordResponse getPopularKeywords(int days, int size) {
        // 최근 days일 범위로 date range 필터 + normalizedKeyword 기준 terms 집계
        String gte = "now-" + days + "d/d";

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.range(r -> r.date(d -> d
                        .field("searchedAt")
                        .gte(gte))))
                .withMaxResults(0) // 문서 본문은 필요 없고 집계 결과만 사용
                .withAggregation("popularKeywords", Aggregation.of(a -> a
                        .terms(t -> t
                                .field("normalizedKeyword")
                                .size(size))))
                .build();

        SearchHits<SearchKeywordDocument> hits = operations.search(query, SearchKeywordDocument.class);
        Map<String, Aggregate> aggregateMap = toAggregateMap(hits.getAggregations());

        List<PopularKeywordItemResponse> items = extractPopularKeywords(aggregateMap.get("popularKeywords"));
        return new PopularKeywordResponse(items);
    }

    private Map<String, Aggregate> toAggregateMap(AggregationsContainer<?> container) {
        if (!(container instanceof ElasticsearchAggregations esAggs)) {
            return Map.of();
        }
        Map<String, Aggregate> map = new HashMap<>();
        for (ElasticsearchAggregation aggregation : esAggs.aggregations()) {
            map.put(aggregation.aggregation().getName(), aggregation.aggregation().getAggregate());
        }
        return map;
    }

    // normalizedKeyword(StringTermsAggregate) 버킷을 응답 항목으로 변환
    private List<PopularKeywordItemResponse> extractPopularKeywords(Aggregate aggregate) {
        if (aggregate == null || !aggregate.isSterms()) {
            return List.of();
        }
        return aggregate.sterms().buckets().array().stream()
                .map(bucket -> new PopularKeywordItemResponse(
                        bucket.key().stringValue(),
                        bucket.docCount()))
                .toList();
    }
}
