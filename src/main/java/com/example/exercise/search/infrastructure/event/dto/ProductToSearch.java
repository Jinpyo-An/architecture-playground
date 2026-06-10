package com.example.exercise.search.infrastructure.event.dto;

public record ProductToSearch<T>(
        String eventType,
        T payload
) {
}
