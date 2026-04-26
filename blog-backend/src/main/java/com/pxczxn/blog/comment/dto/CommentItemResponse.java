




package com.pxczxn.blog.comment.dto;

import com.pxczxn.blog.comment.entity.Comment;

import java.time.LocalDateTime;

public record CommentItemResponse(
        
        Long id,
        
        Long parentId,
        
        String replyToNickname,
        
        String nickname,
        
        String avatar,
        
        String profileUsername,
        
        boolean communityUser,
        
        String content,
        
        LocalDateTime createdAt
) {
    









    public static CommentItemResponse from(Comment comment,
                                           String replyToNickname,
                                           String avatar,
                                           String profileUsername,
                                           boolean communityUser) {
        return new CommentItemResponse(
                comment.getId(),
                comment.getParentId(),
                replyToNickname,
                comment.getNickname(),
                avatar,
                profileUsername,
                communityUser,
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}

