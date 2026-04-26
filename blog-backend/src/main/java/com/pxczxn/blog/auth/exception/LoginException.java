





package com.pxczxn.blog.auth.exception;

import com.pxczxn.blog.common.response.ApiErrorCode;
import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {

    
    private final ApiErrorCode errorCode;

    




    public LoginException(ApiErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    
    public static LoginException invalidCredentials() {
        return new LoginException(ApiErrorCode.AUTH_INVALID_CREDENTIALS, ApiErrorCode.AUTH_INVALID_CREDENTIALS.getDefaultMessage());
    }

    
    public static LoginException accountLocked() {
        return new LoginException(ApiErrorCode.AUTH_ACCOUNT_LOCKED, ApiErrorCode.AUTH_ACCOUNT_LOCKED.getDefaultMessage());
    }

    
    public static LoginException accountBanned() {
        return new LoginException(ApiErrorCode.AUTH_ACCOUNT_DISABLED, ApiErrorCode.AUTH_ACCOUNT_DISABLED.getDefaultMessage());
    }
}

