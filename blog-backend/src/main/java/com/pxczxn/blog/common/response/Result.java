





package com.pxczxn.blog.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result<T> {

    
    private int code;

    
    private String error;

    
    private String message;

    
    private T data;

    






    public Result(int code, String message, T data) {
        this(code, null, message, data);
    }

    







    public Result(int code, String error, String message, T data) {
        this.code = code;
        this.error = error;
        this.message = message;
        this.data = data;
    }

    





    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    






    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    





    public static <T> Result<T> error(ApiErrorCode errorCode) {
        return error(errorCode, errorCode.getDefaultMessage());
    }

    






    public static <T> Result<T> error(ApiErrorCode errorCode, String message) {
        return new Result<>(errorCode.getHttpStatus().value(), errorCode.getError(), message, null);
    }

    







    public static <T> Result<T> error(ApiErrorCode errorCode, String message, T data) {
        return new Result<>(errorCode.getHttpStatus().value(), errorCode.getError(), message, data);
    }

    






    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, null, message, null);
    }

    







    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, null, message, data);
    }
}
