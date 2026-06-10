package com.example.exercise.product.application.event;

import java.util.UUID;

public record ProductDeletedEvent(UUID productId) {
}
