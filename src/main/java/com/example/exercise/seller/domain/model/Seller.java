package com.example.exercise.seller.domain.model;

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

@Entity
@Getter
@Table(name = "\"seller\"", schema = "public")
@Schema(description = "판매자 정보")
public class Seller {

    @Id
    @Schema(description = "판매자 ID", example = "11111111-1111-1111-1111-111111111111", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Column(nullable = false, length = 50, unique = true)
    @Schema(description = "판매자 이메일", example = "seller@example.com")
    private String email;

    @Column(name = "\"name\"", nullable = false, length = 50)
    @Schema(description = "판매자명", example = "홍길동 상점")
    private String name;

    @Column(name = "business_number", nullable = false, length = 20, unique = true)
    @Schema(description = "사업자 번호", example = "123-45-67890")
    private String businessNumber;

    @Column(nullable = false, length = 20)
    @Schema(description = "판매자 상태", example = "ACTIVE")
    private String status;

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

    protected Seller() {
    }

    private Seller(UUID id,
                   String email,
                   String name,
                   String businessNumber,
                   String status) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.businessNumber = businessNumber;
        this.status = status;
    }

    public static Seller create(String email,
                                String name,
                                String businessNumber,
                                String status,
                                UUID creatorId) {
        Seller seller = new Seller(UUID.randomUUID(), email, name, businessNumber, status);
        seller.regId = creatorId;
        seller.modifyId = creatorId;
        return seller;
    }

    public void update(String email,
                       String name,
                       String businessNumber,
                       String status,
                       UUID modifierId) {
        this.email = email;
        this.name = name;
        this.businessNumber = businessNumber;
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
    }

    @PreUpdate
    public void onUpdate() {
        modifyDt = LocalDateTime.now();
        if (modifyId == null) {
            modifyId = id;
        }
    }
}
