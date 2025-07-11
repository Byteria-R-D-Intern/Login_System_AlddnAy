package com.example.login_system.infrastructure.service;

import com.example.login_system.application.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    public String createToken(String id, String role) {
        long simdikiZaman = System.currentTimeMillis();
        long tokenBitisi = simdikiZaman + 3600_000;

        return Jwts.builder()
            .setSubject(id)
            .claim("role", role)
            .setIssuedAt(new Date(simdikiZaman))
            .setExpiration(new Date(tokenBitisi))
            .signWith(key)
            .compact();
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public Integer extractUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            // Token'ın subject'i userId olarak kullanılıyor
            return Integer.parseInt(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token");
        }
    }
    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            Date expiration = claims.getExpiration();
            return expiration.before(new Date()); // Şu anki zaman ile karşılaştırıyorum
        } catch (JwtException | IllegalArgumentException e) {
            return true; // hatalı tokenlar da süresi geçmiş kabul ediliyor 
        }
    }

    @Override
    public Date getTokenExpirationDate(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            return claims.getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token");
        }
    }

    @Override
    public long getTokenExpirationTimeInSeconds(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            Date expiration = claims.getExpiration();
            Date now = new Date();
            
            long diffInMillies = expiration.getTime() - now.getTime();
            return diffInMillies / 1000; // Saniye cinsinden
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token");
        }
    }
}
