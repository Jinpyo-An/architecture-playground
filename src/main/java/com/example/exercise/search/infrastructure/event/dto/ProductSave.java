package com.example.exercise.search.infrastructure.event.dto;

import java.math.BigDecimal;

public record ProductSave(
        String id,
        String name,
        String brand,
        String category,
        BigDecimal price
) {
}
