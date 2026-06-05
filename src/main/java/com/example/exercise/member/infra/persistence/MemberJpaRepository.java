package com.example.exercise.member.infra.persistence;

import com.example.exercise.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberJpaRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByPhone(String phone);
    Optional<Member> findByEmail(String email);
}
