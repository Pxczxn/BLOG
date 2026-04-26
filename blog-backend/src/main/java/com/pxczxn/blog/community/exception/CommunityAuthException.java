


package com.pxczxn.blog.community.exception;

import com.pxczxn.blog.common.response.ApiErrorCode;
import lombok.Getter;

@Getter
public class CommunityAuthException extends RuntimeException {

    
    private final ApiErrorCode errorCode;

    





    public CommunityAuthException(ApiErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    




    public static CommunityAuthException invalidCredentials() {
        return new CommunityAuthException(ApiErrorCode.AUTH_INVALID_CREDENTIALS, "用户名、邮箱或密码错误");
    }

    




    public static CommunityAuthException accountDisabled() {
        return new CommunityAuthException(ApiErrorCode.AUTH_ACCOUNT_DISABLED, "账号已被禁用");
    }
}
