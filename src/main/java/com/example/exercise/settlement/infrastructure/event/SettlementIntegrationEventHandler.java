package com.example.exercise.settlement.infrastructure.event;

import com.example.exercise.order.application.dto.MarkOrdersSettledCommand;
import com.example.exercise.order.application.usecase.OrderUseCase;
import com.example.exercise.settlement.application.event.SettlementCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementIntegrationEventHandler {

    private final OrderUseCase orderUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SettlementCompletedEvent event) {
        orderUseCase.markSettled(new MarkOrdersSettledCommand(
                event.settlementBatchId(),
                event.orderIds(),
                event.actorId()
        ));
        log.info("Settlement completed event handled after commit. settlementBatchId={}, orderCount={}",
                event.settlementBatchId(),
                event.orderIds().size());
    }
}
