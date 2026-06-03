package com.example.exercise.payment.application;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.example.exercise.payment.application.dto.PaymentCommand;
import com.example.exercise.payment.application.dto.PaymentFailCommand;
import com.example.exercise.payment.application.dto.PaymentFailureInfo;
import com.example.exercise.payment.application.dto.PaymentInfo;

public interface PaymentUseCase {
    ResponseEntity<List<PaymentInfo>> findAll(Pageable pageable);
    ResponseEntity<PaymentInfo> confirm(PaymentCommand command);
    ResponseEntity<PaymentFailureInfo> recordFailure(PaymentFailCommand command);
}
