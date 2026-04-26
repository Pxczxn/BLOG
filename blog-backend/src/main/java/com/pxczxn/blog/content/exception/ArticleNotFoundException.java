/**
 * 文章未找到异常
 * <p>
 * 当根据ID或slug查询文章不存在时抛出此异常。
 */
package com.pxczxn.blog.content.exception;

public class ArticleNotFoundException extends RuntimeException {

    /** 查询时使用的标识符（ID或slug） */
    private final Object identifier;

    /**
     * 根据文章ID构造异常
     *
     * @param id 未找到的文章ID
     */
    public ArticleNotFoundException(Long id) {
        super("Article not found: id=" + id);
        this.identifier = id;
    }

    /**
     * 根据文章slug构造异常
     *
     * @param slug 未找到的文章slug
     */
    public ArticleNotFoundException(String slug) {
        super("Article not found: slug=" + slug);
        this.identifier = slug;
    }

    /**
     * 获取查询时使用的标识符
     *
     * @return 标识符（ID或slug）
     */
    public Object getIdentifier() {
        return identifier;
    }
}
