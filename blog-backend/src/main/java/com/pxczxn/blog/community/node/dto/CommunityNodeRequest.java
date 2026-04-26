package com.pxczxn.blog.community.node.dto;

import com.pxczxn.blog.community.node.entity.CommunityNodeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 社区节点请求DTO
 * <p>
 * 用于创建和更新社区节点
 */
@Data
public class CommunityNodeRequest {

    /**
     * 节点名称
     */
    @NotBlank(message = "节点名称不能为空")
    @Size(max = 80, message = "节点名称长度不能超过80")
    private String name;

    /**
     * URL别名，用于构建友好的URL路径
     */
    @NotBlank(message = "URL别名不能为空")
    @Size(max = 80, message = "URL别名长度不能超过80")
    private String slug;

    /**
     * 节点描述
     */
    @Size(max = 255, message = "节点描述长度不能超过255")
    private String description;

    /**
     * 图标名称
     */
    @Size(max = 50, message = "图标名称长度不能超过50")
    private String icon;

    /**
     * 排序顺序，数值越小越靠前
     */
    private Integer sortOrder;

    /**
     * 节点状态
     */
    private CommunityNodeStatus status;
}
