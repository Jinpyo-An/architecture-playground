package com.example.exercise.payment.infrastructure.event;

import com.example.exercise.order.application.dto.MarkOrderPaidCommand;
import com.example.exercise.order.application.usecase.OrderUseCase;
import com.example.exercise.payment.application.event.PaymentConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentIntegrationEventHandler {
    @Value("order-service")
    private String topicName;
    private final OrderUseCase orderUseCase;
    private final KafkaTemplate<String, MarkOrderPaidCommand> kafkaTemplate;
    //결제가 완료되었을때 이벤트를 받는 부분. 여기서 오더에 상태를 변경하는 usecase 함수를 실행해주면 됨.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentConfirmedEvent event) {
        //TODO: 카프카 메시징을 보내게되면 프로세스를 기다리지 않고 보내고 종료됨(모듈 분리시 카프카로 수정해야함).
        kafkaTemplate.send(topicName, event.orderId(), new MarkOrderPaidCommand(event.orderId(), event.approvedAt(), event.paymentId()))
                .whenComplete((result, throwable) -> {
                    log.info("sended kafka : {}, {},{}"
                            , result.getProducerRecord().value().orderNo()
                            , result.getProducerRecord().value().paidAt()
                            , result.getProducerRecord().value().paymentId());
                });

//        orderUseCase.markPaid(new MarkOrderPaidCommand(
//                event.orderId(),
//                event.approvedAt(),
//                event.paymentId()
//        ));
//        log.info("Payment confirmed event handled after commit. paymentId={}, orderId={}, approvedAt={}",
//                event.paymentId(),
//                event.orderId(),
//                event.approvedAt());
    }
}
