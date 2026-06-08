package com.example.exercise.member.presentation.controller;

import com.example.exercise.member.application.dto.MemberCreateCommand;
import com.example.exercise.member.application.dto.MemberLogin;
import com.example.exercise.member.application.dto.MemberResQuery;
import com.example.exercise.member.application.dto.Token;
import com.example.exercise.member.application.usecase.MemberUsecase;
import com.example.exercise.member.presentation.dto.Login;
import com.example.exercise.member.presentation.dto.MemberReq;
import com.example.exercise.member.presentation.dto.MemberRes;
import com.example.exercise.member.presentation.dto.TokenRes;
import com.example.exercise.member.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.init}/member")
public class MemberController {

    public final MemberUsecase memberUsecase;
    public final JwtProvider provider;

    @PostMapping("/")
    public MemberRes join(@RequestBody MemberReq memberReq){
        MemberResQuery memberResQuery = memberUsecase.join(new MemberCreateCommand(memberReq.email(), memberReq.name(), memberReq.password(), memberReq.phone(), memberReq.address()));
        return new MemberRes(memberResQuery.email(), memberResQuery.name(), memberResQuery.status());
    }

    @GetMapping("/generateKey")
    public void generateKey(){
        provider.makeRsaKey();
    }

    @PostMapping("/login")
    public TokenRes login(@RequestBody Login login) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Token tok = memberUsecase.login(new MemberLogin(login.email(), login.password()));
        return new TokenRes(tok.refreshToken(), tok.accessToken());
    }

    //    @GetMapping("/refreshToken")
    @PostMapping("/refreshToken")// swagger 테스트 전용.
    public TokenRes refreshToken(
//            @RequestHeader("RefreshToken") String refreshToken,
            @RequestBody String refreshToken
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Token tok = memberUsecase.refreshToken(refreshToken);
        return new TokenRes(tok.refreshToken(), tok.accessToken());
    }
}
