package com.example.exercise.product.application.service;

import com.example.exercise.product.application.vector.ProductEmbeddingService;
import com.example.exercise.product.domain.model.Product;
import com.example.exercise.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

    private final ProductRepository productRepository;
    private final ProductEmbeddingService productEmbeddingService;

    public List<Product> semanticSearch(String query, int size) {
        int limit = normalizeSize(size);
        Optional<float[]> queryEmbedding = productEmbeddingService.embed(query);

        if (queryEmbedding.isPresent()) {
            List<Product> nearestProducts = productRepository.findNearestProducts(queryEmbedding.get(), limit);
            if (!nearestProducts.isEmpty()) {
                return nearestProducts;
            }
        }

        return lexicalSearch(query, limit);
    }

    private List<Product> lexicalSearch(String query, int limit) {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return List.of();
        }

        return products.stream()
                .sorted(Comparator
                        .comparingDouble((Product product) -> lexicalScore(product, query))
                        .reversed()
                        .thenComparing(Product::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .limit(limit)
                .toList();
    }

    private double lexicalScore(Product product, String query) {
        if (query == null || query.isBlank()) {
            return 0.0;
        }

        String haystack = ((product.getName() == null ? "" : product.getName()) + " "
                + (product.getDescription() == null ? "" : product.getDescription()))
                .toLowerCase(Locale.ROOT);
        String needle = query.toLowerCase(Locale.ROOT).trim();

        if (haystack.contains(needle)) {
            return 1.0;
        }

        Set<String> tokens = tokenize(needle);
        if (tokens.isEmpty()) {
            return 0.0;
        }

        long matches = tokens.stream().filter(haystack::contains).count();
        return (double) matches / tokens.size();
    }

    private int normalizeSize(int size) {
        return size <= 0 ? 5 : size;
    }

    private Set<String> tokenize(String text) {
        return Arrays.stream(text.split("\\s+"))
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .collect(Collectors.toSet());
    }

    @Transactional
    public void refreshEmbeddings() {
        productEmbeddingService.refreshEmbeddings();
    }
}
