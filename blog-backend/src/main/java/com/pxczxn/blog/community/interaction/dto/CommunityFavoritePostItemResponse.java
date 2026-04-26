/**
 * 社区收藏帖子列表项响应DTO
 */
package com.pxczxn.blog.community.interaction.dto;

import com.pxczxn.blog.community.post.dto.CommunityPostAuthorSummaryResponse;
import com.pxczxn.blog.community.post.dto.CommunityPostNodeSummaryResponse;
import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityFavoritePostItemResponse {

    /** 帖子ID */
    private Long id;
    /** 帖子标题 */
    private String title;
    /** 帖子slug */
    private String slug;
    /** 帖子摘要 */
    private String summary;
    /** 所属节点信息 */
    private CommunityPostNodeSummaryResponse node;
    /** 作者信息 */
    private CommunityPostAuthorSummaryResponse author;
    /** 发布时间 */
    private LocalDateTime publishedAt;
    /** 收藏时间 */
    private LocalDateTime favoritedAt;
    /** 点赞数 */
    private long likeCount;
    /** 收藏数 */
    private long favoriteCount;

    /**
     * 根据帖子实体和相关数据构建响应
     *
     * @param post          帖子实体
     * @param node          节点摘要信息
     * @param author        作者摘要信息
     * @param favoritedAt   收藏时间
     * @param likeCount     点赞数
     * @param favoriteCount 收藏数
     * @return 收藏帖子列表项响应
     */
    public static CommunityFavoritePostItemResponse from(CommunityPost post,
                                                         CommunityPostNodeSummaryResponse node,
                                                         CommunityPostAuthorSummaryResponse author,
                                                         LocalDateTime favoritedAt,
                                                         long likeCount,
                                                         long favoriteCount) {
        return CommunityFavoritePostItemResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .summary(post.getSummary())
                .node(node)
                .author(author)
                .publishedAt(post.getPublishedAt())
                .favoritedAt(favoritedAt)
                .likeCount(likeCount)
                .favoriteCount(favoriteCount)
                .build();
    }
}

