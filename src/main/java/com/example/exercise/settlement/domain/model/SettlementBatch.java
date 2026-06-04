package com.example.exercise.settlement.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Table(name = "\"settlement_batch\"", schema = "public")
@Comment("정산 배치")
public class SettlementBatch {

    @Id
    @Comment("정산 배치 ID(UUID)")
    private UUID id;

    @Column(name = "settlement_date", nullable = false)
    @Comment("정산 기준일")
    private LocalDate settlementDate;

    @Column(name = "seller_id")
    @Comment("판매자 ID(UUID)")
    private UUID sellerId;

    @Column(name = "total_gross_amount", nullable = false, precision = 15, scale = 2)
    @Comment("총 주문 금액")
    private BigDecimal totalGrossAmount;

    @Column(name = "total_fee_amount", nullable = false, precision = 15, scale = 2)
    @Comment("총 수수료 금액")
    private BigDecimal totalFeeAmount;

    @Column(name = "total_refund_amount", nullable = false, precision = 15, scale = 2)
    @Comment("총 환불 금액")
    private BigDecimal totalRefundAmount;

    @Column(name = "total_settlement_amount", nullable = false, precision = 15, scale = 2)
    @Comment("총 정산 금액")
    private BigDecimal totalSettlementAmount;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Comment("정산 상태")
    private SettlementStatus status;

    @Column(name = "reg_id", nullable = false)
    @Comment("생성자 ID(UUID)")
    private UUID regId;

    @Column(name = "reg_dt", nullable = false)
    @Comment("생성 일시")
    private LocalDateTime regDt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettlementItem> items = new ArrayList<>();

    protected SettlementBatch() {
    }

    private SettlementBatch(LocalDate settlementDate, UUID sellerId, UUID actorId) {
        this.id = UUID.randomUUID();
        this.settlementDate = settlementDate;
        this.sellerId = Objects.requireNonNull(sellerId, "sellerId");
        this.totalGrossAmount = BigDecimal.ZERO;
        this.totalFeeAmount = BigDecimal.ZERO;
        this.totalRefundAmount = BigDecimal.ZERO;
        this.totalSettlementAmount = BigDecimal.ZERO;
        this.status = SettlementStatus.COMPLETED;
        this.regId = actorId;
    }

    public static SettlementBatch create(LocalDate settlementDate, UUID sellerId, UUID actorId) {
        return new SettlementBatch(settlementDate, sellerId, actorId);
    }

    public void addItem(SettlementItem item) {
        item.assignBatch(this);
        items.add(item);
        totalGrossAmount = totalGrossAmount.add(item.getGrossAmount());
        totalFeeAmount = totalFeeAmount.add(item.getFeeAmount());
        totalRefundAmount = totalRefundAmount.add(item.getRefundAmount());
        totalSettlementAmount = totalSettlementAmount.add(item.getSettlementAmount());
    }

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (settlementDate == null) {
            settlementDate = LocalDate.now();
        }
        if (status == null) {
            status = SettlementStatus.COMPLETED;
        }
        if (regId == null) {
            regId = id;
        }
        if (regDt == null) {
            regDt = LocalDateTime.now();
        }
    }
}
