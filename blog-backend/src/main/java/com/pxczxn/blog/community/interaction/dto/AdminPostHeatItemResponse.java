


package com.pxczxn.blog.community.interaction.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminPostHeatItemResponse {

    
    private Long postId;
    
    private String title;
    
    private String slug;
    
    private String status;
    
    private long likeCount;
    
    private long favoriteCount;
    
    private long heatScore;

    







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

