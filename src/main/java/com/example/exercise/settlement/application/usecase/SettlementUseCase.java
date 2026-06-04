package com.example.exercise.settlement.application.usecase;

import com.example.exercise.settlement.application.dto.ExecuteSettlementCommand;
import com.example.exercise.settlement.application.dto.SettlementBatchResult;

import java.util.List;

public interface SettlementUseCase {

    List<SettlementBatchResult> execute(ExecuteSettlementCommand command);

    List<SettlementBatchResult> findAll();
}
