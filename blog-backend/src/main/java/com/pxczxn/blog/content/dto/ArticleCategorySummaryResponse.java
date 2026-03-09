package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.category.entity.Category;

public record ArticleCategorySummaryResponse(
        Long id,
        String name,
        String slug
) {
    public static ArticleCategorySummaryResponse from(Category category) {
        if (category == null) {
            return null;
        }

        return new ArticleCategorySummaryResponse(
                category.getId(),
                category.getName(),
                category.getSlug()
        );
    }
}
