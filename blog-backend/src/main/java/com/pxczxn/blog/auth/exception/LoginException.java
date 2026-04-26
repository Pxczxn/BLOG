/**
 * 登录异常
 * <p>
 * 登录过程中发生的业务异常，如用户名密码错误、账号被锁定、账号被禁用等。
 * 包含错误码，便于前端统一处理错误信息。
 */
package com.pxczxn.blog.auth.exception;

import com.pxczxn.blog.common.response.ApiErrorCode;
import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {

    /** 错误码，用于前端识别错误类型 */
    private final ApiErrorCode errorCode;

    /**
     * 构造登录异常
     * @param errorCode 错误码
     * @param message   错误信息
     */
    public LoginException(ApiErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /** 创建"用户名或密码错误"异常 */
    public static LoginException invalidCredentials() {
        return new LoginException(ApiErrorCode.AUTH_INVALID_CREDENTIALS, ApiErrorCode.AUTH_INVALID_CREDENTIALS.getDefaultMessage());
    }

    /** 创建"账号已锁定"异常 */
    public static LoginException accountLocked() {
        return new LoginException(ApiErrorCode.AUTH_ACCOUNT_LOCKED, ApiErrorCode.AUTH_ACCOUNT_LOCKED.getDefaultMessage());
    }

    /** 创建"账号已禁用"异常 */
    public static LoginException accountBanned() {
        return new LoginException(ApiErrorCode.AUTH_ACCOUNT_DISABLED, ApiErrorCode.AUTH_ACCOUNT_DISABLED.getDefaultMessage());
    }
}

