package com.example.exercise.member.application.usecase;


import com.example.exercise.member.application.dto.MemberCreateCommand;
import com.example.exercise.member.application.dto.MemberLogin;
import com.example.exercise.member.application.dto.MemberResQuery;
import com.example.exercise.member.application.dto.Token;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface MemberUsecase {
    MemberResQuery join(MemberCreateCommand createCommand);
    Token login(MemberLogin memberLogin) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
