package com.example.exercise.settlement.infrastructure.persistence;

import java.math.BigDecimal;

public interface SettlementItemTotalProjection {

    BigDecimal getTotalGrossAmount();

    BigDecimal getTotalFeeAmount();

    BigDecimal getTotalRefundAmount();

    BigDecimal getTotalSettlementAmount();
}
