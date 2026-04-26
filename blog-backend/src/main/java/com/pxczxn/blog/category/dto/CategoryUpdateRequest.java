/**
 * 分类更新请求 DTO
 * <p>
 * 包含待更新的分类名称，名称不能为空且最大长度100。
 */
package com.pxczxn.blog.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateRequest {

    /**
     * 分类名称，不能为空，最大长度100
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100")
    private String name;
}

