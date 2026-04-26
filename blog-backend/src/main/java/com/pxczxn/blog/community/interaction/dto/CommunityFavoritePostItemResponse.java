


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

    
    private Long id;
    
    private String title;
    
    private String slug;
    
    private String summary;
    
    private CommunityPostNodeSummaryResponse node;
    
    private CommunityPostAuthorSummaryResponse author;
    
    private LocalDateTime publishedAt;
    
    private LocalDateTime favoritedAt;
    
    private long likeCount;
    
    private long favoriteCount;

    










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

