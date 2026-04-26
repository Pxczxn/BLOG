/**
 * JWT 认证过滤器
 * <p>
 * 拦截所有需要认证的后台管理接口请求，从请求头或 Cookie 中提取 JWT 令牌并验证。
 * 验证通过后将用户信息存入 Spring Security 上下文中供后续使用。
 */
package com.pxczxn.blog.auth.filter;

import com.pxczxn.blog.auth.dto.TokenValidationResult;
import com.pxczxn.blog.auth.service.JwtService;
import com.pxczxn.blog.auth.util.RequestTokenResolver;
import com.pxczxn.blog.config.RestAuthenticationEntryPoint;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import com.pxczxn.blog.security.AuthenticatedUserType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /**
     * JWT 认证过滤核心逻辑
     * <p>
     * 从请求中提取并验证 JWT，验证成功后将用户信息存入 SecurityContext。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = RequestTokenResolver.resolve(request);

        if (token != null) {
            TokenValidationResult validationResult = jwtService.validateToken(token);
            if (validationResult.valid()) {
                String userId = validationResult.claims().getSubject();
                String username = validationResult.claims().get("username", String.class);
                String role = validationResult.claims().get("role", String.class);
                String authority = "ROLE_" + (role == null || role.isBlank() ? "USER" : role);

                AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(
                        Long.valueOf(userId),
                        username,
                        role == null ? "USER" : role,
                        AuthenticatedUserType.ADMIN
                );

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                List.of(new SimpleGrantedAuthority(authority))
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("JWT 认证成功: userId={}, username={}", userId, username);
            } else {
                SecurityContextHolder.clearContext();
                request.setAttribute(RestAuthenticationEntryPoint.AUTH_ERROR_ATTRIBUTE, validationResult.errorCode().name());
                log.warn("JWT 认证失败: reason={}", validationResult.errorCode());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 判断是否跳过此过滤器
     * <p>
     * 登录接口和 ping 接口不需要 JWT 验证。
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/admin/")
                || "/api/admin/login".equals(path)
                || "/api/admin/ping".equals(path);
    }
}

