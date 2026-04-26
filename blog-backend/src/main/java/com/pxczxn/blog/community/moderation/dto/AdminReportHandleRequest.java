/**
 * 举报处理请求 DTO
 * <p>
 * 用于管理员提交对举报的处理决定
 */
package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ReportHandleAction;
import com.pxczxn.blog.community.moderation.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminReportHandleRequest {

    /** 处理状态 */
    @NotNull(message = "处理状态不能为空")
    private ReportStatus status;

    /** 处理动作 */
    private ReportHandleAction handleAction;

    /** 处理说明 */
    @Size(max = 500, message = "处理说明长度不能超过 500 个字符")
    private String handleNote;
}
