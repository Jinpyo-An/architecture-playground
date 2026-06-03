package com.example.exercise.order.infrastructure.persistence;

import com.example.exercise.order.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

    List<Order> findByStatusAndSettledFalseAndPaidAtGreaterThanEqualAndPaidAtLessThan(
            String status,
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive
    );
}
