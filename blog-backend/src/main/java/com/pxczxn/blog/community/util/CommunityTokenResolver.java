/**
 * 社区用户令牌解析器，从 HTTP 请求中解析社区用户的 JWT 令牌
 */
package com.pxczxn.blog.community.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class CommunityTokenResolver {

    /** 请求头名称 */
    private static final String HEADER_NAME = "X-Community-Authorization";

    /** Cookie 名称 */
    private static final String COOKIE_NAME = "community_jwt";

    /** 私有构造函数，防止实例化 */
    private CommunityTokenResolver() {
    }

    /**
     * 从 HTTP 请求中解析 JWT 令牌
     * 优先从请求头 X-Community-Authorization 获取（Bearer Token），
     * 若请求头不存在则从 Cookie community_jwt 获取
     *
     * @param request HTTP 请求对象
     * @return JWT 令牌字符串，若不存在则返回 null
     */
    public static String resolve(HttpServletRequest request) {
        // 从请求头获取
        String authHeader = request.getHeader(HEADER_NAME);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            if (!token.isEmpty()) {
                return token;
            }
        }

        // 从 Cookie 获取
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue().trim();
            }
        }
        return null;
    }
}
