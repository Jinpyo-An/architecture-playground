package com.example.exercise.product.infrastructure.vector;

import com.example.exercise.product.application.vector.ProductEmbeddingGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class OpenAiProductEmbeddingGenerator implements ProductEmbeddingGenerator {

    private static final String OPENAI_EMBEDDINGS_URL = "https://api.openai.com/v1/embeddings";

    private final RestClient restClient;

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.embedding.model:text-embedding-3-small}")
    private String model;

    @Override
    public Optional<float[]> generate(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }

        EmbeddingResponse response = restClient.post()
                .uri(OPENAI_EMBEDDINGS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(requireApiKey()))
                .body(Map.of(
                        "model", model,
                        "input", text
                ))
                .retrieve()
                .body(EmbeddingResponse.class);

        if (response == null || response.data() == null || response.data().isEmpty()) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "OpenAI embedding returned no data");
        }

        return Optional.of(response.data().get(0).embedding());
    }

    private String requireApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "OPENAI_API_KEY is required");
        }
        return apiKey;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record EmbeddingResponse(List<EmbeddingData> data) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record EmbeddingData(float[] embedding, int index, String object) {
    }
}
