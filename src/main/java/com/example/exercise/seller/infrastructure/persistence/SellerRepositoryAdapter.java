package com.example.exercise.seller.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.exercise.seller.domain.model.Seller;
import com.example.exercise.seller.domain.repository.SellerRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SellerRepositoryAdapter implements SellerRepository {

    private final SellerJpaRepository sellerJpaRepository;

    @Override
    public Seller save(Seller seller) {
        return sellerJpaRepository.save(seller);
    }

    @Override
    public Optional<Seller> findById(UUID sellerId) {
        return sellerJpaRepository.findById(sellerId);
    }

    @Override
    public List<Seller> findAll() {
        return sellerJpaRepository.findAll();
    }

    @Override
    public void delete(Seller seller) {
        sellerJpaRepository.delete(seller);
    }
}
