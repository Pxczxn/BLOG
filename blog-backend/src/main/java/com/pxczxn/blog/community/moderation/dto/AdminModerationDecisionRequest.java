/**
 * 审核决定请求 DTO
 * <p>
 * 用于管理员提交对审核任务的决定（通过或拒绝）
 */
package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ModerationTaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminModerationDecisionRequest {

    /** 审核决定（APPROVED 或 REJECTED） */
    @NotNull(message = "审核决定不能为空")
    private ModerationTaskStatus decision;

    /** 审核备注 */
    @Size(max = 500, message = "审核备注长度不能超过500")
    private String decisionNote;
}
