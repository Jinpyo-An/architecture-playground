package com.example.exercise.product.infrastructure.acl;

import com.example.exercise.product.domain.model.SellerValidation;
import com.example.exercise.seller.domain.model.Seller;
import org.springframework.stereotype.Component;

@Component
public class SellerValidationTranslator {

    public SellerValidation translate(Seller seller) {
        return new SellerValidation(
                seller.getId(),
                seller.getName(),
                seller.getStatus()
        );
    }
}
