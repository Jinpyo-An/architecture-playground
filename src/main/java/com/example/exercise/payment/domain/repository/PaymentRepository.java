package com.example.exercise.payment.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.exercise.payment.domain.model.Payment;

public interface PaymentRepository {

    Page<Payment> findAll(Pageable pageable);

    Optional<Payment> findById(UUID id);

    Payment save(Payment payment);
}
