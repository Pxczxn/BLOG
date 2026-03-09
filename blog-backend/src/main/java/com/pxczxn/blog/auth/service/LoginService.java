package com.pxczxn.blog.auth.service;

import com.pxczxn.blog.auth.dto.LoginRequest;
import com.pxczxn.blog.auth.dto.LoginResponse;
import com.pxczxn.blog.auth.entity.DeviceSession;
import com.pxczxn.blog.auth.exception.LoginException;
import com.pxczxn.blog.user.entity.AdminUser;
import com.pxczxn.blog.user.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        String identifier = request.getUsername();

        // 检查账号是否被锁定
        if (loginAttemptService.isLocked(identifier)) {
            long remainingMinutes = loginAttemptService.getRemainingLockTime(identifier) / 60000;
            log.warn("登录失败：账号已锁定, identifier={}, 剩余锁定时间={}分钟", identifier, remainingMinutes);
            throw LoginException.accountLocked();
        }

        // 查询用户
        AdminUser user;
        try {
            user = adminUserService.getByUsernameOrEmail(identifier);
        } catch (Exception e) {
            loginAttemptService.loginFailed(identifier);
            throw LoginException.userNotFound();
        }

        // 检查账号状态
        if (com.pxczxn.blog.user.entity.AdminUserStatus.BANNED.equals(user.getStatus())) {
            log.warn("登录失败：账号已被禁用, username={}", user.getUsername());
            throw LoginException.accountBanned();
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            System.out.println(user.getPasswordHash());
            loginAttemptService.loginFailed(identifier);
            int attempts = loginAttemptService.getAttempts(identifier);
            log.warn("登录失败：密码错误, username={}, attempts={}", user.getUsername(), attempts);
            throw LoginException.invalidPassword();
        }

        // 登录成功，清除失败记录
        loginAttemptService.loginSucceeded(identifier);

        // 更新最后登录时间
        adminUserService.updateLastLoginAt(user.getId());

        // 获取设备信息
        String deviceId = request.getDeviceId();
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = generateDeviceId(httpRequest);
        }

        String ip = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        // 创建或更新设备会话（包含设备限制逻辑）
        DeviceSession session = deviceSessionService.createOrUpdateSession(user, deviceId, request.getDeviceName(), ip, userAgent);

        // 生成 JWT Token
        String token = jwtService.generateToken(user.getId(), user.getUsername(), deviceId, user.getRole());

        log.info("登录成功: username={}, ip={}, deviceId={}", user.getUsername(), ip, deviceId);

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .lastLoginAt(LocalDateTime.now())
                .message("登录成功")
                .deviceId(deviceId)
                .token(token)
                .expiresAt(jwtService.getExpirationDate(token).toInstant())
                .build();
    }

    private String generateDeviceId(HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        return String.valueOf((ip + userAgent).hashCode());
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "unknown";
    }
}
