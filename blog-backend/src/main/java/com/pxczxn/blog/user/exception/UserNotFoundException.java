/**
 * 用户不存在异常
 * <p>
 * 当查询的用户不存在时抛出
 */
package com.pxczxn.blog.user.exception;

/**
 * 用户不存在异常
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * 根据用户ID构造异常
     *
     * @param id 用户ID
     */
    public UserNotFoundException(Long id) {
        super(String.format("用户 ID '%d' 不存在", id));
    }

    /**
     * 根据用户标识符（用户名或邮箱）构造异常
     *
     * @param identifier 用户标识符
     */
    public UserNotFoundException(String identifier) {
        super(String.format("用户 '%s' 不存在", identifier));
    }
}
