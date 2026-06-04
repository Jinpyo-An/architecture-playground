package com.example.exercise.settlement.infrastructure.acl;

import com.example.exercise.order.domain.model.Order;
import com.example.exercise.settlement.domain.model.SettlementOrder;
import org.springframework.stereotype.Component;

@Component
public class SettlementOrderTranslator {

    public SettlementOrder translate(Order order) {
        return new SettlementOrder(
                order.getId(),
                order.getOrderNo(),
                order.getSellerId(),
                order.getGrossAmount(),
                order.getFeeAmount(),
                order.getRefundAmount(),
                order.getNetAmount(),
                order.getPaidAt()
        );
    }
}
