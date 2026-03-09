package com.pxczxn.blog.auth.service;

import com.pxczxn.blog.auth.repository.TokenRevokedRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    private final SecretKey key;
    private final long jwtExpiration;
    private final TokenRevokedRepository tokenRevokedRepository;

    public JwtService(@Value("${jwt.secret:my-very-secure-secret-key-for-jwt-token-generation-min-32-chars}") String secret,
                      @Value("${jwt.expiration:86400000}") long jwtExpiration,
                      TokenRevokedRepository tokenRevokedRepository) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = jwtExpiration;
        this.tokenRevokedRepository = tokenRevokedRepository;
    }

    public String generateToken(Long userId, String username, String deviceId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        String jti = java.util.UUID.randomUUID().toString();

        return Jwts.builder()
                .id(jti)
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("deviceId", deviceId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractUsername(String token) {
        return extractClaims(token).get("username", String.class);
    }

    public String extractDeviceId(String token) {
        return extractClaims(token).get("deviceId", String.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String extractJti(String token) {
        return extractClaims(token).getId();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            if (isTokenExpired(token)) {
                return false;
            }
            String jti = extractJti(token);
            if (jti != null && tokenRevokedRepository.existsByJti(jti)) {
                log.warn("Token has been revoked: jti={}", jti);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Date getExpirationDate(String token) {
        return extractClaims(token).getExpiration();
    }
}
