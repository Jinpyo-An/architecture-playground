package com.example.exercise.order.infrastructure.persistence;

import com.example.exercise.order.domain.model.Order;
import com.example.exercise.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll();
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return orderJpaRepository.findById(id);
    }

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public List<Order> findUnsettledPaidOrders(LocalDateTime fromInclusive, LocalDateTime toExclusive) {
        return orderJpaRepository.findByStatusAndSettledFalseAndPaidAtGreaterThanEqualAndPaidAtLessThan(
                "PAID",
                fromInclusive,
                toExclusive
        );
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        return orderJpaRepository.findByOrderNo(orderNo);
    }

    @Override
    public List<Order> findTimedOutPaymentWaitOrders(LocalDateTime cutoffAt, int limit) {
        return orderJpaRepository.findTimedOutPaymentWaitOrders(cutoffAt, PageRequest.of(0, limit));
    }
}
