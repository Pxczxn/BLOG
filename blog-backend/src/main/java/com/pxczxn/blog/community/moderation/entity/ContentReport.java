




package com.pxczxn.blog.community.moderation.entity;

import com.pxczxn.blog.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "content_report")
public class ContentReport extends BaseTimeEntity {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private ModerationContentType contentType;

    
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    
    @Column(name = "reporter_user_id", nullable = false)
    private Long reporterUserId;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 20)
    @Builder.Default
    private ReportReason reason = ReportReason.OTHER;

    
    @Column(name = "description", length = 500)
    private String description;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ReportStatus status = ReportStatus.OPEN;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "handle_action", nullable = false, length = 20)
    @Builder.Default
    private ReportHandleAction handleAction = ReportHandleAction.NONE;

    
    @Column(name = "handle_note", length = 500)
    private String handleNote;

    
    @Column(name = "handled_by")
    private Long handledBy;

    
    @Column(name = "handled_at")
    private LocalDateTime handledAt;
}
