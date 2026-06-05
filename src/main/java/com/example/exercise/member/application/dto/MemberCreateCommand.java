package com.example.exercise.member.application.dto;

public record MemberCreateCommand(
        String email,
        String name,
        String password,
        String phone,
        String address
) {
}
