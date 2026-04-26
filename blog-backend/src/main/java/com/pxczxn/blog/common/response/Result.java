/**
 * 统一响应结果类
 * <p>
 * 所有 API 接口的统一返回格式，包含状态码、错误标识、消息和数据。
 * 成功时 code=200，失败时根据 ApiErrorCode 返回对应的状态码和错误信息。
 */
package com.pxczxn.blog.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result<T> {

    /** HTTP 状态码 */
    private int code;

    /** 错误标识，成功时为 null */
    private String error;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    /**
     * 构造函数（无错误标识）
     *
     * @param code 状态码
     * @param message 消息
     * @param data 数据
     */
    public Result(int code, String message, T data) {
        this(code, null, message, data);
    }

    /**
     * 完整构造函数
     *
     * @param code 状态码
     * @param error 错误标识
     * @param message 消息
     * @param data 数据
     */
    public Result(int code, String error, String message, T data) {
        this.code = code;
        this.error = error;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应（使用默认消息）
     *
     * @param data 响应数据
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功响应（自定义消息）
     *
     * @param message 响应消息
     * @param data 响应数据
     * @return 成功结果
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 错误响应（使用错误码的默认消息）
     *
     * @param errorCode 错误码枚举
     * @return 错误结果
     */
    public static <T> Result<T> error(ApiErrorCode errorCode) {
        return error(errorCode, errorCode.getDefaultMessage());
    }

    /**
     * 错误响应（自定义消息）
     *
     * @param errorCode 错误码枚举
     * @param message 错误消息
     * @return 错误结果
     */
    public static <T> Result<T> error(ApiErrorCode errorCode, String message) {
        return new Result<>(errorCode.getHttpStatus().value(), errorCode.getError(), message, null);
    }

    /**
     * 错误响应（包含附加数据）
     *
     * @param errorCode 错误码枚举
     * @param message 错误消息
     * @param data 附加数据
     * @return 错误结果
     */
    public static <T> Result<T> error(ApiErrorCode errorCode, String message, T data) {
        return new Result<>(errorCode.getHttpStatus().value(), errorCode.getError(), message, data);
    }

    /**
     * 错误响应（自定义状态码）
     *
     * @param code HTTP 状态码
     * @param message 错误消息
     * @return 错误结果
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, null, message, null);
    }

    /**
     * 错误响应（自定义状态码，包含附加数据）
     *
     * @param code HTTP 状态码
     * @param message 错误消息
     * @param data 附加数据
     * @return 错误结果
     */
    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, null, message, data);
    }
}
