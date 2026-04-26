/**
 * 社区通知列表项响应DTO
 */
package com.pxczxn.blog.community.interaction.dto;

import com.pxczxn.blog.community.interaction.entity.CommunityNotification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityNotificationItemResponse {

    /** 通知ID */
    private Long id;
    /** 通知类型 */
    private String type;
    /** 通知标题 */
    private String title;
    /** 通知内容 */
    private String content;
    /** 操作者用户ID */
    private Long actorUserId;
    /** 关联帖子ID */
    private Long relatedPostId;
    /** 关联评论ID */
    private Long relatedCommentId;
    /** 关联帖子评论ID */
    private Long relatedPostCommentId;
    /** 是否已读 */
    private boolean read;
    /** 已读时间 */
    private LocalDateTime readAt;
    /** 创建时间 */
    private LocalDateTime createdAt;

    /**
     * 根据通知实体构建响应
     *
     * @param notification 通知实体
     * @return 通知列表项响应
     */
    public static CommunityNotificationItemResponse from(CommunityNotification notification) {
        return CommunityNotificationItemResponse.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .title(notification.getTitle())
                .content(notification.getContent())
                .actorUserId(notification.getActorUserId())
                .relatedPostId(notification.getRelatedPostId())
                .relatedCommentId(notification.getRelatedCommentId())
                .relatedPostCommentId(notification.getRelatedPostCommentId())
                .read(Boolean.TRUE.equals(notification.getIsRead()))
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

