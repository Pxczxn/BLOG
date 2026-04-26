/**
 * 分类创建响应 DTO
 * <p>
 * 返回新创建分类的ID、名称、slug和创建时间。
 */
package com.pxczxn.blog.category.dto;

import com.pxczxn.blog.category.entity.Category;

import java.time.LocalDateTime;

public record CategoryCreateResponse(
        /** 分类ID */
        Long id,
        /** 分类名称 */
        String name,
        /** URL友好的分类标识 */
        String slug,
        /** 创建时间 */
        LocalDateTime createdAt
) {
    /**
     * 将分类实体转换为创建响应DTO
     *
     * @param category 分类实体
     * @return 分类创建响应
     */
    public static CategoryCreateResponse from(Category category) {
        return new CategoryCreateResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getCreatedAt()
        );
    }
}

