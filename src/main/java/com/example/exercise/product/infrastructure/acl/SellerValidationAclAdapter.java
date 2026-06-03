package com.example.exercise.product.infrastructure.acl;

import com.example.exercise.product.application.acl.SellerValidationAcl;
import com.example.exercise.product.domain.model.SellerValidation;
import com.example.exercise.seller.domain.model.Seller;
import com.example.exercise.seller.domain.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SellerValidationAclAdapter implements SellerValidationAcl {

    private final SellerRepository sellerRepository;
    private final SellerValidationTranslator sellerValidationTranslator;

    @Override
    public SellerValidation validate(UUID sellerId) {
        //외부의 모듈에서 호출해서 데이터를 가져오는 과정으로 수정할 수 있음.
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));
        return sellerValidationTranslator.translate(seller);
    }
}
