/**
 * 帖子评论项响应
 */
package com.pxczxn.blog.community.post.comment.dto;

import com.pxczxn.blog.community.post.comment.entity.CommunityPostComment;

import java.time.LocalDateTime;

public record CommunityPostCommentItemResponse(
        /** 评论ID */
        Long id,
        /** 父评论ID */
        Long parentId,
        /** 回复目标昵称 */
        String replyToNickname,
        /** 评论用户ID */
        Long communityUserId,
        /** 评论者昵称 */
        String nickname,
        /** 评论者头像 */
        String avatar,
        /** 评论者用户名 */
        String profileUsername,
        /** 评论内容 */
        String content,
        /** 评论状态 */
        String status,
        /** 创建时间 */
        LocalDateTime createdAt
) {
    /**
     * 从评论实体转换为响应对象
     *
     * @param comment         评论实体
     * @param replyToNickname 回复目标昵称
     * @param avatar          评论者头像
     * @param profileUsername 评论者用户名
     * @return 响应对象
     */
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

