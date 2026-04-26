




package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.Article;
import com.pxczxn.blog.content.entity.ArticleStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ArticleAdminDetailResponse(
        
        Long id,
        
        String title,
        
        String slug,
        
        String summary,
        
        String content,
        
        String coverImage,
        
        ArticleStatus status,
        
        Long categoryId,
        
        List<Long> tagIds,
        
        LocalDateTime publishedAt,
        
        LocalDateTime createdAt,
        
        LocalDateTime updatedAt
) {
    






    public static ArticleAdminDetailResponse from(Article article, List<Long> tagIds) {
        return new ArticleAdminDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getSummary(),
                article.getContent(),
                article.getCoverImage(),
                article.getStatus(),
                article.getCategoryId(),
                tagIds,
                article.getPublishedAt(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}
