/**
 * 社区用户个人资料响应 DTO，返回当前用户的完整资料信息
 */
package com.pxczxn.blog.community.dto;

import com.pxczxn.blog.community.entity.CommunityUser;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityProfileResponse {

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

    /**
     * 从实体对象构建响应 DTO
     *
     * @param user 社区用户实体
     * @return 个人资料响应 DTO
     */
    public static CommunityProfileResponse from(CommunityUser user) {
        return CommunityProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .website(user.getWebsite())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
    }
}
