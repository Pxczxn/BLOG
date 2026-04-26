




package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ModerationTaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminModerationDecisionRequest {

    
    @NotNull(message = "审核决定不能为空")
    private ModerationTaskStatus decision;

    
    @Size(max = 500, message = "审核备注长度不能超过500")
    private String decisionNote;
}
