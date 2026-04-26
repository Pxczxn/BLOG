/**
 * 公开帖子列表项响应
 */
package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PublicCommunityPostListItemResponse {

    /** 帖子ID */
    private Long id;
    /** 帖子标题 */
    private String title;
    /** URL Slug */
    private String slug;
    /** 帖子摘要 */
    private String summary;
    /** 所属节点信息 */
    private CommunityPostNodeSummaryResponse node;
    /** 作者信息 */
    private CommunityPostAuthorSummaryResponse author;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 发布时间 */
    private LocalDateTime publishedAt;
    /** 浏览次数 */
    private Long viewCount;
    /** 点赞数 */
    private long likeCount;
    /** 收藏数 */
    private long favoriteCount;

    /**
     * 从帖子实体转换为公开列表项响应
     *
     * @param post          帖子实体
     * @param node          节点摘要信息
     * @param author        作者摘要信息
     * @param likeCount     点赞数
     * @param favoriteCount 收藏数
     * @return 响应对象
     */
    public static PublicCommunityPostListItemResponse from(CommunityPost post,
                                                           CommunityPostNodeSummaryResponse node,
                                                           CommunityPostAuthorSummaryResponse author,
                                                           long likeCount,
                                                           long favoriteCount) {
        return PublicCommunityPostListItemResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .summary(post.getSummary())
                .node(node)
                .author(author)
                .createdAt(post.getCreatedAt())
                .publishedAt(post.getPublishedAt())
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .favoriteCount(favoriteCount)
                .build();
    }
}

