/**
 * 评论不存在异常
 * <p>
 * 当操作的评论ID在数据库中不存在时抛出此异常。
 */
package com.pxczxn.blog.comment.exception;

public class CommentNotFoundException extends RuntimeException {

    /**
     * 根据评论ID构造异常
     *
     * @param id 评论ID
     */
    public CommentNotFoundException(Long id) {
        super("Comment not found: id=" + id);
    }
}

