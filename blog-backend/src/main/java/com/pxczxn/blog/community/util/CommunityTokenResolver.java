


package com.pxczxn.blog.community.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class CommunityTokenResolver {

    
    private static final String HEADER_NAME = "X-Community-Authorization";

    
    private static final String COOKIE_NAME = "community_jwt";

    
    private CommunityTokenResolver() {
    }

    







    public static String resolve(HttpServletRequest request) {
        
        String authHeader = request.getHeader(HEADER_NAME);
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
            if (COOKIE_NAME.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue().trim();
            }
        }
        return null;
    }
}
