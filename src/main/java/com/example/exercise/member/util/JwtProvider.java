package com.example.exercise.member.util;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
    public final int JWT_REFRSH_EXPIRED_TIME = 1000*60*60*24*7;
    public String generateToken(Authentication authentication) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return generate(authentication, JWT_EXPIRED_TIME);
    }

    public String generateRefreshToken(Authentication authentication) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return generate(authentication, JWT_REFRSH_EXPIRED_TIME);
    }

    public String generate(Authentication authentication, int expiredTime) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Date date = new Date();
        Date expiredDate = new Date(date.getTime()+expiredTime);
        return Jwts.builder().subject((String) authentication.getPrincipal()).issuedAt(date)
                .expiration(expiredDate)
                .signWith(loadPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public String verifyToken(String refreshToken) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String jwt = refreshToken.replace("Bearer ", "");
        log.info("token : {}", jwt);
        String subjects = Jwts.parser()
                .verifyWith(loadPublicKey())
                .build().parseSignedClaims(jwt).getPayload().getSubject();
        return subjects;
    }

    public KeyPair makeRsaKey(){
        KeyPairGenerator pairGenerator = null;
        try {
            pairGenerator = KeyPairGenerator.getInstance("RSA");
        }catch (Exception e){

        }
        KeyPair keypair = pairGenerator.generateKeyPair();
        log.info("public : {}",Base64.getEncoder().encodeToString(keypair.getPublic().getEncoded()));
        log.info("private : {}",Base64.getEncoder().encodeToString(keypair.getPrivate().getEncoded()));
        return pairGenerator.generateKeyPair();
    }
    private PrivateKey loadPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyByte = Base64.getDecoder().decode(privateKey);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyByte));
    }

    private PublicKey loadPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyByte = Base64.getDecoder().decode(publicKey);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyByte));
    }
}
