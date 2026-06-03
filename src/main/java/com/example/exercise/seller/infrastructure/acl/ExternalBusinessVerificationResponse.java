package com.example.exercise.seller.infrastructure.acl;

public record ExternalBusinessVerificationResponse(
        String bizNo,
        String corpNm,
        String taxType
) {
}
