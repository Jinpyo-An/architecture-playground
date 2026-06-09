package com.example.exercise.order.infrastructure.event;

import com.example.exercise.order.application.dto.MarkOrderPaidCommand;
import com.example.exercise.order.application.usecase.OrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class OrderKafkaEvent {
    private final OrderUseCase orderUseCase;
    @KafkaListener(
            topics = "order-service",
            groupId = "order-payment",
            containerFactory = "OrderKafkaContainerFactory"
//            , concurrency = "3"//partition 갯수에 맞춰서 하는게 좋음.
    )
    public void handle(MarkOrderPaidCommand event) {
        log.info("handle {}", event.orderNo());
        orderUseCase.markPaid(event);
    }
}
