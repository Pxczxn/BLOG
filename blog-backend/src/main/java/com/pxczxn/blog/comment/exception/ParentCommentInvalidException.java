/**
 * 父评论无效异常
 * <p>
 * 当回复的父评论不存在、不属于同一文章或未通过审核时抛出此异常。
 */
package com.pxczxn.blog.comment.exception;

public class ParentCommentInvalidException extends RuntimeException {

    /**
     * 构造父评论无效异常
     */
    public ParentCommentInvalidException() {
        super("Parent comment invalid");
    }
}

