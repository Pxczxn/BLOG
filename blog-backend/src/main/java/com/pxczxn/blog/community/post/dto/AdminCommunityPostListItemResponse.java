/**
 * 管理端帖子列表项响应
 */
package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminCommunityPostListItemResponse {

    /** 帖子ID */
    private Long id;
    /** 帖子标题 */
    private String title;
    /** URL Slug */
    private String slug;
    /** 帖子状态 */
    private String status;
    /** 所属节点名称 */
    private String nodeName;
    /** 作者名称 */
    private String authorName;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 发布时间 */
    private LocalDateTime publishedAt;
    /** 浏览次数 */
    private Long viewCount;

    /**
     * 从实体转换为响应对象
     *
     * @param post       帖子实体
     * @param nodeName   节点名称
     * @param authorName 作者名称
     * @return 响应对象
     */
    public static AdminCommunityPostListItemResponse from(CommunityPost post, String nodeName, String authorName) {
        return AdminCommunityPostListItemResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .status(post.getStatus().name())
                .nodeName(nodeName)
                .authorName(authorName)
                .createdAt(post.getCreatedAt())
                .publishedAt(post.getPublishedAt())
                .viewCount(post.getViewCount())
                .build();
    }
}

