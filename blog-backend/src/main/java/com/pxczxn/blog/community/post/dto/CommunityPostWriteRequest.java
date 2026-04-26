/**
 * 帖子写入请求（创建/更新共用）
 */
package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityPostWriteRequest {

    /** 所属节点ID */
    @NotNull(message = "节点ID不能为空")
    private Long nodeId;

    /** 帖子标题 */
    @NotBlank(message = "帖子标题不能为空")
    @Size(max = 200, message = "帖子标题长度不能超过200")
    private String title;

    /** 帖子摘要 */
    @Size(max = 500, message = "帖子摘要长度不能超过500")
    private String summary;

    /** 帖子正文内容 */
    @NotBlank(message = "帖子内容不能为空")
    private String content;

    /** 目标状态 */
    private CommunityPostStatus status;
}

