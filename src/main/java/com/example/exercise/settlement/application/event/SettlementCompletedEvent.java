package com.example.exercise.settlement.application.event;

import java.util.List;
import java.util.UUID;

public record SettlementCompletedEvent(
        UUID settlementBatchId,
        List<UUID> orderIds,
        UUID actorId
) {
}
