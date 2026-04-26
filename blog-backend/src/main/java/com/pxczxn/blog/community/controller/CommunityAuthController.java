/**
 * 社区用户认证控制器，处理社区用户的注册、登录和退出登录
 */
package com.pxczxn.blog.community.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.dto.CommunityAuthResponse;
import com.pxczxn.blog.community.dto.CommunityLoginRequest;
import com.pxczxn.blog.community.dto.CommunityRegisterRequest;
import com.pxczxn.blog.community.service.CommunityAuthService;
import com.pxczxn.blog.community.util.CommunityTokenResolver;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityAuthController {

    private final CommunityAuthService communityAuthService;

    /** Cookie 是否启用 Secure 标志 */
    @Value("${app.security.cookie-secure:true}")
    private boolean cookieSecure;

    /**
     * 社区用户注册
     *
     * @param request      注册请求参数（用户名、邮箱、密码、显示名称）
     * @param httpRequest  HTTP 请求对象，用于获取客户端信息
     * @param httpResponse HTTP 响应对象，用于写入 JWT Cookie
     * @return 注册成功后的认证信息（含用户资料和令牌）
     */
    @PostMapping("/auth/register")
    public Result<CommunityAuthResponse> register(@Valid @RequestBody CommunityRegisterRequest request,
                                                  HttpServletRequest httpRequest,
                                                  HttpServletResponse httpResponse) {
        CommunityAuthResponse response = communityAuthService.register(request, httpRequest);
        writeJwtCookie(httpResponse, response.getToken(), 86400);
        return Result.success(response);
    }

    /**
     * 社区用户登录
     *
     * @param request      登录请求参数（用户名/邮箱、密码）
     * @param httpRequest  HTTP 请求对象，用于获取客户端信息
     * @param httpResponse HTTP 响应对象，用于写入 JWT Cookie
     * @return 登录成功后的认证信息（含用户资料和令牌）
     */
    @PostMapping("/auth/login")
    public Result<CommunityAuthResponse> login(@Valid @RequestBody CommunityLoginRequest request,
                                               HttpServletRequest httpRequest,
                                               HttpServletResponse httpResponse) {
        CommunityAuthResponse response = communityAuthService.login(request, httpRequest);
        writeJwtCookie(httpResponse, response.getToken(), 86400);
        return Result.success(response);
    }

    /**
     * 社区用户退出登录，吊销当前令牌并清除 Cookie
     *
     * @param request  HTTP 请求对象，用于解析令牌
     * @param response HTTP 响应对象，用于清除 JWT Cookie
     * @return 退出成功结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = CommunityTokenResolver.resolve(request);
        if (token != null) {
            communityAuthService.logout(token);
        }
        writeJwtCookie(response, "", 0);
        return Result.success("退出成功", null);
    }

    /**
     * 将 JWT 令牌写入响应的 Cookie 中
     *
     * @param response HTTP 响应对象
     * @param token    JWT 令牌字符串，退出时传空字符串
     * @param maxAge   Cookie 有效期（秒），退出时传 0 表示立即失效
     */
    private void writeJwtCookie(HttpServletResponse response, String token, int maxAge) {
        Cookie cookie = new Cookie("community_jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }
}
