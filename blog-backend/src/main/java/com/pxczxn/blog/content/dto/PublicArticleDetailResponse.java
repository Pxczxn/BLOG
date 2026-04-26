




package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.Article;

import java.time.LocalDateTime;
import java.util.List;

public record PublicArticleDetailResponse(
        
        Long id,
        
        String title,
        
        String slug,
        
        String summary,
        
        String content,
        
        String coverImage,
        
        Long viewCount,
        
        ArticleCategorySummaryResponse category,
        
        List<ArticleTagSummaryResponse> tags,
        
        LocalDateTime createdAt
) {
    







    public static PublicArticleDetailResponse from(
            Article article,
            ArticleCategorySummaryResponse category,
            List<ArticleTagSummaryResponse> tags
    ) {
        return new PublicArticleDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getSummary(),
                article.getContent(),
                article.getCoverImage(),
                article.getViewCount(),
                category,
                tags,
                article.getCreatedAt()
        );
    }
}
