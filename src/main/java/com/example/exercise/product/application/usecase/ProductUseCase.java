package com.example.exercise.product.application.usecase;

import java.util.List;
import java.util.UUID;

import com.example.exercise.product.domain.model.Product;
import com.example.exercise.product.presentation.dto.ProductCreateRequest;
import com.example.exercise.product.presentation.dto.ProductUpdateRequest;

public interface ProductUseCase {

    Product create(ProductCreateRequest request);

    Product getById(UUID productId);

    List<Product> getAll();

    Product update(UUID productId, ProductUpdateRequest request);

    void delete(UUID productId);
}
