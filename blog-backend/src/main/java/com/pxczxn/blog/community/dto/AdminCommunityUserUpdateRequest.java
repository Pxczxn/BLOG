package com.pxczxn.blog.community.dto;

import com.pxczxn.blog.community.entity.CommunityUserRole;
import com.pxczxn.blog.community.entity.CommunityUserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminCommunityUserUpdateRequest {

    @NotNull
    private CommunityUserRole role;

    @NotNull
    private CommunityUserStatus status;
}
