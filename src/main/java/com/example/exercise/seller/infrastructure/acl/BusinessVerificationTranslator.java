package com.example.exercise.seller.infrastructure.acl;

import com.example.exercise.seller.domain.model.BusinessVerification;
import org.springframework.stereotype.Component;

@Component
public class BusinessVerificationTranslator {

    public BusinessVerification translate(ExternalBusinessVerificationResponse response) {
        boolean valid = "NORMAL".equalsIgnoreCase(response.taxType());
        return new BusinessVerification(response.bizNo(), response.corpNm(), valid);
    }
}
