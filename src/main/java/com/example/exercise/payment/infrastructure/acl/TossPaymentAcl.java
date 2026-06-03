package com.example.exercise.payment.infrastructure.acl;

import org.springframework.stereotype.Component;

import com.example.exercise.payment.application.dto.PaymentCommand;
import com.example.exercise.payment.application.dto.PaymentConfirmation;
import com.example.exercise.payment.client.TossPaymentClient;
import com.example.exercise.payment.client.dto.TossPaymentResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TossPaymentAcl {

    private final TossPaymentClient tossPaymentClient;
    private final TossPaymentTranslator tossPaymentTranslator;

    public PaymentConfirmation confirm(PaymentCommand command) {
        TossPaymentResponse response = tossPaymentClient.confirm(command);
        return tossPaymentTranslator.translate(response);
    }
}
