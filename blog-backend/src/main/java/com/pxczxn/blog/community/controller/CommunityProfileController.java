


package com.pxczxn.blog.community.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.dto.CommunityProfileResponse;
import com.pxczxn.blog.community.dto.CommunityProfileUpdateRequest;
import com.pxczxn.blog.community.dto.PublicCommunityProfileResponse;
import com.pxczxn.blog.community.service.CommunityProfileService;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommunityProfileController {

    private final CommunityProfileService communityProfileService;

    





    @GetMapping("/api/community/me")
    public Result<CommunityProfileResponse> getMe(Authentication authentication) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityProfileService.getMe(principal.userId()));
    }

    






    @PatchMapping("/api/community/me")
    public Result<CommunityProfileResponse> updateMe(Authentication authentication,
                                                    @Valid @RequestBody CommunityProfileUpdateRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityProfileService.updateMe(principal.userId(), request));
    }

    






    @GetMapping("/api/public/users/{username}")
    public Result<PublicCommunityProfileResponse> getPublicProfile(Authentication authentication,
                                                                   @PathVariable String username) {
        Long viewerUserId = null;
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal && principal.isCommunityUser()) {
            viewerUserId = principal.userId();
        }
        return Result.success(communityProfileService.getPublicProfile(username, viewerUserId));
    }
}
