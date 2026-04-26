/**
 * 令牌验证结果
 * <p>
 * 封装 JWT 令牌的验证结果，包括是否有效、解析出的 Claims 以及验证失败时的错误码。
 */
package com.pxczxn.blog.auth.dto;

import com.pxczxn.blog.common.response.ApiErrorCode;
import io.jsonwebtoken.Claims;

/**
 * 令牌验证结果
 * <p>
 * 封装 JWT 令牌的验证结果，包括是否有效、解析出的 Claims 以及验证失败时的错误码。
 *
 * @param valid     是否验证通过
 * @param claims    解析出的 Claims（验证通过时有值）
 * @param errorCode 错误码（验证失败时有值）
 */
public record TokenValidationResult(boolean valid, Claims claims, ApiErrorCode errorCode) {

    /** 创建验证通过的结果 */
    public static TokenValidationResult valid(Claims claims) {
        return new TokenValidationResult(true, claims, null);
    }

    /** 创建验证失败的结果 */
    public static TokenValidationResult invalid(ApiErrorCode errorCode) {
        return new TokenValidationResult(false, null, errorCode);
    }
}

