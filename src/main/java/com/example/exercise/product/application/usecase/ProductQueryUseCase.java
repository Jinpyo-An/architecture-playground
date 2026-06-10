package com.example.exercise.product.application.usecase;


import com.example.exercise.product.domain.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductQueryUseCase {

    Product getById(UUID productId);

    List<Product> getAll();
}
