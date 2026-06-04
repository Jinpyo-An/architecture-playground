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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementApplicationService implements SettlementUseCase {

    private final SettlementOrderAcl settlementOrderAcl;
    private final SettlementBatchRepository settlementBatchRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public List<SettlementBatchResult> execute(ExecuteSettlementCommand command) {
        LocalDate settlementDate = command.settlementDate() == null ? LocalDate.now() : command.settlementDate();
        UUID actorId = command.actorId() == null ? UUID.randomUUID() : command.actorId();
        List<SettlementOrder> orders = settlementOrderAcl.findSettlementCandidates(settlementDate);

        if (orders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Settlement candidate order not found");
        }

        Map<UUID, List<SettlementOrder>> ordersBySeller = orders.stream()
                .collect(Collectors.groupingBy(
                        SettlementOrder::sellerId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<SettlementBatchResult> results = new ArrayList<>();
        for (Map.Entry<UUID, List<SettlementOrder>> entry : ordersBySeller.entrySet()) {
            UUID sellerId = entry.getKey();
            List<SettlementOrder> sellerOrders = entry.getValue();

            SettlementBatch batch = SettlementBatch.create(settlementDate, sellerId, actorId);
            sellerOrders.stream()
                    .map(SettlementItem::from)
                    .forEach(batch::addItem);

            SettlementBatch saved = settlementBatchRepository.save(batch);
            eventPublisher.publishEvent(new SettlementCompletedEvent(
                    saved.getId(),
                    sellerOrders.stream()
                            .map(SettlementOrder::orderId)
                            .toList(),
                    actorId
            ));
            results.add(SettlementBatchResult.from(saved));
        }
        return results;
    }

    @Override
    public List<SettlementBatchResult> findAll() {
        return settlementBatchRepository.findAll().stream()
                .map(SettlementBatchResult::from)
                .toList();
    }
}
