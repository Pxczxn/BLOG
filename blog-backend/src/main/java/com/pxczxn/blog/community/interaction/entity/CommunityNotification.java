/**
 * 社区通知实体
 */
package com.pxczxn.blog.community.interaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "community_notification")
public class CommunityNotification {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 通知接收者用户ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 触发通知的操作者用户ID */
    @Column(name = "actor_user_id")
    private Long actorUserId;

    /** 通知类型 */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private CommunityNotificationType type;

    /** 通知标题 */
    @Column(name = "title", nullable = false, length = 120)
    private String title;

    /** 通知内容 */
    @Column(name = "content", length = 500)
    private String content;

    /** 关联帖子ID */
    @Column(name = "related_post_id")
    private Long relatedPostId;

    /** 关联评论ID */
    @Column(name = "related_comment_id")
    private Long relatedCommentId;

    /** 关联帖子评论ID */
    @Column(name = "related_post_comment_id")
    private Long relatedPostCommentId;

    /** 是否已读 */
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = Boolean.FALSE;

    /** 已读时间 */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 持久化前设置默认值 */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = Boolean.FALSE;
        }
    }
}

