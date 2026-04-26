/**
 * 文章标签更新请求 DTO
 * <p>
 * 管理端更新文章关联标签时提交的请求数据，包含标签ID列表。
 */
package com.pxczxn.blog.content.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleTagUpdateRequest {

    /** 标签ID列表，替换文章当前所有关联标签 */
    private List<Long> tagIds;
}
