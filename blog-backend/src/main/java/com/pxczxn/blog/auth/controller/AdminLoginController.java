package com.pxczxn.blog.auth.controller;

import com.pxczxn.blog.auth.dto.LoginRequest;
import com.pxczxn.blog.auth.dto.LoginResponse;
import com.pxczxn.blog.auth.exception.LoginException;
import com.pxczxn.blog.auth.service.DeviceSessionService;
import com.pxczxn.blog.auth.service.JwtService;
import com.pxczxn.blog.auth.service.LoginService;
import com.pxczxn.blog.auth.service.LogoutService;
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

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final JwtService jwtService;
    private final DeviceSessionService deviceSessionService;

    @Value("${app.security.cookie-secure:true}")
    private boolean cookieSecure;

    @PostMapping("/login")
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        try {
            LoginResponse response = loginService.login(request, httpRequest);

            Cookie cookie = new Cookie("jwt", response.getToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(cookieSecure);
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            cookie.setAttribute("SameSite", "Lax");
            httpResponse.addCookie(cookie);

            return Result.success(response);
        } catch (LoginException e) {
            return Result.error(e.getErrorCode(), e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = extractToken(request);
        if (token != null) {
            logoutService.logout(token);
        }

        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);

        return Result.success("退出成功", null);
    }

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("pong");
    }

    @GetMapping("/me")
    public Result<LoginResponse> getCurrentUser(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null || !jwtService.isTokenValid(token)) {
            return Result.error(401, "未登录或登录已过期");
        }

        String userId = jwtService.extractUserId(token);
        String username = jwtService.extractUsername(token);
        String deviceId = jwtService.extractDeviceId(token);

        LoginResponse loginResponse = LoginResponse.builder()
                .userId(Long.valueOf(userId))
                .username(username)
                .deviceId(deviceId)
                .build();

        return Result.success(loginResponse);
    }

    @GetMapping("/sessions")
    public Result<?> getActiveSessions(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null || !jwtService.isTokenValid(token)) {
            return Result.error(401, "未登录或登录已过期");
        }

        String userId = jwtService.extractUserId(token);
        var sessions = deviceSessionService.getActiveSessions(Long.valueOf(userId));
        return Result.success(sessions);
    }

    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
