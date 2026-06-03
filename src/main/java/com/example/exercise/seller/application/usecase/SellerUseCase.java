package com.example.exercise.seller.application.usecase;

import java.util.List;
import java.util.UUID;

import com.example.exercise.seller.domain.model.Seller;
import com.example.exercise.seller.presentation.dto.SellerCreateRequest;
import com.example.exercise.seller.presentation.dto.SellerUpdateRequest;

public interface SellerUseCase {

    Seller create(SellerCreateRequest request);

    Seller getById(UUID sellerId);

    List<Seller> getAll();

    Seller update(UUID sellerId, SellerUpdateRequest request);

    void delete(UUID sellerId);
}
