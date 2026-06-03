package com.example.exercise.payment.infrastructure;

import org.springframework.stereotype.Repository;

import com.example.exercise.payment.domain.model.PaymentFailure;
import com.example.exercise.payment.domain.repository.PaymentFailureRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentFailureRepositoryAdapter implements PaymentFailureRepository {

    private final PaymentFailureJpaRepository repository;

    @Override
    public PaymentFailure save(PaymentFailure failure) {
        return repository.save(failure);
    }
}
