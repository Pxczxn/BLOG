package com.pxczxn.blog.community.dto;

import com.pxczxn.blog.community.entity.CommunityUser;
import com.pxczxn.blog.community.entity.CommunityUserRole;
import com.pxczxn.blog.community.entity.CommunityUserStatus;

import java.time.LocalDateTime;

public record AdminCommunityUserItemResponse(
        Long id,
        String username,
        String email,
        String displayName,
        String avatar,
        String bio,
        String website,
        CommunityUserRole role,
        CommunityUserStatus status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AdminCommunityUserItemResponse from(CommunityUser user) {
        return new AdminCommunityUserItemResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAvatar(),
                user.getBio(),
                user.getWebsite(),
                user.getRole(),
                user.getStatus(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
