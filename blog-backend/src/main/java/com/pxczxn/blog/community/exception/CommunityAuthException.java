/**
 * 社区用户认证异常，用于认证过程中发生的业务异常
 */
package com.pxczxn.blog.community.exception;

import com.pxczxn.blog.common.response.ApiErrorCode;
import lombok.Getter;

@Getter
public class CommunityAuthException extends RuntimeException {

    /** 错误码，用于前端识别具体错误类型 */
    private final ApiErrorCode errorCode;

    /**
     * 构造认证异常
     *
     * @param errorCode 错误码
     * @param message   错误消息
     */
    public CommunityAuthException(ApiErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 创建凭证无效异常（用户名、邮箱或密码错误）
     *
     * @return 凭证无效异常实例
     */
    public static CommunityAuthException invalidCredentials() {
        return new CommunityAuthException(ApiErrorCode.AUTH_INVALID_CREDENTIALS, "用户名、邮箱或密码错误");
    }

    /**
     * 创建账号已禁用异常
     *
     * @return 账号禁用异常实例
     */
    public static CommunityAuthException accountDisabled() {
        return new CommunityAuthException(ApiErrorCode.AUTH_ACCOUNT_DISABLED, "账号已被禁用");
    }
}
