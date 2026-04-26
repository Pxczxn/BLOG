/**
 * 社区用户认证响应 DTO，包含用户资料和认证令牌信息
 */
package com.pxczxn.blog.community.dto;

import com.pxczxn.blog.community.entity.CommunityUser;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CommunityAuthResponse {

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 邮箱地址 */
    private String email;

    /** 显示名称（昵称） */
    private String displayName;

    /** 头像 URL */
    private String avatar;

    /** 个人简介 */
    private String bio;

    /** 个人网站 URL */
    private String website;

    /** 用户角色 */
    private String role;

    /** 用户状态 */
    private String status;

    /** JWT 认证令牌 */
    private String token;

    /** 令牌过期时间 */
    private Instant expiresAt;

    /**
     * 从实体对象构建响应 DTO
     *
     * @param user     社区用户实体
     * @param token    JWT 令牌
     * @param expiresAt 令牌过期时间
     * @return 认证响应 DTO
     */
    public static CommunityAuthResponse from(CommunityUser user, String token, Instant expiresAt) {
        return CommunityAuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .website(user.getWebsite())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .token(token)
                .expiresAt(expiresAt)
                .build();
    }
}
