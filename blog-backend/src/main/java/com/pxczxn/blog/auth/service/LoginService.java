/**
 * 登录服务
 * <p>
 * 处理管理员登录的核心业务逻辑，包括凭证验证、设备会话创建、令牌生成等。
 * 支持用户名或邮箱登录，自动检测账号状态和登录锁定。
 */
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

    /**
     * 管理员登录
     * <p>
     * 登录流程：1)检查账号锁定 → 2)查找用户 → 3)检查账号状态 →
     * 4)验证密码 → 5)创建设备会话 → 6)生成JWT令牌
     *
     * @param request    登录请求（含用户名/邮箱、密码、设备信息）
     * @param httpRequest HTTP 请求，用于获取客户端 IP 和 User-Agent
     * @return 登录响应，包含用户信息和JWT令牌
     * @throws LoginException 登录失败时抛出（账号锁定/禁用/密码错误）
     */
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        // 标准化登录标识（用户名/邮箱），统一转为小写
        String identifier = normalizeIdentifier(request.getUsername());

        // 检查账号是否因多次登录失败被锁定
        if (loginAttemptService.isLocked(identifier)) {
            long remainingMinutes = Math.max(1, loginAttemptService.getRemainingLockTime(identifier) / 60000);
            log.warn("登录已被锁定: identifier={}, remainingMinutes={}", identifier, remainingMinutes);
            throw LoginException.accountLocked();
        }

        // 根据用户名或邮箱查找用户
        AdminUser user;
        try {
            user = adminUserService.getByUsernameOrEmail(identifier);
        } catch (Exception ex) {
            loginAttemptService.loginFailed(identifier);
            throw LoginException.invalidCredentials();
        }

        // 检查账号是否被禁用
        if (AdminUserStatus.BANNED.equals(user.getStatus())) {
            log.warn("账号已被禁用，拒绝登录: username={}", user.getUsername());
            throw LoginException.accountBanned();
        }

        // 验证密码，失败则记录尝试次数
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            loginAttemptService.loginFailed(identifier);
            int attempts = loginAttemptService.getAttempts(identifier);
            log.warn("登录失败，密码错误: username={}, attempts={}", user.getUsername(), attempts);
            throw LoginException.invalidCredentials();
        }

        // 登录成功，清除失败记录并更新最后登录时间
        loginAttemptService.loginSucceeded(identifier);
        adminUserService.updateLastLoginAt(user.getId());

        // 创建或更新设备会话，并生成JWT令牌
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

    /** 标准化登录标识，去除首尾空格并转小写 */
    private String normalizeIdentifier(String identifier) {
        if (identifier == null) {
            return "";
        }
        return identifier.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 标准化设备ID
     * <p>
     * 若前端未传入设备ID，则根据 IP + User-Agent 的哈希值自动生成一个。
     */
    private String normalizeDeviceId(String deviceId, HttpServletRequest request) {
        if (deviceId == null || deviceId.isBlank()) {
            return generateDeviceId(request);
        }
        return deviceId.trim();
    }

    /**
     * 根据请求信息自动生成设备ID
     * <p>
     * 使用客户端 IP 和 User-Agent 的拼接哈希值作为设备标识。
     */
    private String generateDeviceId(HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        return String.valueOf((ip + "|" + userAgent).hashCode());
    }

    /**
     * 获取客户端真实IP地址
     * <p>
     * 依次尝试从 X-Forwarded-For、X-Real-IP 头获取，
     * 最后回退到 request.getRemoteAddr()。
     * 支持 X-Forwarded-For 中多个IP的情况，取第一个。
     */
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