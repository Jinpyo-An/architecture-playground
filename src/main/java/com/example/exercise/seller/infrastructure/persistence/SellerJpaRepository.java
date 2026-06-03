package com.example.exercise.seller.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.exercise.seller.domain.model.Seller;

public interface SellerJpaRepository extends JpaRepository<Seller, UUID> {
}
