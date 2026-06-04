package com.example.exercise.settlement.presentation.controller;

import com.example.exercise.settlement.application.usecase.SettlementUseCase;
import com.example.exercise.settlement.presentation.dto.request.ExecuteSettlementRequest;
import com.example.exercise.settlement.presentation.dto.response.SettlementBatchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.init}/settlements")
@RequiredArgsConstructor
@Tag(name = "Settlement", description = "정산 API")
public class SettlementController {

    private final SettlementUseCase settlementUseCase;

    @PostMapping
    @Operation(summary = "정산 실행", description = "정산 기준일의 미정산 PAID 주문을 정산합니다.")
    public ResponseEntity<SettlementBatchResponse> execute(@RequestBody ExecuteSettlementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SettlementBatchResponse.from(settlementUseCase.execute(request.toCommand())));
    }

    @GetMapping
    @Operation(summary = "정산 목록 조회", description = "생성된 정산 배치를 조회합니다.")
    public ResponseEntity<List<SettlementBatchResponse>> findAll() {
        return ResponseEntity.ok(settlementUseCase.findAll().stream()
                .map(SettlementBatchResponse::from)
                .toList());
    }
}
