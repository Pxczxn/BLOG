




package com.pxczxn.blog.community.service;

import com.pxczxn.blog.auth.dto.TokenValidationResult;
import com.pxczxn.blog.common.response.ApiErrorCode;
import com.pxczxn.blog.community.entity.CommunityUserStatus;
import com.pxczxn.blog.community.repository.CommunityTokenRevokedRepository;
import com.pxczxn.blog.community.repository.CommunityUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class CommunityJwtService {

    private final SecretKey key;
    private final long jwtExpiration;
    private final CommunityTokenRevokedRepository communityTokenRevokedRepository;
    private final CommunityUserRepository communityUserRepository;

    public CommunityJwtService(@Value("${jwt.secret}") String secret,
                               @Value("${jwt.expiration:86400000}") long jwtExpiration,
                               CommunityTokenRevokedRepository communityTokenRevokedRepository,
                               CommunityUserRepository communityUserRepository) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET 不能为空");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET 长度不能少于 32 个字符");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = jwtExpiration;
        this.communityTokenRevokedRepository = communityTokenRevokedRepository;
        this.communityUserRepository = communityUserRepository;
    }

    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .claim("userType", "COMMUNITY")
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

    public Date getExpirationDate(String token) {
        return extractClaims(token).getExpiration();
    }

    public TokenValidationResult validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            if (claims.getExpiration().before(new Date())) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_TOKEN_EXPIRED);
            }
            String jti = claims.getId();
            if (jti != null && communityTokenRevokedRepository.existsByJti(jti)) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_SESSION_EXPIRED);
            }
            Long userId = Long.valueOf(claims.getSubject());
            var user = communityUserRepository.findById(userId).orElse(null);
            if (user == null) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_SESSION_EXPIRED);
            }
            if (user.getStatus() != CommunityUserStatus.ACTIVE) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_ACCOUNT_DISABLED);
            }
            return TokenValidationResult.valid(claims);
        } catch (ExpiredJwtException ex) {
            log.warn("Community token expired: {}", ex.getMessage());
            return TokenValidationResult.invalid(ApiErrorCode.AUTH_TOKEN_EXPIRED);
        } catch (Exception ex) {
            log.warn("Community token validation failed: {}", ex.getMessage());
            return TokenValidationResult.invalid(ApiErrorCode.AUTH_INVALID_TOKEN);
        }
    }
}