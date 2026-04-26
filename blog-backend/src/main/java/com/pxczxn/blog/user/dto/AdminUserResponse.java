




package com.pxczxn.blog.user.dto;

import com.pxczxn.blog.user.entity.AdminUserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    
    private Long id;

    
    private String username;

    
    private String email;

    
    private AdminUserStatus status;

    
    private String role;

    
    private LocalDateTime lastLoginAt;

    
    private LocalDateTime createdAt;

    
    private LocalDateTime updatedAt;

    





    public static AdminUserResponse from(com.pxczxn.blog.user.entity.AdminUser entity) {
        return AdminUserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .status(entity.getStatus())
                .role(entity.getRole())
                .lastLoginAt(entity.getLastLoginAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
