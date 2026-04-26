




package com.pxczxn.blog.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    
    private Long userId;
    
    private String username;
    
    private String email;
    
    private String role;
    
    private LocalDateTime lastLoginAt;
    
    private String message;
    
    private String deviceId;
    
    private String token;
    
    private Instant expiresAt;
}

