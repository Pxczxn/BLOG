/**
 * 审核任务实体
 * <p>
 * 记录每条内容的审核流程，包括提交人、审核状态、风险级别和审核决定等信息
 */
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

    /** 任务 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 内容类型 */
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private ModerationContentType contentType;

    /** 内容 ID */
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    /** 提交者用户 ID */
    @Column(name = "submitted_by")
    private Long submittedBy;

    /** 内容标题快照 */
    @Column(name = "title_snapshot", length = 220)
    private String titleSnapshot;

    /** 审核状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ModerationTaskStatus status = ModerationTaskStatus.PENDING;

    /** 风险级别 */
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 10)
    @Builder.Default
    private ModerationRiskLevel riskLevel = ModerationRiskLevel.LOW;

    /** 规则命中次数 */
    @Column(name = "hit_count", nullable = false)
    @Builder.Default
    private Integer hitCount = 0;

    /** 审核备注 */
    @Column(name = "decision_note", length = 500)
    private String decisionNote;

    /** 审核人（管理员）ID */
    @Column(name = "reviewed_by")
    private Long reviewedBy;

    /** 提交时间 */
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    /** 审核时间 */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
