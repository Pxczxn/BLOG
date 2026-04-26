/**
 * 社区用户认证过滤器，负责解析和验证社区用户的 JWT 令牌
 */
package com.pxczxn.blog.community.filter;

import com.pxczxn.blog.auth.dto.TokenValidationResult;
import com.pxczxn.blog.community.service.CommunityJwtService;
import com.pxczxn.blog.community.util.CommunityTokenResolver;
import com.pxczxn.blog.config.RestAuthenticationEntryPoint;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import com.pxczxn.blog.security.AuthenticatedUserType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommunityAuthenticationFilter extends OncePerRequestFilter {

    private final CommunityJwtService communityJwtService;

    /**
     * 执行过滤器内部逻辑：解析令牌、验证有效性并设置安全上下文
     *
     * @param request     HTTP 请求对象
     * @param response    HTTP 响应对象
     * @param filterChain  过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = CommunityTokenResolver.resolve(request);
        if (token != null) {
            TokenValidationResult validationResult = communityJwtService.validateToken(token);
            if (validationResult.valid()) {
                Long userId = Long.valueOf(validationResult.claims().getSubject());
                String username = validationResult.claims().get("username", String.class);
                String role = validationResult.claims().get("role", String.class);

                AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(
                        userId,
                        username,
                        role == null ? "USER" : role,
                        AuthenticatedUserType.COMMUNITY
                );

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_COMMUNITY_" + (role == null || role.isBlank() ? "USER" : role)))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                SecurityContextHolder.clearContext();
                request.setAttribute(RestAuthenticationEntryPoint.AUTH_ERROR_ATTRIBUTE, validationResult.errorCode().name());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 判断是否跳过此过滤器
     * 仅当请求路径以 /api/community/ 或 /api/public/ 开头时才执行过滤
     *
     * @param request HTTP 请求对象
     * @return 若应跳过返回 true，否则返回 false
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.startsWith("/api/community/") || path.startsWith("/api/public/"));
    }
}
