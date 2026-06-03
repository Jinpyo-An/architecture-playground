package com.example.exercise.payment.presentation;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exercise.payment.application.PaymentUseCase;
import com.example.exercise.payment.application.dto.PaymentFailureInfo;
import com.example.exercise.payment.application.dto.PaymentInfo;
import com.example.exercise.payment.presentation.dto.PaymentFailRequest;
import com.example.exercise.payment.presentation.dto.PaymentRequest;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.init}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private PaymentUseCase paymentUseCase;

    @Operation(summary = "결제 내역 조회", description = "확정된 결제 정보를 페이지 단위로 조회한다.")
    @GetMapping
    public ResponseEntity<List<PaymentInfo>> findAll(Pageable pageable) {
        return paymentUseCase.findAll(pageable);
    }

    @Operation(summary = "토스 결제 승인", description = "토스 결제 완료 후 paymentKey/orderId/amount를 전달받아 결제를 승인한다.")
    @PostMapping("/confirm")
    public ResponseEntity<PaymentInfo> confirm(@RequestBody PaymentRequest request) {
        return paymentUseCase.confirm(request.toCommand());
    }

    @Operation(summary = "결제 실패 기록", description = "토스 결제 실패 정보를 저장한다.")
    @PostMapping("/fail")
    public ResponseEntity<PaymentFailureInfo> fail(@RequestBody PaymentFailRequest request) {
        return paymentUseCase.recordFailure(request.toCommand());
    }
}
