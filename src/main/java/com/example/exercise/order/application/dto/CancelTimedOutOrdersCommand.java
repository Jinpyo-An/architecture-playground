package com.example.exercise.order.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CancelTimedOutOrdersCommand(
        LocalDateTime cutoffAt,
        UUID actorId,
        int batchSize
) {
}
