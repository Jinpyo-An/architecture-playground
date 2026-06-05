package com.example.exercise.member.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 후 생성된 토큰")
public record Token(
        @Schema(description = "토큰 재발급을 위한 토큰")
        String refreshToken,
        @Schema(description = "API 호출에 필요한 토큰")
        String accessToken
) {
}
