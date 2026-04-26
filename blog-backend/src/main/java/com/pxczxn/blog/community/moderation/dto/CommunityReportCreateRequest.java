/**
 * 创建举报请求 DTO
 * <p>
 * 用于用户提交对内容的举报
 */
package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ModerationContentType;
import com.pxczxn.blog.community.moderation.entity.ReportReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityReportCreateRequest {

    /** 举报内容类型 */
    @NotNull(message = "内容类型不能为空")
    private ModerationContentType contentType;

    /** 举报内容 ID */
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    /** 举报原因 */
    @NotNull(message = "举报原因不能为空")
    private ReportReason reason;

    /** 举报描述 */
    @Size(max = 500, message = "举报描述长度不能超过500")
    private String description;
}
