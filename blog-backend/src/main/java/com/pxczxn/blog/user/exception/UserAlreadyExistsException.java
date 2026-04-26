/**
 * 用户已存在异常
 * <p>
 * 当创建用户时用户名或邮箱已被占用时抛出
 */
package com.pxczxn.blog.user.exception;

/**
 * 用户已存在异常
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * 构造异常
     *
     * @param message 异常信息
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * 构造异常，自动生成格式化的异常信息
     *
     * @param field 冲突的字段名称（如"用户名"、"邮箱"）
     * @param value 冲突的字段值
     */
    public UserAlreadyExistsException(String field, String value) {
        super(String.format("%s '%s' 已存在", field, value));
    }
}
