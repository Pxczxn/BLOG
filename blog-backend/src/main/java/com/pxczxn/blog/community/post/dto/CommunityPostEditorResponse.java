


package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityPostEditorResponse {

    
    private Long id;
    
    private Long nodeId;
    
    private String title;
    
    private String slug;
    
    private String summary;
    
    private String content;
    
    private String status;
    
    private String rejectionReason;
    
    private Long viewCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime publishedAt;

    





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

