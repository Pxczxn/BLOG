/**
 * 文章分类摘要响应 DTO
 * <p>
 * 在文章详情中展示的关联分类摘要信息，仅包含分类的基本标识。
 */
package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.category.entity.Category;

public record ArticleCategorySummaryResponse(
        /** 分类ID */
        Long id,
        /** 分类名称 */
        String name,
        /** 分类slug标识 */
        String slug
) {
    /**
     * 从分类实体构建响应
     *
     * @param category 分类实体，为null时返回null
     * @return 分类摘要响应，若分类为null则返回null
     */
    public static ArticleCategorySummaryResponse from(Category category) {
        if (category == null) {
            return null;
        }

        return new ArticleCategorySummaryResponse(
                category.getId(),
                category.getName(),
                category.getSlug()
        );
    }
}
