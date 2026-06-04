package com.example.exercise.settlement.presentation.controller;

import com.example.exercise.settlement.infrastructure.batch.SettlementJobConfig;
import com.example.exercise.settlement.presentation.dto.request.RunSettlementJobRequest;
import com.example.exercise.settlement.presentation.dto.response.SettlementJobResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("${api.init}/settlements/batch")
@RequiredArgsConstructor
@Tag(name = "Settlement Batch", description = "정산 배치잡 API")
public class SettlementBatchController {

    private final JobOperator jobOperator;
    private final Job settlementJob;

    @PostMapping
    @Operation(summary = "정산 배치잡 실행", description = "정산 기준일의 미정산 PAID 주문을 정산하는 Batch Job을 실행합니다.")
    public ResponseEntity<SettlementJobResponse> run(@RequestBody RunSettlementJobRequest request) throws Exception {
        LocalDate settlementDate = request.settlementDate() == null ? LocalDate.now() : request.settlementDate();
        UUID actorId = request.actorId() == null ? UUID.randomUUID() : request.actorId();

        JobExecution jobExecution = jobOperator.start(settlementJob, new JobParametersBuilder()
                .addString("jobName", SettlementJobConfig.SETTLEMENT_JOB_NAME)
                .addString("settlementDate", settlementDate.toString())
                .addString("actorId", actorId.toString())
                .addString("requestedAt", LocalDateTime.now().toString())
                .toJobParameters());

        return ResponseEntity.ok(SettlementJobResponse.from(jobExecution));
    }
}
