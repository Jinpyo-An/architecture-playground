package com.example.exercise.settlement.application.service;

import com.example.exercise.settlement.domain.model.SettlementBatch;
import com.example.exercise.settlement.domain.model.SettlementItem;
import com.example.exercise.settlement.domain.model.SettlementItemTotals;
import com.example.exercise.settlement.domain.repository.SettlementBatchRepository;
import com.example.exercise.settlement.domain.repository.SettlementItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementChunkService {

    private final SettlementBatchRepository settlementBatchRepository;
    private final SettlementItemRepository settlementItemRepository;

    @Transactional
    public UUID createBatch(LocalDate settlementDate, UUID actorId) {
        SettlementBatch batch = SettlementBatch.create(settlementDate, actorId);
        return settlementBatchRepository.save(batch).getId();
    }

    @Transactional
    public void saveItems(UUID batchId, List<SettlementItem> items) {
        SettlementBatch batch = findBatch(batchId);
        items.forEach(item -> item.assignBatch(batch));
        settlementItemRepository.saveAll(items);
    }

    @Transactional
    public void completeBatch(UUID batchId) {
        SettlementBatch batch = findBatch(batchId);
        SettlementItemTotals totals = settlementItemRepository.sumByBatchId(batchId);
        batch.completeTotals(totals);
    }

    private SettlementBatch findBatch(UUID batchId) {
        return settlementBatchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Settlement batch not found: " + batchId));
    }
}
