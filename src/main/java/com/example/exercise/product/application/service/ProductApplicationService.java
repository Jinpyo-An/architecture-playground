package com.example.exercise.product.application.service;

import com.example.exercise.product.application.vector.ProductEmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

    private final ProductEmbeddingService productEmbeddingService;

    @Transactional
    public void refreshEmbeddings() {
        productEmbeddingService.refreshEmbeddings();
    }
}
