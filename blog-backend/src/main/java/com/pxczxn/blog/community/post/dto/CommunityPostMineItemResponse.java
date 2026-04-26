/**
 * 我的帖子列表项响应
 */
package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityPostMineItemResponse {

    /** 帖子ID */
    private Long id;
    /** 帖子标题 */
    private String title;
    /** URL Slug */
    private String slug;
    /** 帖子状态 */
    private String status;
    /** 所属节点信息 */
    private CommunityPostNodeSummaryResponse node;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;
    /** 发布时间 */
    private LocalDateTime publishedAt;
    /** 浏览次数 */
    private Long viewCount;

    /**
     * 从帖子实体转换为我的帖子列表项响应
     *
     * @param post 帖子实体
     * @param node 节点摘要信息
     * @return 响应对象
     */
    public static CommunityPostMineItemResponse from(CommunityPost post, CommunityPostNodeSummaryResponse node) {
        return CommunityPostMineItemResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .status(post.getStatus().name())
                .node(node)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .publishedAt(post.getPublishedAt())
                .viewCount(post.getViewCount())
                .build();
    }
}

