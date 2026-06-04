package com.example.exercise.settlement.infrastructure.batch;

import com.example.exercise.settlement.application.dto.ExecuteSettlementCommand;
import com.example.exercise.settlement.application.usecase.SettlementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class SettlementJobConfig {

    public static final String SETTLEMENT_JOB_NAME = "settlementJob";
    public static final String SETTLEMENT_STEP_NAME = "settlementStep";

    private final SettlementUseCase settlementUseCase;

    @Bean
    public Job settlementJob(JobRepository jobRepository, Step settlementStep) {
        return new JobBuilder(SETTLEMENT_JOB_NAME, jobRepository)
                .start(settlementStep)
                .build();
    }

    @Bean
    public Step settlementStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder(SETTLEMENT_STEP_NAME, jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Map<String, Object> parameters = chunkContext.getStepContext().getJobParameters();
                    settlementUseCase.execute(new ExecuteSettlementCommand(
                            parseSettlementDate(parameters.get("settlementDate")),
                            parseActorId(parameters.get("actorId"))
                    ));
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    private LocalDate parseSettlementDate(Object value) {
        if (value == null) {
            return LocalDate.now();
        }
        return LocalDate.parse(value.toString());
    }

    private UUID parseActorId(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        return UUID.fromString(value.toString());
    }
}
