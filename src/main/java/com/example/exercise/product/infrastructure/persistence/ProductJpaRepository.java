package com.example.exercise.product.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.exercise.product.domain.model.Product;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {
}
