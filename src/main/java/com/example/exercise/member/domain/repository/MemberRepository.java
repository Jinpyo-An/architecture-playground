package com.example.exercise.member.domain.repository;


import com.example.exercise.member.domain.Member;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findByPhone(String phone);
    Optional<Member> findByEmail(String email);
    Member save(Member member);
}
