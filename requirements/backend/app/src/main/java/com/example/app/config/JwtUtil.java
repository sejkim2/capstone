package com.example.app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final Key key;
    private final long expirationTime;

    // secretKey, expirationTime 둘 다 @Value로 주입받는다
    @Autowired
    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-time}") long expirationTime) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationTime = expirationTime;
    }

    // 토큰 생성
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime); // 설정된 만료시간 적용

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 이메일(email) 꺼내기
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
