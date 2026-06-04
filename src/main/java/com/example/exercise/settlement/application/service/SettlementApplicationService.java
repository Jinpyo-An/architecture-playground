package com.example.exercise.settlement.application.service;

import com.example.exercise.settlement.application.acl.SettlementOrderAcl;
import com.example.exercise.settlement.application.dto.ExecuteSettlementCommand;
import com.example.exercise.settlement.application.dto.SettlementBatchResult;
import com.example.exercise.settlement.application.event.SettlementCompletedEvent;
import com.example.exercise.settlement.application.usecase.SettlementUseCase;
import com.example.exercise.settlement.domain.model.SettlementBatch;
import com.example.exercise.settlement.domain.model.SettlementItem;
import com.example.exercise.settlement.domain.model.SettlementOrder;
import com.example.exercise.settlement.domain.repository.SettlementBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementApplicationService implements SettlementUseCase {

    private final SettlementOrderAcl settlementOrderAcl;
    private final SettlementBatchRepository settlementBatchRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public SettlementBatchResult execute(ExecuteSettlementCommand command) {
        LocalDate settlementDate = command.settlementDate() == null ? LocalDate.now() : command.settlementDate();
        UUID actorId = command.actorId() == null ? UUID.randomUUID() : command.actorId();
        List<SettlementOrder> orders = settlementOrderAcl.findSettlementCandidates(settlementDate);

        if (orders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Settlement candidate order not found");
        }

        SettlementBatch batch = SettlementBatch.create(settlementDate, actorId);
        orders.stream()
                .map(SettlementItem::from)
                .forEach(batch::addItem);

        SettlementBatch saved = settlementBatchRepository.save(batch);
        eventPublisher.publishEvent(new SettlementCompletedEvent(
                saved.getId(),
                orders.stream()
                        .map(SettlementOrder::orderId)
                        .toList(),
                actorId
        ));
        return SettlementBatchResult.from(saved);
    }

    @Override
    public List<SettlementBatchResult> findAll() {
        return settlementBatchRepository.findAll().stream()
                .map(SettlementBatchResult::from)
                .toList();
    }
}
