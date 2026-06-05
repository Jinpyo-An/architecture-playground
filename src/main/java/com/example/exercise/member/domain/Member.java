package com.example.exercise.member.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "\"member\"", schema = "public")
public class Member {
    @Schema(description = "유저의 UUID")
    @Id
    private UUID id;
    @Schema(description = "유저의 email")
    @Column(nullable = false, length = 50, unique = true)
    private String email;
    @Schema(description = "유저명")
    @Column(name = "\"name\"", length = 20)
    private String name;
    @Schema(description = "비밀번호")
    @Column(name = "\"password\"", nullable = false, length = 100)
    private String password;
    @Schema(description = "핸드폰번호")
    @Column(nullable = false, length = 20, unique = true)
    private String phone;
    @Schema(description = "주소")
    @Column(nullable = false, length = 100)
    private String address;
    @Schema(description = "유저상태")
    @Column(name = "\"status\"", length = 5)
    private String status;

    @Column(name = "reg_id", nullable = false)
    private UUID regId;

    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime regDt;

    @Column(name = "modify_id", nullable = false)
    private UUID modifyId;

    @Column(name = "modify_dt", nullable = false)
    private LocalDateTime modifyDt;

    @Column(name = "saltkey", nullable = false, length = 14)
    private String saltKey;

    @Column(name = "flag", length = 5)
    private String flag;

    protected Member(){}

    private Member(String email, String name, String address, String status, String password, String phone) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.status = status;
        this.password = password;
        this.phone = phone;
        this.id = UUID.randomUUID();
        this.regId = this.id;
        this.modifyId = this.id;
        this.regDt = LocalDateTime.now();
        this.modifyDt = LocalDateTime.now();
    }

    public static Member create(String email, String name, String address, String status, String password, String phone) {
        return new Member(email, name, address, status, password, phone);
    }
}