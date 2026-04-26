/**
 * 登录响应 DTO
 * <p>
 * 登录成功后返回用户信息及 JWT 令牌，包含用户ID、用户名、邮箱、角色、设备ID、令牌及过期时间。
 */
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

    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 邮箱 */
    private String email;
    /** 角色 */
    private String role;
    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;
    /** 响应消息 */
    private String message;
    /** 设备ID */
    private String deviceId;
    /** JWT 令牌 */
    private String token;
    /** 令牌过期时间 */
    private Instant expiresAt;
}

