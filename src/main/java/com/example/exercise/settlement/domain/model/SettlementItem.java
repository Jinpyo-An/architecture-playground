package com.example.exercise.settlement.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Table(name = "\"settlement_item\"", schema = "public")
@Comment("정산 항목")
public class SettlementItem {

    @Id
    @Comment("정산 항목 ID(UUID)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_batch_id", nullable = false)
    @Comment("정산 배치")
    private SettlementBatch batch;

    @Column(name = "order_id", nullable = false)
    @Comment("주문 ID(UUID)")
    private UUID orderId;

    @Column(name = "order_no", nullable = false, length = 50)
    @Comment("주문 번호")
    private String orderNo;

    @Column(name = "seller_id", nullable = false)
    @Comment("판매자 ID(UUID)")
    private UUID sellerId;

    @Column(name = "gross_amount", nullable = false, precision = 15, scale = 2)
    @Comment("주문 금액")
    private BigDecimal grossAmount;

    @Column(name = "fee_amount", nullable = false, precision = 15, scale = 2)
    @Comment("수수료 금액")
    private BigDecimal feeAmount;

    @Column(name = "refund_amount", nullable = false, precision = 15, scale = 2)
    @Comment("환불 금액")
    private BigDecimal refundAmount;

    @Column(name = "settlement_amount", nullable = false, precision = 15, scale = 2)
    @Comment("정산 금액")
    private BigDecimal settlementAmount;

    protected SettlementItem() {
    }

    private SettlementItem(SettlementOrder order) {
        this.id = UUID.randomUUID();
        this.orderId = order.orderId();
        this.orderNo = order.orderNo();
        this.sellerId = order.sellerId();
        this.grossAmount = order.grossAmount();
        this.feeAmount = order.feeAmount();
        this.refundAmount = order.refundAmount();
        this.settlementAmount = order.netAmount();
    }

    public static SettlementItem from(SettlementOrder order) {
        return new SettlementItem(order);
    }

    public void assignBatch(SettlementBatch batch) {
        this.batch = batch;
    }

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
