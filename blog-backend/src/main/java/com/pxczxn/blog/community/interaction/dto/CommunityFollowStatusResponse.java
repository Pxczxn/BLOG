/**
 * 社区用户关注状态响应DTO
 */
package com.pxczxn.blog.community.interaction.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityFollowStatusResponse {

    /** 目标用户ID */
    private Long targetUserId;
    /** 目标用户名 */
    private String targetUsername;
    /** 当前用户是否已关注目标用户 */
    private boolean following;
    /** 目标用户的粉丝数 */
    private long followerCount;
    /** 目标用户的关注数 */
    private long followingCount;
}

