





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

    




    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/admin/")
                || "/api/admin/login".equals(path)
                || "/api/admin/ping".equals(path);
    }
}

