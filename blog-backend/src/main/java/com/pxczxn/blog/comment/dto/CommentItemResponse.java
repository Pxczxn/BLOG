/**
 * 评论列表项响应 DTO（公开端）
 * <p>
 * 返回评论详情，包括父评论昵称、社区用户头像和用户名等信息，用于前端展示。
 */
package com.pxczxn.blog.comment.dto;

import com.pxczxn.blog.comment.entity.Comment;

import java.time.LocalDateTime;

public record CommentItemResponse(
        /** 评论ID */
        Long id,
        /** 父评论ID */
        Long parentId,
        /** 回复目标的昵称 */
        String replyToNickname,
        /** 评论者昵称 */
        String nickname,
        /** 评论者头像 */
        String avatar,
        /** 社区用户名 */
        String profileUsername,
        /** 是否为社区用户 */
        boolean communityUser,
        /** 评论内容 */
        String content,
        /** 创建时间 */
        LocalDateTime createdAt
) {
    /**
     * 将评论实体转换为响应DTO
     *
     * @param comment         评论实体
     * @param replyToNickname 回复目标的昵称
     * @param avatar          评论者头像
     * @param profileUsername 社区用户名
     * @param communityUser   是否为社区用户
     * @return 响应DTO
     */
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

