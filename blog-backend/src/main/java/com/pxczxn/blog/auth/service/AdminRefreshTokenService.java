package com.pxczxn.blog.auth.service;

import com.pxczxn.blog.auth.entity.DeviceSession;
import com.pxczxn.blog.auth.repository.DeviceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminRefreshTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 48;

    private final DeviceSessionRepository deviceSessionRepository;

    @Value("${jwt.refresh-expiration:2592000000}")
    private long refreshExpirationMillis;

    @Transactional
    public IssuedRefreshToken issue(DeviceSession session) {
        String token = newToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusNanos(refreshExpirationMillis * 1_000_000);
        session.setRefreshTokenHash(hash(token));
        session.setRefreshTokenExpiresAt(expiresAt);
        deviceSessionRepository.save(session);
        return new IssuedRefreshToken(token, toInstant(expiresAt));
    }

    @Transactional(readOnly = true)
    public Optional<DeviceSession> findValidSession(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return Optional.empty();
        }
        return deviceSessionRepository.findByRefreshTokenHashAndIsActiveTrue(hash(refreshToken))
                .filter(session -> session.getRefreshTokenExpiresAt() != null)
                .filter(session -> session.getRefreshTokenExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Transactional
    public void clear(DeviceSession session) {
        session.setRefreshTokenHash(null);
        session.setRefreshTokenExpiresAt(null);
        deviceSessionRepository.save(session);
    }

    public int refreshCookieMaxAgeSeconds() {
        return Math.toIntExact(Math.max(1, refreshExpirationMillis / 1000));
    }

    private String newToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    public record IssuedRefreshToken(String token, Instant expiresAt) {
    }
}
