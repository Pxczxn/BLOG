


package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityPostMineItemResponse {

    
    private Long id;
    
    private String title;
    
    private String slug;
    
    private String status;
    
    private CommunityPostNodeSummaryResponse node;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime publishedAt;
    
    private Long viewCount;

    






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

