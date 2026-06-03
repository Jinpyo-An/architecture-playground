package com.example.exercise.seller.domain.model;

public record BusinessVerification(
        String businessNumber,
        String companyName,
        boolean valid
) {
}
