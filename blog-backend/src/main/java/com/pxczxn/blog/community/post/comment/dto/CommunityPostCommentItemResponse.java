


package com.pxczxn.blog.community.post.comment.dto;

import com.pxczxn.blog.community.post.comment.entity.CommunityPostComment;

import java.time.LocalDateTime;

public record CommunityPostCommentItemResponse(
        
        Long id,
        
        Long parentId,
        
        String replyToNickname,
        
        Long communityUserId,
        
        String nickname,
        
        String avatar,
        
        String profileUsername,
        
        String content,
        
        String status,
        
        LocalDateTime createdAt
) {
    








    public static CommunityPostCommentItemResponse from(CommunityPostComment comment,
                                                        String replyToNickname,
                                                        String avatar,
                                                        String profileUsername) {
        return new CommunityPostCommentItemResponse(
                comment.getId(),
                comment.getParentId(),
                replyToNickname,
                comment.getCommunityUserId(),
                comment.getNickname(),
                avatar,
                profileUsername,
                comment.getContent(),
                comment.getStatus().name(),
                comment.getCreatedAt()
        );
    }
}

