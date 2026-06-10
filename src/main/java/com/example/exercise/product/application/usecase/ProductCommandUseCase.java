package com.example.exercise.product.application.usecase;


import com.example.exercise.product.domain.model.Product;
import com.example.exercise.product.presentation.dto.ProductCreateRequest;
import com.example.exercise.product.presentation.dto.ProductUpdateRequest;

import java.util.UUID;

public interface ProductCommandUseCase {

    Product create(ProductCreateRequest request);

    Product update(UUID productId, ProductUpdateRequest request);

    void delete(UUID productId);
}
