





package com.pxczxn.blog.auth.controller;

import com.pxczxn.blog.auth.dto.LoginRequest;
import com.pxczxn.blog.auth.dto.LoginResponse;
import com.pxczxn.blog.auth.dto.TokenValidationResult;
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

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final JwtService jwtService;
    private final DeviceSessionService deviceSessionService;

    
    @Value("${app.security.cookie-secure:true}")
    private boolean cookieSecure;

    




    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        LoginResponse response = loginService.login(request, httpRequest);
        writeJwtCookie(httpResponse, response.getToken(), 86400);
        return Result.success(response);
    }

    
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = RequestTokenResolver.resolve(request);
        if (token != null) {
            logoutService.logout(token);
        }
        writeJwtCookie(response, "", 0);
        return Result.success("退出成功", null);
    }

    
    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("pong");
    }

    
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

    






    private void writeJwtCookie(HttpServletResponse response, String token, int maxAge) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }
}
