





package com.pxczxn.blog.auth.service;

import com.pxczxn.blog.auth.dto.TokenValidationResult;
import com.pxczxn.blog.auth.repository.DeviceSessionRepository;
import com.pxczxn.blog.auth.repository.TokenRevokedRepository;
import com.pxczxn.blog.common.response.ApiErrorCode;
import com.pxczxn.blog.user.entity.AdminUserStatus;
import com.pxczxn.blog.user.repository.AdminUserRepository;
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
public class JwtService {

    
    private final SecretKey key;
    
    private final long jwtExpiration;
    private final TokenRevokedRepository tokenRevokedRepository;
    private final DeviceSessionRepository deviceSessionRepository;
    private final AdminUserRepository adminUserRepository;

    









    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration:86400000}") long jwtExpiration,
                      TokenRevokedRepository tokenRevokedRepository,
                      DeviceSessionRepository deviceSessionRepository,
                      AdminUserRepository adminUserRepository) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET 不能为空");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET 长度不能少于 32 个字符");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = jwtExpiration;
        this.tokenRevokedRepository = tokenRevokedRepository;
        this.deviceSessionRepository = deviceSessionRepository;
        this.adminUserRepository = adminUserRepository;
    }

    










    public String generateToken(Long userId, String username, String deviceId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
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
        return validateToken(token).valid();
    }

    








    public TokenValidationResult validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            
            if (claims.getExpiration().before(new Date())) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_TOKEN_EXPIRED);
            }

            
            String jti = claims.getId();
            if (jti != null && tokenRevokedRepository.existsByJti(jti)) {
                log.warn("Token has been revoked: jti={}", jti);
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_SESSION_EXPIRED);
            }

            
            Long userId = Long.valueOf(claims.getSubject());
            String deviceId = claims.get("deviceId", String.class);
            if (deviceId == null || deviceId.isBlank()) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_INVALID_TOKEN);
            }

            
            var user = adminUserRepository.findById(userId).orElse(null);
            if (user == null) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_SESSION_EXPIRED);
            }
            
            if (AdminUserStatus.BANNED.equals(user.getStatus())) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_ACCOUNT_DISABLED);
            }
            
            if (deviceSessionRepository.findByAdminUser_IdAndDeviceIdAndIsActiveTrue(userId, deviceId).isEmpty()) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_SESSION_EXPIRED);
            }

            return TokenValidationResult.valid(claims);
        } catch (ExpiredJwtException ex) {
            log.warn("Token expired: {}", ex.getMessage());
            return TokenValidationResult.invalid(ApiErrorCode.AUTH_TOKEN_EXPIRED);
        } catch (Exception ex) {
            log.warn("Token validation failed: {}", ex.getMessage());
            return TokenValidationResult.invalid(ApiErrorCode.AUTH_INVALID_TOKEN);
        }
    }

    public Date getExpirationDate(String token) {
        return extractClaims(token).getExpiration();
    }
}