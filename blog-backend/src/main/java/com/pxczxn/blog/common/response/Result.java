package com.pxczxn.blog.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> error(String errorCode, String message) {
        return new Result<>(getErrorCode(errorCode), message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    private static int getErrorCode(String errorCode) {
        return switch (errorCode) {
            case "USER_NOT_FOUND" -> 401;
            case "INVALID_PASSWORD" -> 402;
            case "ACCOUNT_LOCKED" -> 403;
            case "ACCOUNT_BANNED" -> 404;
            default -> 500;
        };
    }
}
