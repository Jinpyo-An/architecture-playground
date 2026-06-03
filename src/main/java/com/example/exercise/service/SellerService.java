package com.example.exercise.service;

import java.util.List;
import java.util.UUID;

import com.example.exercise.dto.SellerCreateRequest;
import com.example.exercise.dto.SellerUpdateRequest;
import com.example.exercise.entity.Seller;

public interface SellerService {

    Seller create(SellerCreateRequest request);

    Seller getById(UUID sellerId);

    List<Seller> getAll();

    Seller update(UUID sellerId, SellerUpdateRequest request);

    void delete(UUID sellerId);
}
