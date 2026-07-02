package com.example.exercise.product.application.llm;


import com.example.exercise.product.domain.model.Product;

import java.util.List;

public interface ProductLlmAnswerGenerator {

    String generateAnswer(String question, List<Product> products);
}
