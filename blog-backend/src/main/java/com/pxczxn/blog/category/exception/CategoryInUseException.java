/**
 * 分类正在使用异常
 * <p>
 * 当试图删除仍有文章引用的分类时抛出此异常。
 */
package com.pxczxn.blog.category.exception;

public class CategoryInUseException extends RuntimeException {

    /**
     * 构造分类正在使用异常
     *
     * @param id 被引用的分类ID
     */
    public CategoryInUseException(Long id) {
        super("Category is in use: id=" + id);
    }
}

