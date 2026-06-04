package com.example.exercise.settlement.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementBatchScheduler {

    private final JobOperator jobOperator;
    private final Job settlementJob;

    @Value("${settlement.batch.actor-id:00000000-0000-0000-0000-000000000000}")
    private UUID actorId;

    @Scheduled(cron = "${settlement.batch.cron}")
    public void run() {
        LocalDate settlementDate = LocalDate.now().minusDays(1);

        try {
            JobExecution jobExecution = jobOperator.start(settlementJob, new JobParametersBuilder()
                    .addString("jobName", SettlementJobConfig.SETTLEMENT_JOB_NAME)
                    .addString("settlementDate", settlementDate.toString())
                    .addString("actorId", actorId.toString())
                    .addString("requestedAt", LocalDateTime.now().toString())
                    .toJobParameters());

            log.info("Settlement batch job started. jobExecutionId={}, settlementDate={}",
                    jobExecution.getId(),
                    settlementDate);
        } catch (Exception e) {
            log.error("Settlement batch job failed. settlementDate={}", settlementDate, e);
        }
    }
}
