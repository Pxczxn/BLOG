/**
 * 帖子作者摘要响应
 */
package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.entity.CommunityUser;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityPostAuthorSummaryResponse {

    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 显示名称 */
    private String displayName;
    /** 头像地址 */
    private String avatar;
    /** 用户角色 */
    private String role;

    /**
     * 从社区用户实体转换为响应对象
     *
     * @param user 社区用户实体
     * @return 响应对象
     */
    public static CommunityPostAuthorSummaryResponse from(CommunityUser user) {
        return CommunityPostAuthorSummaryResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .role(user.getRole().name())
                .build();
    }
}

