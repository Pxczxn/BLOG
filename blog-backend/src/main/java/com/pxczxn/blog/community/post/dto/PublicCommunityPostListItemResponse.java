


package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<CommunityPostTagSummaryResponse> tags;

    


    public static PublicCommunityPostListItemResponse from(CommunityPost post,
                                                           CommunityPostNodeSummaryResponse node,
                                                           CommunityPostAuthorSummaryResponse author,
                                                           long likeCount,
                                                           long favoriteCount) {
        return from(post, node, author, likeCount, favoriteCount, List.of());
    }

    public static PublicCommunityPostListItemResponse from(CommunityPost post,
                                                           CommunityPostNodeSummaryResponse node,
                                                           CommunityPostAuthorSummaryResponse author,
                                                           long likeCount,
                                                           long favoriteCount,
                                                           List<CommunityPostTagSummaryResponse> tags) {
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
                .tags(tags)
                .build();
    }
}

