





package com.pxczxn.blog.auth.service;

import com.pxczxn.blog.auth.entity.TokenRevoked;
import com.pxczxn.blog.auth.repository.TokenRevokedRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final JwtService jwtService;
    private final TokenRevokedRepository tokenRevokedRepository;
    private final DeviceSessionService deviceSessionService;

    





    @Transactional
    public void logout(String token) {
        Claims claims;
        try {
            claims = jwtService.extractClaims(token);
        } catch (Exception ex) {
            log.warn("尝试登出无效 token");
            return;
        }

        String jti = claims.getId();
        Long userId = Long.valueOf(claims.getSubject());
        String deviceId = claims.get("deviceId", String.class);

        if (jti != null && !tokenRevokedRepository.existsByJti(jti)) {
            TokenRevoked tokenRevoked = TokenRevoked.builder()
                    .jti(jti)
                    .adminUserId(userId)
                    .revokedAt(LocalDateTime.now())
                    .expiresAt(claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .reason("LOGOUT")
                    .build();
            tokenRevokedRepository.save(tokenRevoked);
            log.info("Token 已加入黑名单: jti={}, userId={}", jti, userId);
        }

        if (deviceId != null && !deviceId.isBlank()) {
            deviceSessionService.deactivateSessionByDeviceId(userId, deviceId);
        }
    }
}

