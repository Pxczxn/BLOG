





package com.pxczxn.blog.auth.service;

import com.pxczxn.blog.auth.dto.LoginRequest;
import com.pxczxn.blog.auth.dto.LoginResponse;
import com.pxczxn.blog.auth.exception.LoginException;
import com.pxczxn.blog.user.entity.AdminUser;
import com.pxczxn.blog.user.entity.AdminUserStatus;
import com.pxczxn.blog.user.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AdminUserService adminUserService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final DeviceSessionService deviceSessionService;
    private final JwtService jwtService;

    










    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        
        String identifier = normalizeIdentifier(request.getUsername());

        
        if (loginAttemptService.isLocked(identifier)) {
            long remainingMinutes = Math.max(1, loginAttemptService.getRemainingLockTime(identifier) / 60000);
            log.warn("登录已被锁定: identifier={}, remainingMinutes={}", identifier, remainingMinutes);
            throw LoginException.accountLocked();
        }

        
        AdminUser user;
        try {
            user = adminUserService.getByUsernameOrEmail(identifier);
        } catch (Exception ex) {
            loginAttemptService.loginFailed(identifier);
            throw LoginException.invalidCredentials();
        }

        
        if (AdminUserStatus.BANNED.equals(user.getStatus())) {
            log.warn("账号已被禁用，拒绝登录: username={}", user.getUsername());
            throw LoginException.accountBanned();
        }

        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            loginAttemptService.loginFailed(identifier);
            int attempts = loginAttemptService.getAttempts(identifier);
            log.warn("登录失败，密码错误: username={}, attempts={}", user.getUsername(), attempts);
            throw LoginException.invalidCredentials();
        }

        
        loginAttemptService.loginSucceeded(identifier);
        adminUserService.updateLastLoginAt(user.getId());

        
        String deviceId = normalizeDeviceId(request.getDeviceId(), httpRequest);
        String ip = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        deviceSessionService.createOrUpdateSession(user, deviceId, request.getDeviceName(), ip, userAgent);

        String token = jwtService.generateToken(user.getId(), user.getUsername(), deviceId, user.getRole());
        LocalDateTime now = LocalDateTime.now();
        log.info("登录成功: username={}, ip={}, deviceId={}", user.getUsername(), ip, deviceId);

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .lastLoginAt(now)
                .message("登录成功")
                .deviceId(deviceId)
                .token(token)
                .expiresAt(jwtService.getExpirationDate(token).toInstant())
                .build();
    }

    
    private String normalizeIdentifier(String identifier) {
        if (identifier == null) {
            return "";
        }
        return identifier.trim().toLowerCase(Locale.ROOT);
    }

    




    private String normalizeDeviceId(String deviceId, HttpServletRequest request) {
        if (deviceId == null || deviceId.isBlank()) {
            return generateDeviceId(request);
        }
        return deviceId.trim();
    }

    




    private String generateDeviceId(HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        return String.valueOf((ip + "|" + userAgent).hashCode());
    }

    






    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "unknown";
    }
}