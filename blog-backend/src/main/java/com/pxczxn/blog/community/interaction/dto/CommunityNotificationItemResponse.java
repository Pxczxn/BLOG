


package com.pxczxn.blog.community.interaction.dto;

import com.pxczxn.blog.community.interaction.entity.CommunityNotification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityNotificationItemResponse {

    
    private Long id;
    
    private String type;
    
    private String title;
    
    private String content;
    
    private Long actorUserId;
    
    private Long relatedPostId;
    
    private Long relatedCommentId;
    
    private Long relatedPostCommentId;
    
    private boolean read;
    
    private LocalDateTime readAt;
    
    private LocalDateTime createdAt;

    





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

