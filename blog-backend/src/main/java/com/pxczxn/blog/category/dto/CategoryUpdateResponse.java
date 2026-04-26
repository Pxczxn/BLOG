




package com.pxczxn.blog.category.dto;

import com.pxczxn.blog.category.entity.Category;

import java.time.LocalDateTime;

public record CategoryUpdateResponse(
        
        Long id,
        
        String name,
        
        String slug,
        
        LocalDateTime updatedAt
) {
    





    public static CategoryUpdateResponse from(Category category) {
        return new CategoryUpdateResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getUpdatedAt()
        );
    }
}

