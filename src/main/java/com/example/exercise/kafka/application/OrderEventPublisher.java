package com.example.exercise.kafka.application;

import com.example.exercise.kafka.dto.OrderDispatchResult;
import com.example.exercise.kafka.dto.OrderEvent;
import com.example.exercise.kafka.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
//@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final Clock clock;
    @Value("${kafka.topic.async-orders:async-orders}")
    private String topicName;

    public CompletableFuture<OrderDispatchResult> publish(OrderRequest request) {
        // HTTP 요청에서 받은 정보를 그대로 카프카 이벤트로 변환
        OrderEvent event = new OrderEvent(
                request.orderId(),
                request.memberId(),
                request.totalAmount(),
                request.itemSkus(),
                Instant.now(clock)
        );
        CompletableFuture<OrderDispatchResult> future = new CompletableFuture<>();
        // KafkaTemplate이 반환한 future를 CompletableFuture로 감싸 비동기 응답을 만든다.
        kafkaTemplate.send(topicName, event.orderId()+"1", event)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to dispatch async order event {}", event.orderId(), throwable);
                        future.completeExceptionally(throwable);
                        return;
                    }
                    if (result == null) {
                        future.completeExceptionally(new IllegalStateException("Kafka send returned null result"));
                        return;
                    }
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.info("Async order {} dispatched to {}-{}@{}", event.orderId(), metadata.topic(),
                            metadata.partition(), metadata.offset());
                    future.complete(new OrderDispatchResult(
                            event.orderId(),
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset()
                    ));
                });
        OrderEvent event2 = new OrderEvent(
                request.orderId(),
                request.memberId(),
                request.totalAmount(),
                request.itemSkus(),
                Instant.now(clock)
        );
        kafkaTemplate.send(topicName, event2.orderId()+"2", event2)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to dispatch async order event {}", event2.orderId(), throwable);
                        future.completeExceptionally(throwable);
                        return;
                    }
                    if (result == null) {
                        future.completeExceptionally(new IllegalStateException("Kafka send returned null result"));
                        return;
                    }
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.info("Async order {} dispatched to {}-{}@{}", event2.orderId(), metadata.topic(),
                            metadata.partition(), metadata.offset());
                    future.complete(new OrderDispatchResult(
                            event2.orderId(),
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset()
                    ));});
        OrderEvent event1 = new OrderEvent(
                request.orderId(),
                request.memberId(),
                request.totalAmount(),
                request.itemSkus(),
                Instant.now(clock)
        );
        kafkaTemplate.send(topicName, event1.orderId()+"3", event1)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to dispatch async order event {}", event1.orderId(), throwable);
                        future.completeExceptionally(throwable);
                        return;
                    }
                    if (result == null) {
                        future.completeExceptionally(new IllegalStateException("Kafka send returned null result"));
                        return;
                    }
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.info("Async order {} dispatched to {}-{}@{}", event1.orderId(), metadata.topic(),
                            metadata.partition(), metadata.offset());
                    future.complete(new OrderDispatchResult(
                            event1.orderId(),
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset()
                    ));
                });
        return future;
    }
}
