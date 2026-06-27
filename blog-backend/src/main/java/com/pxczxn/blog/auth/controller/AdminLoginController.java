/**
 * 后台管理员登录控制器
 * <p>
 * 提供管理员的登录、登出、当前用户查询、会话管理等接口。
 * 登录成功后会通过 Cookie 写入 JWT 令牌，前端可从 Cookie 或 Authorization 头获取令牌。
 */
package com.pxczxn.blog.auth.controller;

import com.pxczxn.blog.auth.dto.AdminAuthResult;
import com.pxczxn.blog.auth.dto.LoginRequest;
import com.pxczxn.blog.auth.dto.LoginResponse;
import com.pxczxn.blog.auth.dto.TokenValidationResult;
import com.pxczxn.blog.auth.service.AdminRefreshTokenService;
import com.pxczxn.blog.auth.service.DeviceSessionService;
import com.pxczxn.blog.auth.service.JwtService;
import com.pxczxn.blog.auth.service.LoginService;
import com.pxczxn.blog.auth.service.LogoutService;
import com.pxczxn.blog.auth.util.RequestTokenResolver;
import com.pxczxn.blog.common.response.ApiErrorCode;
import com.pxczxn.blog.common.response.Result;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminLoginController {

    private static final String LEGACY_ACCESS_COOKIE = "jwt";
    private static final String REFRESH_COOKIE = "admin_refresh";

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final JwtService jwtService;
    private final DeviceSessionService deviceSessionService;
    private final AdminRefreshTokenService adminRefreshTokenService;

    /** 是否启用 Cookie Secure 标志（生产环境为 true） */
    @Value("${app.security.cookie-secure:true}")
    private boolean cookieSecure;

    /**
     * 管理员登录
     * <p>
     * 登录成功后将 JWT 写入 Cookie 并返回用户信息。
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        AdminAuthResult result = loginService.login(request, httpRequest);
        writeRefreshCookie(httpResponse, result.refreshToken(), jwtRefreshMaxAgeSeconds());
        clearLegacyAccessCookie(httpResponse);
        return Result.success(result.response());
    }

    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = resolveCookie(request, REFRESH_COOKIE);
        if (refreshToken == null) {
            clearAuthCookies(response);
            return Result.error(ApiErrorCode.AUTH_REQUIRED);
        }
        AdminAuthResult result = loginService.refresh(refreshToken);
        writeRefreshCookie(response, result.refreshToken(), jwtRefreshMaxAgeSeconds());
        clearLegacyAccessCookie(response);
        return Result.success(result.response());
    }

    /** 管理员登出，将令牌加入黑名单并清除 Cookie */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = RequestTokenResolver.resolve(request);
        if (token != null) {
            logoutService.logout(token);
        } else {
            String refreshToken = resolveCookie(request, REFRESH_COOKIE);
            if (refreshToken != null) {
                logoutService.logoutByRefreshToken(refreshToken);
            }
        }
        clearAuthCookies(response);
        return Result.success("退出成功", null);
    }

    /** 健康检查接口，用于前端检测登录状态 */
    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("pong");
    }

    /** 获取当前登录用户信息 */
    @GetMapping("/me")
    public Result<LoginResponse> getCurrentUser(HttpServletRequest request) {
        String token = RequestTokenResolver.resolve(request);
        if (token == null) {
            return Result.error(ApiErrorCode.AUTH_REQUIRED);
        }

        TokenValidationResult validationResult = jwtService.validateToken(token);
        if (!validationResult.valid()) {
            return Result.error(validationResult.errorCode());
        }

        LoginResponse loginResponse = LoginResponse.builder().userId(Long.valueOf(validationResult.claims().getSubject())).username(validationResult.claims().get("username", String.class)).deviceId(validationResult.claims().get("deviceId", String.class)).role(validationResult.claims().get("role", String.class)).build();

        return Result.success(loginResponse);
    }

    /** 获取当前用户的所有活跃设备会话 */
    @GetMapping("/sessions")
    public Result<?> getActiveSessions(HttpServletRequest request) {
        String token = RequestTokenResolver.resolve(request);
        if (token == null) {
            return Result.error(ApiErrorCode.AUTH_REQUIRED);
        }

        TokenValidationResult validationResult = jwtService.validateToken(token);
        if (!validationResult.valid()) {
            return Result.error(validationResult.errorCode());
        }

        Long userId = Long.valueOf(validationResult.claims().getSubject());
        return Result.success(deviceSessionService.getActiveSessions(userId));
    }

    /**
     * 将 JWT 令牌写入响应 Cookie
     *
     * @param response HTTP 响应
     * @param token    JWT 令牌（登出时传空字符串）
     * @param maxAge   Cookie 有效期（秒），登出时传 0 立即失效
     */
    private void writeRefreshCookie(HttpServletResponse response, String token, int maxAge) {
        Cookie cookie = new Cookie(REFRESH_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/admin");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        writeRefreshCookie(response, "", 0);
        clearLegacyAccessCookie(response);
    }

    private void clearLegacyAccessCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(LEGACY_ACCESS_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    private int jwtRefreshMaxAgeSeconds() {
        return adminRefreshTokenService.refreshCookieMaxAgeSeconds();
    }

    private String resolveCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
