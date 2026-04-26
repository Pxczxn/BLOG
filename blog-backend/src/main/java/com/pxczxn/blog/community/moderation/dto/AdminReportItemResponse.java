




package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ContentReport;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminReportItemResponse {

    
    private Long id;
    
    private String contentType;
    
    private Long contentId;
    
    private String reason;
    
    private String description;
    
    private String status;
    
    private String handleAction;
    
    private String handleNote;
    
    private String reporter;
    
    private String handledBy;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime handledAt;

    







    public static AdminReportItemResponse from(ContentReport report, String reporter, String handledBy) {
        return AdminReportItemResponse.builder()
                .id(report.getId())
                .contentType(report.getContentType().name())
                .contentId(report.getContentId())
                .reason(report.getReason().name())
                .description(report.getDescription())
                .status(report.getStatus().name())
                .handleAction(report.getHandleAction().name())
                .handleNote(report.getHandleNote())
                .reporter(reporter)
                .handledBy(handledBy)
                .createdAt(report.getCreatedAt())
                .handledAt(report.getHandledAt())
                .build();
    }
}
