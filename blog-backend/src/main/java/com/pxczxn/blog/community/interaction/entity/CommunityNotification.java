


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

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "user_id", nullable = false)
    private Long userId;

    
    @Column(name = "actor_user_id")
    private Long actorUserId;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private CommunityNotificationType type;

    
    @Column(name = "title", nullable = false, length = 120)
    private String title;

    
    @Column(name = "content", length = 500)
    private String content;

    
    @Column(name = "related_post_id")
    private Long relatedPostId;

    
    @Column(name = "related_comment_id")
    private Long relatedCommentId;

    
    @Column(name = "related_post_comment_id")
    private Long relatedPostCommentId;

    
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = Boolean.FALSE;

    
    @Column(name = "read_at")
    private LocalDateTime readAt;

    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    
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

