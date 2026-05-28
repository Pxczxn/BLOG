package com.pxczxn.blog.community.post.comment.controller;

import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.post.comment.dto.AdminCommunityPostCommentItemResponse;
import com.pxczxn.blog.community.post.comment.dto.CommunityPostCommentStatusResponse;
import com.pxczxn.blog.community.post.comment.entity.CommunityPostCommentStatus;
import com.pxczxn.blog.community.post.comment.service.CommunityPostCommentService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/admin/community/comments")
@RequiredArgsConstructor
public class AdminCommunityPostCommentController {

    private final CommunityPostCommentService communityPostCommentService;

    @GetMapping
    public Result<PageResponse<AdminCommunityPostCommentItemResponse>> list(
            @RequestParam(required = false) CommunityPostCommentStatus status,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于等于1") @Max(value = 100, message = "每页数量必须小于等于100") int size) {

        int p = Math.max(page - 1, 0);
        PageRequest pageable = PageRequest.of(p, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return Result.success(communityPostCommentService.listByStatus(status, pageable, page));
    }

    @PutMapping("/{id}/approve")
    public Result<CommunityPostCommentStatusResponse> approve(@PathVariable Long id) {
        return Result.success(communityPostCommentService.approve(id));
    }

    @PutMapping("/{id}/reject")
    public Result<CommunityPostCommentStatusResponse> reject(@PathVariable Long id) {
        return Result.success(communityPostCommentService.reject(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        communityPostCommentService.delete(id);
        return Result.success(null);
    }
}
