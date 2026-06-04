package com.example.exercise.payment.infrastructure.event;

import com.example.exercise.payment.application.event.PaymentConfirmedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class PaymentIntegrationEventHandler {
    //결제가 완료되었을때 이벤트를 받는 부분. 여기서 오더에 상태를 변경하는 usecase 함수를 실행해주면 됨.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentConfirmedEvent event) {
        log.info("Payment confirmed event handled after commit. paymentId={}, orderId={}, approvedAt={}",
                event.paymentId(),
                event.orderId(),
                event.approvedAt());
    }
}
