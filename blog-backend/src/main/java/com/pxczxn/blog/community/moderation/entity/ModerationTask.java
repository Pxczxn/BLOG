




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
@Table(name = "moderation_task")
public class ModerationTask extends BaseTimeEntity {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private ModerationContentType contentType;

    
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    
    @Column(name = "submitted_by")
    private Long submittedBy;

    
    @Column(name = "title_snapshot", length = 220)
    private String titleSnapshot;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ModerationTaskStatus status = ModerationTaskStatus.PENDING;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 10)
    @Builder.Default
    private ModerationRiskLevel riskLevel = ModerationRiskLevel.LOW;

    
    @Column(name = "hit_count", nullable = false)
    @Builder.Default
    private Integer hitCount = 0;

    
    @Column(name = "decision_note", length = 500)
    private String decisionNote;

    
    @Column(name = "reviewed_by")
    private Long reviewedBy;

    
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
