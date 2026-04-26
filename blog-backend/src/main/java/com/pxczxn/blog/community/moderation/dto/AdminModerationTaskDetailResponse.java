/**
 * 审核任务详情响应 DTO
 * <p>
 * 返回审核任务的完整详情，包括命中的规则列表
 */
package com.pxczxn.blog.community.moderation.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminModerationTaskDetailResponse {

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
    /** 命中的规则列表 */
    private List<ModerationRuleHitResponse> hits;
}
