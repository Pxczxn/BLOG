/**
 * 管理端帖子热度项响应DTO
 */
package com.pxczxn.blog.community.interaction.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminPostHeatItemResponse {

    /** 帖子ID */
    private Long postId;
    /** 帖子标题 */
    private String title;
    /** 帖子slug */
    private String slug;
    /** 帖子状态 */
    private String status;
    /** 点赞数 */
    private long likeCount;
    /** 收藏数 */
    private long favoriteCount;
    /** 热度分数 */
    private long heatScore;

    /**
     * 根据帖子实体和互动数据构建响应
     *
     * @param post          帖子实体
     * @param likeCount     点赞数
     * @param favoriteCount 收藏数
     * @return 帖子热度响应
     */
    public static AdminPostHeatItemResponse from(CommunityPost post, long likeCount, long favoriteCount) {
        return AdminPostHeatItemResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .status(post.getStatus().name())
                .likeCount(likeCount)
                .favoriteCount(favoriteCount)
                .heatScore(likeCount * 2 + favoriteCount)
                .build();
    }
}

