package com.example.exercise.product.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Table(name = "\"product\"", schema = "public")
@Schema(description = "상품 정보")
public class Product {

    @Id
    @Schema(description = "상품 ID", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Column(name = "seller_id", nullable = false)
    @Schema(description = "판매자 ID", example = "11111111-1111-1111-1111-111111111111")
    private UUID sellerId;

    @Column(nullable = false, length = 100)
    @Schema(description = "상품명", example = "맥북 프로 14")
    private String name;

    @Column(columnDefinition = "text")
    @Schema(description = "상품 설명", example = "M3 칩셋, 16GB RAM")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    @Schema(description = "가격", example = "2590000")
    private BigDecimal price;

    @Column(nullable = false)
    @Schema(description = "재고", example = "10")
    private Integer stock;

    @Column(nullable = false, length = 20)
    @Schema(description = "상태", example = "ACTIVE")
    private String status;

    @Schema(hidden = true)
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1536)
    @Column(name = "embedding")
    private float[] embedding;

    @Column(name = "reg_id", nullable = false)
    @Schema(description = "등록자 ID", example = "22222222-2222-2222-2222-222222222222")
    private UUID regId;

    @Column(name = "reg_dt", nullable = false)
    @Schema(description = "등록일시", example = "2026-03-04T18:10:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime regDt;

    @Column(name = "modify_id", nullable = false)
    @Schema(description = "수정자 ID", example = "33333333-3333-3333-3333-333333333333")
    private UUID modifyId;

    @Column(name = "modify_dt", nullable = false)
    @Schema(description = "수정일시", example = "2026-03-04T18:12:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime modifyDt;

    protected Product() {
    }

    private Product(UUID id,
                    UUID sellerId,
                    String name,
                    String description,
                    BigDecimal price,
                    Integer stock,
                    String status) {
        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    public static Product create(UUID sellerId,
                                 String name,
                                 String description,
                                 BigDecimal price,
                                 Integer stock,
                                 String status,
                                 UUID creatorId) {
        Product product = new Product(UUID.randomUUID(), sellerId, name, description, price, stock, status);
        product.regId = creatorId;
        product.modifyId = creatorId;
        return product;
    }

    public void update(String name,
                       String description,
                       BigDecimal price,
                       Integer stock,
                       String status,
                       UUID modifierId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.modifyId = modifierId;
    }

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
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
        if (status == null) {
            status = "ACTIVE";
        }
        if (stock == null) {
            stock = 0;
        }
    }

    @PreUpdate
    public void onUpdate() {
        modifyDt = LocalDateTime.now();
        if (modifyId == null) {
            modifyId = id;
        }
    }
}
