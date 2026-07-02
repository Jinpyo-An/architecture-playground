package com.example.exercise.product.application.vector;

import java.util.Optional;

public interface ProductEmbeddingGenerator {

    Optional<float[]> generate(String text);
}
