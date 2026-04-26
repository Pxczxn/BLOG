/**
 * 标签未找到异常
 */
package com.pxczxn.blog.tag.exception;

/**
 * 当根据ID查找标签不存在时抛出此异常
 */
public class TagNotFoundException extends RuntimeException {

    /**
     * 根据标签ID构造异常
     *
     * @param id 标签ID
     */
    public TagNotFoundException(Long id) {
        super("Tag not found: " + id);
    }
}

