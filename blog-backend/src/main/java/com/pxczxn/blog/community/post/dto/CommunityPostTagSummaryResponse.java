package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.tag.entity.Tag;

public record CommunityPostTagSummaryResponse(
        Long id,
        String name,
        String slug
) {
    public static CommunityPostTagSummaryResponse from(Tag tag) {
        return new CommunityPostTagSummaryResponse(
                tag.getId(),
                tag.getName(),
                tag.getSlug()
        );
    }
}
