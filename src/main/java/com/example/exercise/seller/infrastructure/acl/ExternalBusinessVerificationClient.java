package com.example.exercise.seller.infrastructure.acl;

import org.springframework.stereotype.Component;

@Component
public class ExternalBusinessVerificationClient {

    public ExternalBusinessVerificationResponse verify(String businessNumber) {
        return new ExternalBusinessVerificationResponse(
                businessNumber,
                "Verified Company",
                "NORMAL"
        );
    }
}
