package com.example.exercise.ai.presentation.controller;

import com.example.exercise.ai.application.service.AiImageService;
import com.example.exercise.ai.presentation.dto.AiImageAnalyzeResponse;
import com.example.exercise.ai.presentation.dto.AiImageGenerateRequest;
import com.example.exercise.ai.presentation.dto.AiImageGenerateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "AI 이미지")
@RestController
@RequestMapping("${api.init}/ai/images")
@RequiredArgsConstructor
public class AiImageController {

    private final AiImageService aiImageService;

    @Operation(summary = "이미지 분석", description = "업로드한 이미지를 AI가 설명합니다.")
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AiImageAnalyzeResponse analyze(
            @Parameter(description = "분석할 이미지")
            @RequestPart("image") MultipartFile image,
            @Parameter(description = "선택 프롬프트")
            @RequestParam(required = false) String prompt
    ) {
        return aiImageService.analyze(image, prompt);
    }

    @Operation(summary = "이미지 생성", description = "텍스트 프롬프트를 바탕으로 이미지를 생성합니다.")
    @PostMapping("/generate")
    public AiImageGenerateResponse generate(@RequestBody AiImageGenerateRequest request) {
        return aiImageService.generate(request);
    }
}
