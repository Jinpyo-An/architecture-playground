package com.example.exercise.seller.infrastructure.acl;

import com.example.exercise.seller.domain.model.BusinessVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessVerificationAcl {

    private final ExternalBusinessVerificationClient externalBusinessVerificationClient;
    private final BusinessVerificationTranslator businessVerificationTranslator;

    public BusinessVerification verify(String businessNumber) {
        ExternalBusinessVerificationResponse response = externalBusinessVerificationClient.verify(businessNumber);
        return businessVerificationTranslator.translate(response);
    }
}
