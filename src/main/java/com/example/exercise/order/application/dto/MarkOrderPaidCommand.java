package com.example.exercise.order.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MarkOrderPaidCommand(
        String orderNo,
        LocalDateTime paidAt,
        UUID paymentId
) {
}
