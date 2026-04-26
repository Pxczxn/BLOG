


package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PublicCommunityPostListItemResponse {

    
    private Long id;
    
    private String title;
    
    private String slug;
    
    private String summary;
    
    private CommunityPostNodeSummaryResponse node;
    
    private CommunityPostAuthorSummaryResponse author;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime publishedAt;
    
    private Long viewCount;
    
    private long likeCount;
    
    private long favoriteCount;

    









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

