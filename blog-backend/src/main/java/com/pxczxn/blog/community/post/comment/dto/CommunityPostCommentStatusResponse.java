package com.pxczxn.blog.community.post.comment.dto;

import com.pxczxn.blog.community.post.comment.entity.CommunityPostComment;
import com.pxczxn.blog.community.post.comment.entity.CommunityPostCommentStatus;

public record CommunityPostCommentStatusResponse(Long id, CommunityPostCommentStatus status) {
    public static CommunityPostCommentStatusResponse from(CommunityPostComment comment) {
        return new CommunityPostCommentStatusResponse(comment.getId(), comment.getStatus());
    }
}
