




package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ModerationTask;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminModerationTaskItemResponse {

    
    private Long id;
    
    private String contentType;
    
    private Long contentId;
    
    private String titleSnapshot;
    
    private String status;
    
    private String riskLevel;
    
    private Integer hitCount;
    
    private String submittedBy;
    
    private String reviewedBy;
    
    private String decisionNote;
    
    private LocalDateTime submittedAt;
    
    private LocalDateTime reviewedAt;

    







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
