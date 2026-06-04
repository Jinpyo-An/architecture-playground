package com.example.exercise.order.infrastructure.scheduler;

import com.example.exercise.order.application.dto.CancelTimedOutOrdersCommand;
import com.example.exercise.order.application.usecase.OrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelScheduler {

    private static final UUID SYSTEM_ACTOR = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final OrderUseCase orderUseCase;

    @Value("${order.auto-cancel.timeout-hours:24}")
    private long timeoutHours;

    @Value("${order.auto-cancel.batch-size:500}")
    private int batchSize;

    @Scheduled(cron = "${order.auto-cancel.cron:0 */10 * * * *}", zone = "Asia/Seoul")
    public void run() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(timeoutHours);
        int canceled = orderUseCase.cancelTimedOutOrders(
                new CancelTimedOutOrdersCommand(cutoff, SYSTEM_ACTOR, batchSize)
        );
        log.info("auto-cancel batch done: cutoffAt={} canceledCount={}", cutoff, canceled);
    }
}
