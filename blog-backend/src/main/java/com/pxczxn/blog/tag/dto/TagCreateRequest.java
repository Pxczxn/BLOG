/**
 * 标签创建请求 DTO
 */
package com.pxczxn.blog.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagCreateRequest {

    /** 标签名称 */
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 100, message = "标签名称长度不能超过100")
    private String name;
}

