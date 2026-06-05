package com.example.exercise.member.util;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.private-key}")
    private String privateKey;
    @Value("${jwt.public-key}")
    private String publicKey;

    public final int JWT_EXPIRED_TIME = 1000*60*60*24;
    public String generateToken(Authentication authentication) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Date date = new Date();
        Date expiredDate = new Date(date.getTime()+JWT_EXPIRED_TIME);
        return Jwts.builder().subject((String) authentication.getPrincipal()).issuedAt(date)
                .expiration(expiredDate)
                .signWith(loadPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public KeyPair makeRsaKey(){
        KeyPairGenerator pairGenerator = null;
        try {
            pairGenerator = KeyPairGenerator.getInstance("RSA");
        }catch (Exception e){

        }
        KeyPair keypair = pairGenerator.generateKeyPair();
        log.info("public : {}", Base64.getEncoder().encodeToString(keypair.getPublic().getEncoded()));
        log.info("private : {}",Base64.getEncoder().encodeToString(keypair.getPrivate().getEncoded()));
        return pairGenerator.generateKeyPair();
    }
    private PrivateKey loadPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }
}
