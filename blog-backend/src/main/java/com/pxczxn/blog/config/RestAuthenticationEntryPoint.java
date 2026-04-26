/**
 * REST 认证入口点
 * <p>
 * 处理未认证请求的统一响应
 */
package com.pxczxn.blog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pxczxn.blog.common.response.ApiErrorCode;
import com.pxczxn.blog.common.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /** 请求属性名，用于存储认证错误类型 */
    public static final String AUTH_ERROR_ATTRIBUTE = "auth.error";

    /** JSON 序列化工具 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理认证失败的情况
     * <p>
     * 当未认证用户访问受保护资源时，返回统一的错误响应
     *
     * @param request       HTTP 请求
     * @param response      HTTP 响应
     * @param authException 认证异常
     * @throws IOException IO 异常
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ApiErrorCode errorCode = resolveErrorCode(request);
        response.setStatus(errorCode.getHttpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Result.error(errorCode));
    }

    /**
     * 解析请求中的错误码
     * <p>
     * 从请求属性中获取错误码名称，若不存在或无效则返回默认错误码
     *
     * @param request HTTP 请求
     * @return 对应的错误码
     */
    private ApiErrorCode resolveErrorCode(HttpServletRequest request) {
        Object value = request.getAttribute(AUTH_ERROR_ATTRIBUTE);
        if (value instanceof String errorName) {
            try {
                return ApiErrorCode.valueOf(errorName);
            } catch (IllegalArgumentException ignored) {
                return ApiErrorCode.AUTH_REQUIRED;
            }
        }
        return ApiErrorCode.AUTH_REQUIRED;
    }
}

