/**
 * 文章标签摘要响应 DTO
 * <p>
 * 在文章详情中展示的关联标签摘要信息，仅包含标签的基本标识。
 */
package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.tag.entity.Tag;

public record ArticleTagSummaryResponse(
        /** 标签ID */
        Long id,
        /** 标签名称 */
        String name,
        /** 标签slug标识 */
        String slug
) {
    /**
     * 从标签实体构建响应
     *
     * @param tag 标签实体
     * @return 标签摘要响应
     */
    public static ArticleTagSummaryResponse from(Tag tag) {
        return new ArticleTagSummaryResponse(
                tag.getId(),
                tag.getName(),
                tag.getSlug()
        );
    }
}
