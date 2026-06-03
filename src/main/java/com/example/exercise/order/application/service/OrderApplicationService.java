package com.example.exercise.order.application.service;

import com.example.exercise.order.application.dto.CreateOrderCommand;
import com.example.exercise.order.application.dto.OrderResult;
import com.example.exercise.order.application.usecase.OrderUseCase;
import com.example.exercise.order.domain.model.Order;
import com.example.exercise.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderApplicationService implements OrderUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResult create(CreateOrderCommand command) {
        UUID actorId = resolveActorId(command);

        Order order = Order.create(
                command.orderNo(),
                command.buyerId(),
                command.sellerId(),
                command.productId(),
                command.quantity(),
                command.grossAmount(),
                command.feeAmount(),
                command.refundAmount(),
                command.status(),
                command.paidAt(),
                actorId
        );

        return toResponse(orderRepository.save(order));
    }

    @Override
    public List<OrderResult> findAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OrderResult> findSettlementCandidates(LocalDate settlementDate) {
        LocalDate targetDate = settlementDate == null ? LocalDate.now() : settlementDate;
        LocalDateTime fromInclusive = targetDate.atStartOfDay();
        LocalDateTime toExclusive = fromInclusive.plusDays(1);

        return orderRepository.findUnsettledPaidOrders(fromInclusive, toExclusive).stream()
                .map(this::toResponse)
                .toList();
    }

    private UUID resolveActorId(CreateOrderCommand command) {
        if (command.actorId() != null) {
            return command.actorId();
        }
        if (command.buyerId() != null) {
            return command.buyerId();
        }
        return UUID.randomUUID();
    }

    private OrderResult toResponse(Order order) {
        return new OrderResult(
                order.getId(),
                order.getOrderNo(),
                order.getBuyerId(),
                order.getSellerId(),
                order.getProductId(),
                order.getQuantity(),
                order.getGrossAmount(),
                order.getFeeAmount(),
                order.getRefundAmount(),
                order.getNetAmount(),
                order.getStatus(),
                order.getPaidAt(),
                order.getSettled(),
                order.getSettlementBatchId(),
                order.getRegId(),
                order.getRegDt(),
                order.getModifyId(),
                order.getModifyDt()
        );
    }
}
