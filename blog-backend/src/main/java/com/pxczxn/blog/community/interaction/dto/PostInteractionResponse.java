/**
 * 帖子互动状态响应DTO
 */
package com.pxczxn.blog.community.interaction.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostInteractionResponse {

    /** 帖子ID */
    private Long postId;
    /** 点赞数 */
    private long likeCount;
    /** 收藏数 */
    private long favoriteCount;
    /** 当前用户是否已点赞 */
    private boolean likedByMe;
    /** 当前用户是否已收藏 */
    private boolean favoritedByMe;
}

