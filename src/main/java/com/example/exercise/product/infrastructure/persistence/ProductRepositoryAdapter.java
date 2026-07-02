package com.example.exercise.product.infrastructure.persistence;

import java.util.*;

import com.pgvector.PGvector;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.exercise.product.domain.model.Product;
import com.example.exercise.product.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return productJpaRepository.findById(productId);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }

    @Override
    public void delete(Product product) {
        productJpaRepository.delete(product);
    }

    @Override
    public List<Product> findNearestProducts(float[] embedding, int limit) {
        if (embedding == null || embedding.length == 0 || limit <= 0) {
            return List.of();
        }

        String sql = """
                SELECT id
                FROM public."product"
                WHERE embedding IS NOT NULL
                ORDER BY embedding <=> ?
                LIMIT ?
                """;

        List<UUID> orderedIds = jdbcTemplate.query(
                connection -> {
                    var statement = connection.prepareStatement(sql);
                    statement.setObject(1, new PGvector(embedding));
                    statement.setInt(2, limit);
                    return statement;
                },
                (resultSet, rowNum) -> UUID.fromString(resultSet.getString("id"))
        );

        if (orderedIds.isEmpty()) {
            return List.of();
        }

        Map<UUID, Product> productMap = new java.util.LinkedHashMap<>();
        for (Product product : productJpaRepository.findAllById(orderedIds)) {
            productMap.put(product.getId(), product);
        }

        return orderedIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
