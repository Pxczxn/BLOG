/**
 * REST 访问拒绝处理器
 * <p>
 * 处理权限不足请求的统一响应
 */
package com.pxczxn.blog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pxczxn.blog.common.response.ApiErrorCode;
import com.pxczxn.blog.common.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    /** JSON 序列化工具 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理访问被拒绝的情况
     * <p>
     * 当已认证用户访问无权限的资源时，返回统一的错误响应
     *
     * @param request               HTTP 请求
     * @param response              HTTP 响应
     * @param accessDeniedException 访问拒绝异常
     * @throws IOException IO 异常
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(ApiErrorCode.AUTH_FORBIDDEN.getHttpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Result.error(ApiErrorCode.AUTH_FORBIDDEN));
    }
}

