/**
 * 请求令牌解析器
 * <p>
 * 工具类，用于从 HTTP 请求中提取 JWT 令牌。
 * 支持从 Authorization 请求头（Bearer 方式）和 Cookie 中获取令牌。
 */
package com.pxczxn.blog.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class RequestTokenResolver {

    /** 私有构造函数，防止实例化 */
    private RequestTokenResolver() {
    }

    /**
     * 从请求中解析 JWT 令牌
     * <p>
     * 优先从 Authorization 头获取（Bearer 方式），若无则从 Cookie 中获取。
     *
     * @param request HTTP 请求
     * @return JWT 令牌字符串，若不存在则返回 null
     */
    public static String resolve(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            if (!token.isEmpty()) {
                return token;
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue().trim();
            }
        }
        return null;
    }
}

