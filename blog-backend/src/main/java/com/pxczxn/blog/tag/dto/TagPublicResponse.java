package com.pxczxn.blog.tag.dto;

import com.pxczxn.blog.tag.entity.Tag;

import java.time.LocalDateTime;

public record TagPublicResponse(
        Long id,
        String name,
        String slug,
        Long articleCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TagPublicResponse from(Tag tag, Long articleCount) {
        return new TagPublicResponse(
                tag.getId(),
                tag.getName(),
                tag.getSlug(),
                articleCount == null ? 0L : articleCount,
                tag.getCreatedAt(),
                tag.getUpdatedAt()
        );
    }
}
