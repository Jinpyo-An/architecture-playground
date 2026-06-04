package com.example.exercise.settlement.infrastructure.persistence;

import com.example.exercise.settlement.domain.model.SettlementBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettlementBatchJpaRepository extends JpaRepository<SettlementBatch, UUID> {
}
