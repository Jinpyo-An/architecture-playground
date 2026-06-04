package com.example.exercise.settlement.infrastructure.batch;

import com.example.exercise.order.domain.model.Order;
import com.example.exercise.settlement.application.service.SettlementChunkService;
import com.example.exercise.settlement.domain.model.SettlementItem;
import com.example.exercise.settlement.infrastructure.acl.SettlementOrderTranslator;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.infrastructure.repeat.policy.SimpleCompletionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class SettlementChunkJobConfig {

    public static final String SETTLEMENT_CHUNK_JOB_NAME = "settlementChunkJob";
    public static final String SETTLEMENT_CHUNK_STEP_NAME = "settlementChunkStep";

    private static final int CHUNK_SIZE = 1000;

    private final EntityManagerFactory entityManagerFactory;
    private final SettlementOrderTranslator settlementOrderTranslator;
    private final SettlementChunkService settlementChunkService;

    @Bean
    public Job settlementChunkJob(JobRepository jobRepository,
                                  Step settlementChunkStep) {
        return new JobBuilder(SETTLEMENT_CHUNK_JOB_NAME, jobRepository)
                .start(settlementChunkStep)
                .build();
    }

    @Bean
    @SuppressWarnings("removal")
    public Step settlementChunkStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    JpaPagingItemReader<Order> settlementOrderReader,
                                    ItemProcessor<Order, SettlementItem> settlementItemProcessor,
                                    ItemWriter<SettlementItem> settlementItemWriter,
                                    StepExecutionListener settlementChunkStepListener) {
        return new StepBuilder(SETTLEMENT_CHUNK_STEP_NAME, jobRepository)
                .<Order, SettlementItem>chunk(new SimpleCompletionPolicy(CHUNK_SIZE), transactionManager)
                .reader(settlementOrderReader)
                .processor(settlementItemProcessor)
                .writer(settlementItemWriter)
                .listener(settlementChunkStepListener)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Order> settlementOrderReader(
            @Value("#{jobParameters['settlementDate']}") String settlementDate
    ) {
        LocalDate targetDate = settlementDate == null ? LocalDate.now() : LocalDate.parse(settlementDate);
        return new JpaPagingItemReaderBuilder<Order>()
                .name("settlementOrderReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        select o
                        from com.example.demo.order.domain.model.Order o
                        where o.status = :status
                          and o.settled = false
                          and o.paidAt >= :fromInclusive
                          and o.paidAt < :toExclusive
                        order by o.paidAt asc, o.id asc
                        """)
                .parameterValues(Map.of(
                        "status", "PAID",
                        "fromInclusive", targetDate.atStartOfDay(),
                        "toExclusive", targetDate.plusDays(1).atStartOfDay()
                ))
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<Order, SettlementItem> settlementItemProcessor() {
        return order -> SettlementItem.from(settlementOrderTranslator.translate(order));
    }

    @Bean
    @StepScope
    public ItemWriter<SettlementItem> settlementItemWriter(
            @Value("#{stepExecutionContext['settlementBatchId']}") String settlementBatchId
    ) {
        return items -> {
            List<SettlementItem> settlementItems = new ArrayList<>();
            items.forEach(settlementItems::add);
            settlementChunkService.saveItems(UUID.fromString(settlementBatchId), settlementItems);
        };
    }

    @Bean
    public StepExecutionListener settlementChunkStepListener() {
        return new StepExecutionListener() {

            @Override
            public void beforeStep(StepExecution stepExecution) {
                LocalDate settlementDate = parseSettlementDate(stepExecution);
                UUID actorId = parseActorId(stepExecution);
                UUID batchId = settlementChunkService.createBatch(settlementDate, actorId);
                stepExecution.getExecutionContext().put("settlementBatchId", batchId.toString());
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                if (!ExitStatus.COMPLETED.equals(stepExecution.getExitStatus())) {
                    return stepExecution.getExitStatus();
                }
                UUID batchId = UUID.fromString(stepExecution.getExecutionContext().getString("settlementBatchId"));
                settlementChunkService.completeBatch(batchId);
                return stepExecution.getExitStatus();
            }
        };
    }

    private LocalDate parseSettlementDate(StepExecution stepExecution) {
        String value = stepExecution.getJobParameters().getString("settlementDate");
        return value == null ? LocalDate.now() : LocalDate.parse(value);
    }

    private UUID parseActorId(StepExecution stepExecution) {
        String value = stepExecution.getJobParameters().getString("actorId");
        return value == null || value.isBlank() ? UUID.randomUUID() : UUID.fromString(value);
    }
}
