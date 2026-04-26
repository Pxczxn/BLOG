/**
 * 文章状态枚举
 * <p>
 * 定义文章的发布状态：
 * DRAFT - 草稿状态，文章尚未发布，仅管理员可见
 * PUBLISHED - 已发布状态，文章对外公开展示
 */
package com.pxczxn.blog.content.entity;

public enum ArticleStatus {
    /** 草稿状态 */
    DRAFT,
    /** 已发布状态 */
    PUBLISHED
}
