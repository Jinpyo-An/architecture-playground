package com.example.exercise.order.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Getter
@Table(name = "\"order\"", schema = "public")
@Comment("주문 엔티티")
public class Order {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Id
    @Comment("주문 ID(UUID)")
    private UUID id;

    @Column(name = "order_no", nullable = false, unique = true, length = 50)
    @Comment("주문 번호")
    private String orderNo;

    @Column(name = "buyer_id", nullable = false)
    @Comment("구매자 회원 ID(UUID)")
    private UUID buyerId;

    @Column(name = "seller_id", nullable = false)
    @Comment("판매자 회원 ID(UUID)")
    private UUID sellerId;

    @Column(name = "product_id", nullable = false)
    @Comment("상품 ID(UUID)")
    private UUID productId;

    @Column(nullable = false)
    @Comment("주문 수량")
    private Integer quantity;

    @Column(name = "gross_amount", nullable = false, precision = 15, scale = 2)
    @Comment("총 주문 금액")
    private BigDecimal grossAmount;

    @Column(name = "fee_amount", nullable = false, precision = 15, scale = 2)
    @Comment("수수료 금액")
    private BigDecimal feeAmount;

    @Column(name = "refund_amount", nullable = false, precision = 15, scale = 2)
    @Comment("환불 금액")
    private BigDecimal refundAmount;

    @Column(name = "net_amount", nullable = false, precision = 15, scale = 2)
    @Comment("순 정산 금액(gross - fee - refund)")
    private BigDecimal netAmount;

    @Column(nullable = false, length = 20)
    @Comment("주문 상태(PAYMENT_WAIT, PAID, CANCELED, REFUNDED 등)")
    private String status;

    @Column(name = "paid_at")
    @Comment("결제 완료 시각")
    private LocalDateTime paidAt;

    @Column(name = "canceled_at")
    @Comment("주문 취소 시각")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason", length = 30)
    @Comment("취소 사유(AUTO_TIMEOUT 등)")
    private String cancelReason;

    @Column(name = "settled", nullable = false)
    @Comment("정산 완료 여부")
    private Boolean settled;

    @Column(name = "settlement_batch_id")
    @Comment("정산 배치 ID(UUID)")
    private UUID settlementBatchId;

    @Column(name = "reg_id", nullable = false)
    @Comment("생성자 ID(UUID)")
    private UUID regId;

    @Column(name = "reg_dt", nullable = false)
    @Comment("생성 일시")
    private LocalDateTime regDt;

    @Column(name = "modify_id", nullable = false)
    @Comment("수정자 ID(UUID)")
    private UUID modifyId;

    @Column(name = "modify_dt", nullable = false)
    @Comment("수정 일시")
    private LocalDateTime modifyDt;

    protected Order() {
    }

    private Order(String orderNo,
                  UUID buyerId,
                  UUID sellerId,
                  UUID productId,
                  Integer quantity,
                  BigDecimal grossAmount,
                  BigDecimal feeAmount,
                  BigDecimal refundAmount,
                  String status,
                  LocalDateTime paidAt,
                  UUID actorId) {
        this.id = UUID.randomUUID();
        this.orderNo = orderNo;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.quantity = quantity;
        this.grossAmount = defaultAmount(grossAmount);
        this.feeAmount = defaultAmount(feeAmount);
        this.refundAmount = defaultAmount(refundAmount);
        this.netAmount = calculateNetAmount(this.grossAmount, this.feeAmount, this.refundAmount);
        this.status = status;
        this.paidAt = paidAt;
        this.settled = false;
        this.regId = actorId;
        this.modifyId = actorId;
    }

    public static Order create(String orderNo,
                               UUID buyerId,
                               UUID sellerId,
                               UUID productId,
                               Integer quantity,
                               BigDecimal grossAmount,
                               BigDecimal feeAmount,
                               BigDecimal refundAmount,
                               String status,
                               LocalDateTime paidAt,
                               UUID actorId) {
        return new Order(
                orderNo,
                buyerId,
                sellerId,
                productId,
                quantity,
                grossAmount,
                feeAmount,
                refundAmount,
                status,
                paidAt,
                actorId
        );
    }

    public void markSettled(UUID batchId, UUID actorId) {
        this.settled = true;
        this.settlementBatchId = batchId;
        this.modifyId = actorId;
    }

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (orderNo == null || orderNo.isBlank()) {
            orderNo = generateOrderNo();
        }
        if (quantity == null) {
            quantity = 1;
        }
        if (status == null || status.isBlank()) {
            status = "PAYMENT_WAIT";
        }
        if (settled == null) {
            settled = false;
        }
        grossAmount = defaultAmount(grossAmount);
        feeAmount = defaultAmount(feeAmount);
        refundAmount = defaultAmount(refundAmount);
        netAmount = calculateNetAmount(grossAmount, feeAmount, refundAmount);

        if (regId == null) {
            regId = id;
        }
        if (modifyId == null) {
            modifyId = regId;
        }
        if (regDt == null) {
            regDt = LocalDateTime.now();
        }
        if (modifyDt == null) {
            modifyDt = regDt;
        }
    }

    @PreUpdate
    public void onUpdate() {
        grossAmount = defaultAmount(grossAmount);
        feeAmount = defaultAmount(feeAmount);
        refundAmount = defaultAmount(refundAmount);
        netAmount = calculateNetAmount(grossAmount, feeAmount, refundAmount);

        modifyDt = LocalDateTime.now();
        if (modifyId == null) {
            modifyId = id;
        }
    }

    private static BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? ZERO : amount;
    }

    private static BigDecimal calculateNetAmount(BigDecimal gross, BigDecimal fee, BigDecimal refund) {
        return gross.subtract(fee).subtract(refund);
    }

    private static String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + suffix;
    }

    public void markPaid(LocalDateTime paidAt, UUID actorId) {
        if ("CANCELED".equals(this.status)) {
            throw new IllegalStateException("Cannot mark canceled order as paid: " + this.id);
        }
        this.status = "PAID";
        this.paidAt = paidAt == null ? LocalDateTime.now() : paidAt;
        this.modifyId = actorId;
    }

    public void markCanceled(String reason, UUID actorId) {
        if (!"PAYMENT_WAIT".equals(this.status)) {
            throw new IllegalStateException("Only PAYMENT_WAIT orders can be canceled: " + this.id);
        }
        this.status = "CANCELED";
        this.canceledAt = LocalDateTime.now();
        this.cancelReason = reason;
        this.modifyId = actorId;
    }
}
