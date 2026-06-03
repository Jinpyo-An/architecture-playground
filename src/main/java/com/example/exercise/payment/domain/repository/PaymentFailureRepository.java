package com.example.exercise.payment.domain.repository;

import com.example.exercise.payment.domain.model.PaymentFailure;

public interface PaymentFailureRepository {

    PaymentFailure save(PaymentFailure failure);
}
