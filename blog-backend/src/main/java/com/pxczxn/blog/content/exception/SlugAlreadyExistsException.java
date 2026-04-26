/**
 * 文章Slug已存在异常
 * <p>
 * 当创建或更新文章时，如果slug已被其他文章占用则抛出此异常。
 */
package com.pxczxn.blog.content.exception;

public class SlugAlreadyExistsException extends RuntimeException {

    /** 已存在的slug值 */
    private final String slug;

    /**
     * 根据slug构造异常
     *
     * @param slug 已存在的slug值
     */
    public SlugAlreadyExistsException(String slug) {
        super("Article slug already exists: " + slug);
        this.slug = slug;
    }

    /**
     * 获取已存在的slug值
     *
     * @return slug值
     */
    public String getSlug() {
        return slug;
    }
}
