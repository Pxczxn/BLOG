/**
 * 分类更新响应 DTO
 * <p>
 * 返回更新后分类的ID、名称、slug和更新时间。
 */
package com.pxczxn.blog.category.dto;

import com.pxczxn.blog.category.entity.Category;

import java.time.LocalDateTime;

public record CategoryUpdateResponse(
        /** 分类ID */
        Long id,
        /** 分类名称 */
        String name,
        /** URL友好的分类标识 */
        String slug,
        /** 更新时间 */
        LocalDateTime updatedAt
) {
    /**
     * 将分类实体转换为更新响应DTO
     *
     * @param category 分类实体
     * @return 分类更新响应
     */
    public static CategoryUpdateResponse from(Category category) {
        return new CategoryUpdateResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getUpdatedAt()
        );
    }
}

