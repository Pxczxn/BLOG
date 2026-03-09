package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.tag.entity.Tag;

public record ArticleTagSummaryResponse(
        Long id,
        String name,
        String slug
) {
    public static ArticleTagSummaryResponse from(Tag tag) {
        return new ArticleTagSummaryResponse(
                tag.getId(),
                tag.getName(),
                tag.getSlug()
        );
    }
}
