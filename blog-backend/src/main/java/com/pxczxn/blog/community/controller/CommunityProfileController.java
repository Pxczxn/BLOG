/**
 * 社区用户个人资料控制器，处理当前用户资料查询/修改及公开资料查看
 */
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

    /**
     * 获取当前登录社区用户的个人资料
     *
     * @param authentication Spring Security 认证信息
     * @return 当前用户的完整个人资料
     */
    @GetMapping("/api/community/me")
    public Result<CommunityProfileResponse> getMe(Authentication authentication) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityProfileService.getMe(principal.userId()));
    }

    /**
     * 修改当前登录社区用户的个人资料
     *
     * @param authentication Spring Security 认证信息
     * @param request        个人资料更新请求（显示名称、简介、头像、个人网站）
     * @return 更新后的个人资料
     */
    @PatchMapping("/api/community/me")
    public Result<CommunityProfileResponse> updateMe(Authentication authentication,
                                                    @Valid @RequestBody CommunityProfileUpdateRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityProfileService.updateMe(principal.userId(), request));
    }

    /**
     * 根据用户名获取社区用户的公开资料，若当前访问者也是社区用户则额外返回关注状态
     *
     * @param authentication Spring Security 认证信息，可能为 null（未登录状态）
     * @param username       目标用户的用户名
     * @return 目标用户的公开资料（含粉丝数、关注数、是否已被当前用户关注）
     */
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
