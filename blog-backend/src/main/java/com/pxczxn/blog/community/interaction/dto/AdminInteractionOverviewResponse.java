/**
 * 管理端社区互动概览响应DTO
 */
package com.pxczxn.blog.community.interaction.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminInteractionOverviewResponse {

    /** 总点赞数 */
    private long totalLikes;
    /** 总收藏数 */
    private long totalFavorites;
    /** 总关注数 */
    private long totalFollows;
    /** 总通知数 */
    private long totalNotifications;
    /** 未读通知数 */
    private long unreadNotifications;
    /** 热门帖子列表 */
    private List<AdminPostHeatItemResponse> topHotPosts;
}

