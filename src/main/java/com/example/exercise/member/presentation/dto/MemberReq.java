package com.example.exercise.member.presentation.dto;

public record MemberReq(String email,
                        String name,
                        String password,
                        String phone,
                        String address) {
}
