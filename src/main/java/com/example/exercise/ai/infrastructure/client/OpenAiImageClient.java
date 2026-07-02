package com.example.exercise.ai.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class OpenAiImageClient {

    private static final String OPENAI_RESPONSES_URL = "https://api.openai.com/v1/responses";
    private static final String OPENAI_IMAGES_URL = "https://api.openai.com/v1/images/generations";

    private final RestClient restClient;

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.image-analysis.model:gpt-5.4-nano}")
    private String imageAnalysisModel;

    @Value("${openai.image.model:gpt-image-1-mini}")
    private String imageModel;

    public String analyzeImage(MultipartFile image, String prompt) {
        AnalysisResponse response = restClient.post()
                .uri(OPENAI_RESPONSES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(requireApiKey()))
                .body(Map.of(
                        "model", imageAnalysisModel,
                        "input", List.of(
                                Map.of(
                                        "role", "user",
                                        "content", List.of(
                                                Map.of("type", "input_text", "text", prompt),
                                                Map.of("type", "input_image", "image_url", toDataUrl(image))
                                        )
                                )
                        )
                ))
                .retrieve()
                .body(AnalysisResponse.class);

        String answer = extractText(response);
        if (answer.isBlank()) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "OpenAI image analysis returned no text");
        }
        return answer;
    }

    public GeneratedImage generateImage(String prompt, String size) {
        GenerationResponse response = restClient.post()
                .uri(OPENAI_IMAGES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(requireApiKey()))
                .body(Map.of(
                        "model", imageModel,
                        "prompt", prompt,
                        "size", size,
                        "output_format", "png"
                ))
                .retrieve()
                .body(GenerationResponse.class);

        if (response == null || response.data() == null || response.data().isEmpty()) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "OpenAI image generation returned no data");
        }

        ImageData imageData = response.data().get(0);
        return new GeneratedImage(
                imageData.b64Json(),
                response.outputFormat() == null ? "png" : response.outputFormat()
        );
    }

    private String extractText(AnalysisResponse response) {
        if (response == null || response.output() == null) {
            return "";
        }

        for (OutputItem item : response.output()) {
            if (item.content() == null) {
                continue;
            }
            for (OutputContent content : item.content()) {
                if ("output_text".equals(content.type()) && content.text() != null) {
                    return content.text();
                }
            }
        }
        return "";
    }

    private String toDataUrl(MultipartFile image) {
        try {
            String contentType = image.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "image/png";
            }
            String base64 = Base64.getEncoder().encodeToString(image.getBytes());
            return "data:%s;base64,%s".formatted(contentType, base64);
        } catch (IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to read image", e);
        }
    }

    private String requireApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "OPENAI_API_KEY is required");
        }
        return apiKey;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AnalysisResponse(List<OutputItem> output) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OutputItem(List<OutputContent> content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OutputContent(
            @JsonProperty("type") String type,
            @JsonProperty("text") String text
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GenerationResponse(
            @JsonProperty("data") List<ImageData> data,
            @JsonProperty("output_format") String outputFormat
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ImageData(
            @JsonProperty("b64_json") String b64Json,
            @JsonProperty("revised_prompt") String revisedPrompt,
            @JsonProperty("url") String url
    ) {
    }

    public record GeneratedImage(String base64, String format) {
    }
}
