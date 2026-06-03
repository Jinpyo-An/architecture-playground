package com.example.exercise.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.exercise.entity.Product;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {
}
