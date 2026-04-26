/**
 * 审核任务列表项响应 DTO
 * <p>
 * 用于审核任务列表展示，提供简要信息
 */
package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ModerationTask;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminModerationTaskItemResponse {

    /** 任务 ID */
    private Long id;
    /** 内容类型 */
    private String contentType;
    /** 内容 ID */
    private Long contentId;
    /** 内容标题快照 */
    private String titleSnapshot;
    /** 审核状态 */
    private String status;
    /** 风险级别 */
    private String riskLevel;
    /** 规则命中次数 */
    private Integer hitCount;
    /** 提交者名称 */
    private String submittedBy;
    /** 审核人名称 */
    private String reviewedBy;
    /** 审核备注 */
    private String decisionNote;
    /** 提交时间 */
    private LocalDateTime submittedAt;
    /** 审核时间 */
    private LocalDateTime reviewedAt;

    /**
     * 从实体转换为响应对象
     *
     * @param task       审核任务实体
     * @param submittedBy 提交者名称
     * @param reviewedBy  审核人名称
     * @return 响应对象
     */
    public static AdminModerationTaskItemResponse from(ModerationTask task, String submittedBy, String reviewedBy) {
        return AdminModerationTaskItemResponse.builder()
                .id(task.getId())
                .contentType(task.getContentType().name())
                .contentId(task.getContentId())
                .titleSnapshot(task.getTitleSnapshot())
                .status(task.getStatus().name())
                .riskLevel(task.getRiskLevel().name())
                .hitCount(task.getHitCount())
                .submittedBy(submittedBy)
                .reviewedBy(reviewedBy)
                .decisionNote(task.getDecisionNote())
                .submittedAt(task.getSubmittedAt())
                .reviewedAt(task.getReviewedAt())
                .build();
    }
}
