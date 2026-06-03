package com.example.exercise.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.exercise.entity.Seller;

public interface SellerJpaRepository extends JpaRepository<Seller, UUID> {
}
