package com.example.exercise.settlement.domain.repository;


import com.example.exercise.settlement.domain.model.SettlementItem;
import com.example.exercise.settlement.domain.model.SettlementItemTotals;

import java.util.List;
import java.util.UUID;

public interface SettlementItemRepository {

    List<SettlementItem> saveAll(List<SettlementItem> items);

    SettlementItemTotals sumByBatchId(UUID batchId);
}
