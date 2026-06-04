package com.example.exercise.order.application.usecase;


import com.example.exercise.order.application.dto.CreateOrderCommand;
import com.example.exercise.order.application.dto.MarkOrderPaidCommand;
import com.example.exercise.order.application.dto.OrderResult;

import java.time.LocalDate;
import java.util.List;

public interface OrderUseCase {

    OrderResult create(CreateOrderCommand command);

    List<OrderResult> findAll();

    List<OrderResult> findSettlementCandidates(LocalDate settlementDate);

    OrderResult markPaid(MarkOrderPaidCommand command);
}
