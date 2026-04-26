




package com.pxczxn.blog.auth.dto;

import com.pxczxn.blog.common.response.ApiErrorCode;
import io.jsonwebtoken.Claims;










public record TokenValidationResult(boolean valid, Claims claims, ApiErrorCode errorCode) {

    
    public static TokenValidationResult valid(Claims claims) {
        return new TokenValidationResult(true, claims, null);
    }

    
    public static TokenValidationResult invalid(ApiErrorCode errorCode) {
        return new TokenValidationResult(false, null, errorCode);
    }
}

