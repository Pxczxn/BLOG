/**
 * 安全配置
 * <p>
 * 配置 Spring Security 的认证授权规则和过滤器链
 */
package com.pxczxn.blog.config;

import com.pxczxn.blog.auth.filter.JwtAuthenticationFilter;
import com.pxczxn.blog.community.filter.CommunityAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /** JWT 认证过滤器，用于验证后台管理系统的 JWT Token */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    /** 社区认证过滤器，用于验证社区用户的认证信息 */
    private final CommunityAuthenticationFilter communityAuthenticationFilter;
    /** 认证入口点，处理未认证请求 */
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    /** 访问拒绝处理器，处理权限不足请求 */
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    /**
     * 创建安全过滤器链
     * <p>
     * 配置 CORS、CSRF、会话管理、异常处理、授权规则和自定义过滤器
     *
     * @param http HttpSecurity 配置对象
     * @return 安全过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/index.html", "/static/**").permitAll()
                        .requestMatchers("/api/admin/login", "/api/admin/refresh", "/api/admin/ping").permitAll()
                        .requestMatchers("/api/community/auth/**").permitAll()
                        .requestMatchers("/api/public/**", "/api/articles/**").permitAll()
                        .requestMatchers("/api/community/posts/{id}/interaction").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/health/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/readiness", "/actuator/health/liveness").permitAll()
                        .requestMatchers("/api/community/**").hasAnyRole("COMMUNITY_USER", "COMMUNITY_MODERATOR")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(communityAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 创建密码编码器
     * <p>
     * 使用 BCrypt 算法进行密码加密
     *
     * @return BCrypt 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

