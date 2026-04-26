/**
 * 管理员用户响应 DTO
 * <p>
 * 用于返回管理员账户信息给前端
 */
package com.pxczxn.blog.user.dto;

import com.pxczxn.blog.user.entity.AdminUserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员用户响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 邮箱地址 */
    private String email;

    /** 账户状态 */
    private AdminUserStatus status;

    /** 角色 */
    private String role;

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /**
     * 将实体对象转换为响应 DTO
     *
     * @param entity 管理员用户实体
     * @return 管理员用户响应 DTO
     */
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
