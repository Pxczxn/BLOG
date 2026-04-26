


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

    






    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.startsWith("/api/community/") || path.startsWith("/api/public/"));
    }
}
