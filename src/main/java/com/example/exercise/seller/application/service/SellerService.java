package com.example.exercise.seller.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.exercise.seller.application.usecase.SellerUseCase;
import com.example.exercise.seller.domain.model.Seller;
import com.example.exercise.seller.domain.repository.SellerRepository;
import com.example.exercise.seller.presentation.dto.SellerCreateRequest;
import com.example.exercise.seller.presentation.dto.SellerUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SellerService implements SellerUseCase {

    private final SellerRepository sellerRepository;

    @Override
    @Transactional
    public Seller create(SellerCreateRequest request) {
        Seller seller = Seller.create(
                request.email(),
                request.name(),
                request.businessNumber(),
                request.status(),
                toUuid(request.creatorId(), "creatorId")
        );
        return sellerRepository.save(seller);
    }

    @Override
    public Seller getById(UUID sellerId) {
        return findByIdOrThrow(sellerId);
    }

    @Override
    public List<Seller> getAll() {
        return sellerRepository.findAll();
    }

    @Override
    @Transactional
    public Seller update(UUID sellerId, SellerUpdateRequest request) {
        Seller seller = findByIdOrThrow(sellerId);
        seller.update(
                request.email(),
                request.name(),
                request.businessNumber(),
                request.status(),
                toUuid(request.modifierId(), "modifierId")
        );
        return seller;
    }

    @Override
    @Transactional
    public void delete(UUID sellerId) {
        Seller seller = findByIdOrThrow(sellerId);
        sellerRepository.delete(seller);
    }

    private Seller findByIdOrThrow(UUID sellerId) {
        return sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));
    }

    private UUID toUuid(String value, String fieldName) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be valid UUID");
        }
    }
}
