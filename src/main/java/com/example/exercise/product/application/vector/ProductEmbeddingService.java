package com.example.exercise.product.application.vector;

import com.example.exercise.product.domain.model.Product;
import com.example.exercise.product.domain.repository.ProductRepository;
import com.example.exercise.product.infrastructure.vector.NoOpProductEmbeddingGenerator;
import com.example.exercise.product.infrastructure.vector.OpenAiProductEmbeddingGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductEmbeddingService {

    private final ProductRepository productRepository;
    private final OpenAiProductEmbeddingGenerator openAiProductEmbeddingGenerator;
    private final NoOpProductEmbeddingGenerator noOpProductEmbeddingGenerator;

    @Value("${openai.embedding.enabled:false}")
    private boolean embeddingEnabled;

    public Optional<float[]> embed(String text) {
        return currentGenerator().generate(text);
    }

    public void applyEmbedding(Product product) {
        String source = buildSourceText(product);
        if (source.isBlank()) {
            return;
        }

        currentGenerator().generate(source)
                .ifPresent(product::updateEmbedding);
    }

    @Transactional
    public int refreshEmbeddings() {
        int updatedCount = 0;
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            float[] before = product.getEmbedding();
            applyEmbedding(product);
            if (!sameEmbedding(before, product.getEmbedding())) {
                productRepository.save(product);
                updatedCount++;
            }
        }

        return updatedCount;
    }

    private ProductEmbeddingGenerator currentGenerator() {
        return embeddingEnabled ? openAiProductEmbeddingGenerator : noOpProductEmbeddingGenerator;
    }

    private String buildSourceText(Product product) {
        return Arrays.stream(new String[]{product.getName(), product.getDescription()})
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining(" "));
    }

    private boolean sameEmbedding(float[] before, float[] after) {
        if (before == null) {
            return after == null;
        }
        return after != null && Arrays.equals(before, after);
    }
}
