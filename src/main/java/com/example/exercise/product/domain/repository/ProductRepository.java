package com.example.exercise.product.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.exercise.product.domain.model.Product;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID productId);

    List<Product> findAll();

    void delete(Product product);
}
