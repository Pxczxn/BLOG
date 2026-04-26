/**
 * 分类公开响应 DTO
 * <p>
 * 用于公开接口返回分类信息，包含ID、名称、slug和创建时间。
 */
package com.pxczxn.blog.category.dto;

import com.pxczxn.blog.category.entity.Category;

import java.time.LocalDateTime;

public record CategoryPublicResponse(
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
     * 将分类实体转换为公开响应DTO
     *
     * @param category 分类实体
     * @return 分类公开响应
     */
    public static CategoryPublicResponse from(Category category) {
        return new CategoryPublicResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getCreatedAt()
        );
    }
}

