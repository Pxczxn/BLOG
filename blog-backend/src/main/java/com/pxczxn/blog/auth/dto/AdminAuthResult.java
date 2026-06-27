package com.pxczxn.blog.auth.dto;

import java.time.Instant;

public record AdminAuthResult(
        LoginResponse response,
        String refreshToken,
        Instant refreshExpiresAt
) {
    public Long getUserId() {
        return response.getUserId();
    }

    public String getUsername() {
        return response.getUsername();
    }

    public String getEmail() {
        return response.getEmail();
    }

    public String getRole() {
        return response.getRole();
    }

    public String getToken() {
        return response.getToken();
    }

    public Instant getExpiresAt() {
        return response.getExpiresAt();
    }
}
