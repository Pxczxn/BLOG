/**
 * 评论实体
 * <p>
 * 文章评论，支持嵌套回复（通过 parentId 关联父评论）。
 * 新评论默认为待审核（PENDING）状态，需管理员审核通过后才公开显示。
 * 社区用户和游客均可发表评论。
 */
package com.pxczxn.blog.comment.entity;

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
@Table(name = "comment")
public class Comment {

    /** 评论ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 所属文章ID */
    @Column(name = "article_id", nullable = false)
    private Long articleId;

    /** 父评论ID，用于嵌套回复，为空表示顶级评论 */
    @Column(name = "parent_id")
    private Long parentId;

    /** 社区用户ID，登录用户发表时关联，游客为空 */
    @Column(name = "community_user_id")
    private Long communityUserId;

    /** 评论者昵称 */
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    /** 评论者邮箱 */
    @Column(name = "email", length = 100)
    private String email;

    /** 评论内容 */
    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    /** 审核状态：PENDING/APPROVED/REJECTED */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CommentStatus status;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 持久化前回调，自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

