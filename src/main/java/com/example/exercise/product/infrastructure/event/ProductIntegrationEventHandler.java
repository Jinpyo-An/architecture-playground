package com.example.exercise.product.infrastructure.event;

import com.example.exercise.product.application.event.ProductCreatedEvent;
import com.example.exercise.product.application.event.ProductDeletedEvent;
import com.example.exercise.product.application.event.ProductUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class ProductIntegrationEventHandler {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProductCreatedEvent event) {
        log.info("Product created event handled after commit. productId={}", event.productId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProductUpdatedEvent event) {
        log.info("Product updated event handled after commit. productId={}", event.productId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProductDeletedEvent event) {
        log.info("Product deleted event handled after commit. productId={}", event.productId());
    }
}
