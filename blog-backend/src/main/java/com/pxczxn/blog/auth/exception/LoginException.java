package com.pxczxn.blog.auth.exception;

public class LoginException extends RuntimeException {

    private final String errorCode;

    public LoginException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static LoginException userNotFound() {
        return new LoginException("USER_NOT_FOUND", "用户不存在");
    }

    public static LoginException invalidPassword() {
        return new LoginException("INVALID_PASSWORD", "密码错误");
    }

    public static LoginException accountLocked() {
        return new LoginException("ACCOUNT_LOCKED", "账号已锁定，请稍后再试");
    }

    public static LoginException accountBanned() {
        return new LoginException("ACCOUNT_BANNED", "账号已被禁用");
    }
}
