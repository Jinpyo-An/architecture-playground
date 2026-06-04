package com.example.exercise.order.infrastructure.persistence;

import com.example.exercise.order.domain.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

    List<Order> findByStatusAndSettledFalseAndPaidAtGreaterThanEqualAndPaidAtLessThan(
            String status,
            LocalDateTime fromInclusive,
            LocalDateTime toExclusive
    );

    Optional<Order> findByOrderNo(String orderNo);

    @Query("""
            select o from Order o
            where o.status = 'PAYMENT_WAIT'
              and o.regDt < :cutoffAt
            order by o.regDt asc
            """)
    List<Order> findTimedOutPaymentWaitOrders(
            @Param("cutoffAt") LocalDateTime cutoffAt,
            Pageable pageable
    );
}
