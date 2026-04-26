/**
 * 社区用户公开资料响应 DTO，用于展示其他用户的公开信息
 */
package com.pxczxn.blog.community.dto;

import com.pxczxn.blog.community.entity.CommunityUser;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicCommunityProfileResponse {

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

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

    /** 粉丝数量 */
    private long followerCount;

    /** 关注数量 */
    private long followingCount;

    /** 当前访问者是否已关注该用户 */
    private boolean followedByMe;

    /**
     * 从实体对象构建响应 DTO
     *
     * @param user           社区用户实体
     * @param followerCount  粉丝数量
     * @param followingCount 关注数量
     * @param followedByMe   当前访问者是否已关注
     * @return 公开资料响应 DTO
     */
    public static PublicCommunityProfileResponse from(CommunityUser user,
                                                      long followerCount,
                                                      long followingCount,
                                                      boolean followedByMe) {
        return PublicCommunityProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .website(user.getWebsite())
                .role(user.getRole().name())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .followedByMe(followedByMe)
                .build();
    }
}
