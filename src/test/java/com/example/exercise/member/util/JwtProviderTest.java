package com.example.exercise.member.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtProviderTest {
    @Autowired
    JwtProvider jwtProvider;
    @Test
    public void MakeRsaKeyTest(){
        jwtProvider.makeRsaKey();
    }
}