/**
 * 社区举报控制器
 * <p>
 * 提供用户举报内容的 API 接口
 */
package com.pxczxn.blog.community.moderation.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.moderation.dto.CommunityReportCreateRequest;
import com.pxczxn.blog.community.moderation.dto.CommunityReportResponse;
import com.pxczxn.blog.community.moderation.service.ModerationService;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community/reports")
@RequiredArgsConstructor
public class CommunityReportController {

    private final ModerationService moderationService;

    /**
     * 创建举报
     *
     * @param authentication 当前认证信息
     * @param request         举报创建请求
     * @return 创建的举报信息
     */
    @PostMapping
    public Result<CommunityReportResponse> createReport(Authentication authentication,
                                                        @Valid @RequestBody CommunityReportCreateRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(moderationService.createReport(principal.userId(), request));
    }
}
