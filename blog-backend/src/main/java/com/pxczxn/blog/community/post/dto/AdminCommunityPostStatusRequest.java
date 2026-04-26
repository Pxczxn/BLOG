/**
 * 管理端帖子状态更新请求
 */
package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminCommunityPostStatusRequest {

    /** 目标状态 */
    @NotNull(message = "帖子状态不能为空")
    private CommunityPostStatus status;

    /** 驳回原因 */
    @Size(max = 500, message = "驳回原因长度不能超过 500 个字符")
    private String rejectionReason;
}