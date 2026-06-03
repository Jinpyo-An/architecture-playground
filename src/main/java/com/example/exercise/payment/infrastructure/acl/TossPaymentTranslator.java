package com.example.exercise.payment.infrastructure.acl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.exercise.payment.application.dto.PaymentConfirmation;
import com.example.exercise.payment.client.dto.TossPaymentResponse;

@Component
public class TossPaymentTranslator {

    public PaymentConfirmation translate(TossPaymentResponse response) {
        LocalDateTime approvedAt = response.approvedAt() != null ? response.approvedAt().toLocalDateTime() : null;
        LocalDateTime requestedAt = response.requestedAt() != null ? response.requestedAt().toLocalDateTime() : null;

        return new PaymentConfirmation(
                response.paymentKey(),
                response.orderId(),
                response.totalAmount(),
                response.method(),
                approvedAt,
                requestedAt
        );
    }
}
