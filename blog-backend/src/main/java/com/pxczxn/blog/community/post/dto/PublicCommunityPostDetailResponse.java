


package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PublicCommunityPostDetailResponse {

    
    private Long id;
    
    private String title;
    
    private String slug;
    
    private String summary;
    
    private String content;
    
    private CommunityPostNodeSummaryResponse node;
    
    private CommunityPostAuthorSummaryResponse author;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime publishedAt;
    
    private Long viewCount;
    
    private long likeCount;
    
    private long favoriteCount;
    
    private boolean likedByMe;
    
    private boolean favoritedByMe;

    











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

