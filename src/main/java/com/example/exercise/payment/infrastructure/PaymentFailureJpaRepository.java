package com.example.exercise.payment.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.exercise.payment.domain.model.PaymentFailure;

public interface PaymentFailureJpaRepository extends JpaRepository<PaymentFailure, UUID> {
}
