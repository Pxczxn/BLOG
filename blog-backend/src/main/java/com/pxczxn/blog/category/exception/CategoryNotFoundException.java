/**
 * 分类不存在异常
 * <p>
 * 当操作的分类ID在数据库中不存在时抛出此异常。
 */
package com.pxczxn.blog.category.exception;

public class CategoryNotFoundException extends RuntimeException {

    /**
     * 构造分类不存在异常
     *
     * @param id 未找到的分类ID
     */
    public CategoryNotFoundException(Long id) {
        super("Category not found: id=" + id);
    }
}

