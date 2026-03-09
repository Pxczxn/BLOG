package com.pxczxn.blog.auth.service;

import com.pxczxn.blog.auth.entity.TokenRevoked;
import com.pxczxn.blog.auth.repository.TokenRevokedRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final JwtService jwtService;
    private final TokenRevokedRepository tokenRevokedRepository;
    private final DeviceSessionService deviceSessionService;

    @Transactional
    public void logout(String token) {
        if (!jwtService.isTokenValid(token)) {
            log.warn("尝试登出无效 token");
            return;
        }

        Claims claims = jwtService.extractClaims(token);
        String jti = claims.getId();
        String userId = claims.getSubject();
        String deviceId = claims.get("deviceId", String.class);

        // 将 jti 加入黑名单
        if (jti != null && !tokenRevokedRepository.existsByJti(jti)) {
            TokenRevoked tokenRevoked = TokenRevoked.builder()
                    .jti(jti)
                    .adminUserId(Long.valueOf(userId))
                    .revokedAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusSeconds(86400)) // 与 token 过期时间一致
                    .reason("LOGOUT")
                    .build();
            tokenRevokedRepository.save(tokenRevoked);
            log.info("Token 已加入黑名单: jti={}, userId={}", jti, userId);
        }

        // 使当前设备会话失效
        if (deviceId != null) {
            deviceSessionService.deactivateSessionByDeviceId(Long.valueOf(userId), deviceId);
        }
    }
}
