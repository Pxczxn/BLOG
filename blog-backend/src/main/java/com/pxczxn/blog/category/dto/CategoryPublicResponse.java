package com.pxczxn.blog.category.dto;

import com.pxczxn.blog.category.entity.Category;

import java.time.LocalDateTime;

public record CategoryPublicResponse(
        Long id,
        String name,
        String slug,
        LocalDateTime createdAt
) {
    public static CategoryPublicResponse from(Category category) {
        return new CategoryPublicResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getCreatedAt()
        );
    }
}
