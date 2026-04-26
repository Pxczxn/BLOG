




package com.pxczxn.blog.community.moderation.controller;

import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.moderation.dto.AdminModerationDecisionRequest;
import com.pxczxn.blog.community.moderation.dto.AdminModerationTaskDetailResponse;
import com.pxczxn.blog.community.moderation.dto.AdminModerationTaskItemResponse;
import com.pxczxn.blog.community.moderation.dto.AdminReportHandleRequest;
import com.pxczxn.blog.community.moderation.dto.AdminReportItemResponse;
import com.pxczxn.blog.community.moderation.service.ModerationService;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/admin/moderation")
@RequiredArgsConstructor
public class AdminModerationController {

    private final ModerationService moderationService;

    








    @GetMapping("/tasks")
    public Result<PageResponse<AdminModerationTaskItemResponse>> listTasks(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于等于1") @Max(value = 100, message = "每页数量必须小于等于100") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        PageRequest pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return Result.success(moderationService.listTasks(pageable, page, status, type));
    }

    





    @GetMapping("/tasks/{id}")
    public Result<AdminModerationTaskDetailResponse> getTask(@PathVariable Long id) {
        return Result.success(moderationService.getTask(id));
    }

    







    @PutMapping("/tasks/{id}/decision")
    public Result<AdminModerationTaskDetailResponse> decideTask(Authentication authentication,
                                                                @PathVariable Long id,
                                                                @Valid @RequestBody AdminModerationDecisionRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(moderationService.decideTask(id, request, principal.userId()));
    }

    








    @GetMapping("/reports")
    public Result<PageResponse<AdminReportItemResponse>> listReports(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于等于1") @Max(value = 100, message = "每页数量必须小于等于100") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        PageRequest pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return Result.success(moderationService.listReports(pageable, page, status, type));
    }

    







    @PutMapping("/reports/{id}/handle")
    public Result<AdminReportItemResponse> handleReport(Authentication authentication,
                                                        @PathVariable Long id,
                                                        @Valid @RequestBody AdminReportHandleRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(moderationService.handleReport(id, request, principal.userId()));
    }
}
