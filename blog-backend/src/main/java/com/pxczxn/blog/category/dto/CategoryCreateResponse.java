




package com.pxczxn.blog.category.dto;

import com.pxczxn.blog.category.entity.Category;

import java.time.LocalDateTime;

public record CategoryCreateResponse(
        
        Long id,
        
        String name,
        
        String slug,
        
        LocalDateTime createdAt
) {
    





    public static CategoryCreateResponse from(Category category) {
        return new CategoryCreateResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getCreatedAt()
        );
    }
}

