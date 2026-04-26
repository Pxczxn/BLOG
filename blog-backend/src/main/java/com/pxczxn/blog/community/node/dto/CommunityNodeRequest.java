package com.pxczxn.blog.community.node.dto;

import com.pxczxn.blog.community.node.entity.CommunityNodeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;






@Data
public class CommunityNodeRequest {

    


    @NotBlank(message = "节点名称不能为空")
    @Size(max = 80, message = "节点名称长度不能超过80")
    private String name;

    


    @NotBlank(message = "URL别名不能为空")
    @Size(max = 80, message = "URL别名长度不能超过80")
    private String slug;

    


    @Size(max = 255, message = "节点描述长度不能超过255")
    private String description;

    


    @Size(max = 50, message = "图标名称长度不能超过50")
    private String icon;

    


    private Integer sortOrder;

    


    private CommunityNodeStatus status;
}
