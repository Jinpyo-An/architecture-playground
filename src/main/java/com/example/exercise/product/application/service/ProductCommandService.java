package com.example.exercise.product.application.service;

import com.example.exercise.product.application.acl.SellerValidationAcl;
import com.example.exercise.product.application.event.ProductCreatedEvent;
import com.example.exercise.product.application.event.ProductDeletedEvent;
import com.example.exercise.product.application.event.ProductUpdatedEvent;
import com.example.exercise.product.application.usecase.ProductCommandUseCase;
import com.example.exercise.product.domain.model.Product;
import com.example.exercise.product.domain.model.SellerValidation;
import com.example.exercise.product.domain.repository.ProductRepository;
import com.example.exercise.product.presentation.dto.ProductCreateRequest;
import com.example.exercise.product.presentation.dto.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService implements ProductCommandUseCase {

    private final ProductRepository productRepository;
    private final SellerValidationAcl sellerValidationAcl;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Product create(ProductCreateRequest request) {
        SellerValidation sellerValidation = sellerValidationAcl.validate(toUuid(request.sellerId(), "sellerId"));
        if (!sellerValidation.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller is not active");
        }
        Product product = Product.create(
                sellerValidation.sellerId(),
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.status(),
                toUuid(request.creatorId(), "creatorId")
        );
        Product savedProduct = productRepository.save(product);
        eventPublisher.publishEvent(new ProductCreatedEvent(savedProduct.getId()));
        return savedProduct;
    }

    @Override
    public Product update(UUID productId, ProductUpdateRequest request) {
        Product product = findByIdOrThrow(productId);
        product.update(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.status(),
                toUuid(request.modifierId(), "modifierId")
        );
        eventPublisher.publishEvent(new ProductUpdatedEvent(product.getId()));
        return product;
    }

    @Override
    public void delete(UUID productId) {
        Product product = findByIdOrThrow(productId);
        productRepository.delete(product);
        eventPublisher.publishEvent(new ProductDeletedEvent(productId));
    }

    private Product findByIdOrThrow(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private UUID toUuid(String value, String fieldName) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be valid UUID");
        }
    }
}
