package com.example.exercise.settlement.application.acl;


import com.example.exercise.settlement.domain.model.SettlementOrder;

import java.time.LocalDate;
import java.util.List;

public interface SettlementOrderAcl {

    List<SettlementOrder> findSettlementCandidates(LocalDate settlementDate);
}
