




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

    
    public static final String AUTH_ERROR_ATTRIBUTE = "auth.error";

    
    private final ObjectMapper objectMapper = new ObjectMapper();

    









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

