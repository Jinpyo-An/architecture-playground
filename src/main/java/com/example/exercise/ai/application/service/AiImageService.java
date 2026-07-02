package com.example.exercise.ai.application.service;

import com.example.exercise.ai.infrastructure.client.OpenAiImageClient;
import com.example.exercise.ai.presentation.dto.AiImageAnalyzeResponse;
import com.example.exercise.ai.presentation.dto.AiImageGenerateRequest;
import com.example.exercise.ai.presentation.dto.AiImageGenerateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AiImageService {

    private final OpenAiImageClient openAiImageClient;

    @Transactional(readOnly = true)
    public AiImageAnalyzeResponse analyze(MultipartFile image, String prompt) {
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "image is required");
        }

        String usedPrompt = normalizeAnalyzePrompt(prompt);
        String answer = openAiImageClient.analyzeImage(image, usedPrompt);
        return new AiImageAnalyzeResponse(usedPrompt, answer);
    }

    @Transactional(readOnly = true)
    public AiImageGenerateResponse generate(AiImageGenerateRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        }
        String usedPrompt = normalizeGeneratePrompt(request.prompt());
        String usedSize = normalizeSize(request.size());
        OpenAiImageClient.GeneratedImage generatedImage = openAiImageClient.generateImage(usedPrompt, usedSize);
        return new AiImageGenerateResponse(usedPrompt, generatedImage.format(), generatedImage.base64());
    }

    private String normalizeAnalyzePrompt(String prompt) {
        return (prompt == null || prompt.isBlank()) ? "이 이미지를 설명해줘" : prompt.trim();
    }

    private String normalizeGeneratePrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "prompt is required");
        }
        return prompt.trim();
    }

    private String normalizeSize(String size) {
        return (size == null || size.isBlank()) ? "1024x1024" : size.trim();
    }
}
