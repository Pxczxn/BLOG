/**
 * 举报列表项响应 DTO
 * <p>
 * 用于举报列表展示，提供简要信息
 */
package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ContentReport;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminReportItemResponse {

    /** 举报 ID */
    private Long id;
    /** 内容类型 */
    private String contentType;
    /** 内容 ID */
    private Long contentId;
    /** 举报原因 */
    private String reason;
    /** 举报描述 */
    private String description;
    /** 处理状态 */
    private String status;
    /** 处理动作 */
    private String handleAction;
    /** 处理说明 */
    private String handleNote;
    /** 举报者名称 */
    private String reporter;
    /** 处理人名称 */
    private String handledBy;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 处理时间 */
    private LocalDateTime handledAt;

    /**
     * 从实体转换为响应对象
     *
     * @param report   举报实体
     * @param reporter 举报者名称
     * @param handledBy 处理人名称
     * @return 响应对象
     */
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
