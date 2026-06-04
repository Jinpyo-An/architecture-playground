package com.example.exercise.settlement.domain.repository;

import com.example.exercise.settlement.domain.model.SettlementBatch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementBatchRepository {

    SettlementBatch save(SettlementBatch settlementBatch);

    List<SettlementBatch> findAll();

    Optional<SettlementBatch> findById(UUID id);
}
