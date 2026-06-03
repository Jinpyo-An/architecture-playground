package com.example.exercise.service;

import java.util.List;
import java.util.UUID;

import com.example.exercise.dto.ProductCreateRequest;
import com.example.exercise.dto.ProductUpdateRequest;
import com.example.exercise.entity.Product;

public interface ProductService {

    Product create(ProductCreateRequest request);

    Product getById(UUID productId);

    List<Product> getAll();

    Product update(UUID productId, ProductUpdateRequest request);

    void delete(UUID productId);
}
