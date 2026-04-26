/**
 * 管理端社区互动控制器
 * <p>
 * 需要管理员权限
 */
package com.pxczxn.blog.community.interaction.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.interaction.dto.AdminInteractionOverviewResponse;
import com.pxczxn.blog.community.interaction.service.CommunityInteractionService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/admin/community/interactions")
@RequiredArgsConstructor
public class AdminCommunityInteractionController {

    private final CommunityInteractionService communityInteractionService;

    /**
     * 获取社区互动概览数据
     *
     * @param topSize 热门帖子数量限制，默认10条，范围1-50
     * @return 互动概览响应
     */
    @GetMapping("/overview")
    public Result<AdminInteractionOverviewResponse> overview(
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "数量必须大于等于1") @Max(value = 50, message = "数量必须小于等于50") int topSize) {
        return Result.success(communityInteractionService.getAdminOverview(topSize));
    }
}

