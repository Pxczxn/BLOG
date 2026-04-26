/**
 * 公开帖子详情响应
 */
package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PublicCommunityPostDetailResponse {

    /** 帖子ID */
    private Long id;
    /** 帖子标题 */
    private String title;
    /** URL Slug */
    private String slug;
    /** 帖子摘要 */
    private String summary;
    /** 帖子正文内容 */
    private String content;
    /** 所属节点信息 */
    private CommunityPostNodeSummaryResponse node;
    /** 作者信息 */
    private CommunityPostAuthorSummaryResponse author;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;
    /** 发布时间 */
    private LocalDateTime publishedAt;
    /** 浏览次数 */
    private Long viewCount;
    /** 点赞数 */
    private long likeCount;
    /** 收藏数 */
    private long favoriteCount;
    /** 当前用户是否已点赞 */
    private boolean likedByMe;
    /** 当前用户是否已收藏 */
    private boolean favoritedByMe;

    /**
     * 从帖子实体转换为公开详情响应
     *
     * @param post          帖子实体
     * @param node          节点摘要信息
     * @param author        作者摘要信息
     * @param likeCount     点赞数
     * @param favoriteCount 收藏数
     * @param likedByMe     当前用户是否已点赞
     * @param favoritedByMe 当前用户是否已收藏
     * @return 响应对象
     */
    public static PublicCommunityPostDetailResponse from(CommunityPost post,
                                                         CommunityPostNodeSummaryResponse node,
                                                         CommunityPostAuthorSummaryResponse author,
                                                         long likeCount,
                                                         long favoriteCount,
                                                         boolean likedByMe,
                                                         boolean favoritedByMe) {
        return PublicCommunityPostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .summary(post.getSummary())
                .content(post.getContent())
                .node(node)
                .author(author)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .publishedAt(post.getPublishedAt())
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .favoriteCount(favoriteCount)
                .likedByMe(likedByMe)
                .favoritedByMe(favoritedByMe)
                .build();
    }
}

