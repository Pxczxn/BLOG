/**
 * 帖子编辑器响应
 */
package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityPostEditorResponse {

    /** 帖子ID */
    private Long id;
    /** 所属节点ID */
    private Long nodeId;
    /** 帖子标题 */
    private String title;
    /** URL Slug */
    private String slug;
    /** 帖子摘要 */
    private String summary;
    /** 帖子正文内容 */
    private String content;
    /** 帖子状态 */
    private String status;
    /** 驳回原因 */
    private String rejectionReason;
    /** 浏览次数 */
    private Long viewCount;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;
    /** 发布时间 */
    private LocalDateTime publishedAt;

    /**
     * 从帖子实体转换为编辑器响应对象
     *
     * @param post 帖子实体
     * @return 编辑器响应对象
     */
    public static CommunityPostEditorResponse from(CommunityPost post) {
        return CommunityPostEditorResponse.builder()
                .id(post.getId())
                .nodeId(post.getNodeId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .summary(post.getSummary())
                .content(post.getContent())
                .status(post.getStatus().name())
                .rejectionReason(post.getRejectionReason())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .publishedAt(post.getPublishedAt())
                .build();
    }
}

