/**
 * 公开评论响应 DTO
 * <p>
 * 返回评论的基本信息，包括ID、父评论ID、昵称、内容和创建时间。
 */
package com.pxczxn.blog.comment.dto;

import com.pxczxn.blog.comment.entity.Comment;

import java.time.LocalDateTime;

public record PublicCommentResponse(
        /** 评论ID */
        Long id,
        /** 父评论ID */
        Long parentId,
        /** 评论者昵称 */
        String nickname,
        /** 评论内容 */
        String content,
        /** 创建时间 */
        LocalDateTime createdAt
) {
    /**
     * 将评论实体转换为响应DTO
     *
     * @param comment 评论实体
     * @return 响应DTO
     */
    public static PublicCommentResponse from(Comment comment) {
        return new PublicCommentResponse(
                comment.getId(),
                comment.getParentId(),
                comment.getNickname(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}

