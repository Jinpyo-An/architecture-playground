package com.example.exercise.member.infra.persistence;

import com.example.exercise.member.domain.Member;
import com.example.exercise.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepository {
    public final MemberJpaRepository jpaRepository;
    @Override
    public Optional<Member> findByPhone(String phone) {
        return jpaRepository.findByPhone(phone);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public Member save(Member member) {
        return jpaRepository.save(member);
    }
}
