package com.example.exercise.seller.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.exercise.seller.domain.model.Seller;

public interface SellerRepository {

    Seller save(Seller seller);

    Optional<Seller> findById(UUID sellerId);

    List<Seller> findAll();

    void delete(Seller seller);
}
