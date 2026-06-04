package com.example.exercise.settlement.infrastructure.persistence;

import com.example.exercise.settlement.domain.model.SettlementItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SettlementItemJpaRepository extends JpaRepository<SettlementItem, UUID> {

    @Query(value = """
            select
                coalesce(sum(gross_amount), 0) as "totalGrossAmount",
                coalesce(sum(fee_amount), 0) as "totalFeeAmount",
                coalesce(sum(refund_amount), 0) as "totalRefundAmount",
                coalesce(sum(settlement_amount), 0) as "totalSettlementAmount"
            from public."settlement_item"
            where settlement_batch_id = :batchId
            """, nativeQuery = true)
    SettlementItemTotalProjection sumByBatchId(@Param("batchId") UUID batchId);
}
