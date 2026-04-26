




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

    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    private final CommunityAuthenticationFilter communityAuthenticationFilter;
    
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    








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
                        .requestMatchers("/api/admin/login", "/api/admin/ping").permitAll()
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

    






    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

