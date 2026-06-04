package com.example.exercise.settlement.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.batch.core.job.JobExecution;

import java.time.LocalDateTime;

@Schema(description = "정산 배치잡 실행 응답")
public record SettlementJobResponse(
        @Schema(description = "Job Execution ID")
        Long jobExecutionId,
        @Schema(description = "Job Instance ID")
        Long jobInstanceId,
        @Schema(description = "Job 이름")
        String jobName,
        @Schema(description = "실행 상태")
        String status,
        @Schema(description = "시작 시각")
        LocalDateTime startTime,
        @Schema(description = "종료 시각")
        LocalDateTime endTime
) {

    public static SettlementJobResponse from(JobExecution jobExecution) {
        return new SettlementJobResponse(
                jobExecution.getId(),
                jobExecution.getJobInstance().getId(),
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus().name(),
                jobExecution.getStartTime(),
                jobExecution.getEndTime()
        );
    }
}
