package com.pxczxn.blog.user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super(String.format("用户 ID '%d' 不存在", id));
    }

    public UserNotFoundException(String identifier) {
        super(String.format("用户 '%s' 不存在", identifier));
    }
}
