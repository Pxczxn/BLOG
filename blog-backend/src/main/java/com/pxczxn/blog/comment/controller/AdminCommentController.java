/**
 * 评论管理控制器（管理员端）
 * <p>
 * 提供评论的审核（通过/拒绝）、删除、按状态分页查询等接口。
 */
package com.pxczxn.blog.comment.controller;

import com.pxczxn.blog.comment.dto.AdminCommentItemResponse;
import com.pxczxn.blog.comment.dto.AdminCommentStatusResponse;
import com.pxczxn.blog.comment.entity.CommentStatus;
import com.pxczxn.blog.comment.service.CommentService;
import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.common.response.Result;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
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
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    /**
     * 按状态分页查询评论列表
     *
     * @param status 审核状态，默认PENDING
     * @param page   页码，从1开始
     * @param size   每页数量
     * @return 分页评论列表
     */
    @GetMapping
    public Result<PageResponse<AdminCommentItemResponse>> list(
            @RequestParam(defaultValue = "PENDING") CommentStatus status,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于等于1") @Max(value = 100, message = "每页数量必须小于等于100") int size) {

        int p = Math.max(page - 1, 0);
        PageRequest pageable = PageRequest.of(p, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return Result.success(commentService.listByStatus(status, pageable, page));
    }

    /**
     * 审核通过评论
     *
     * @param id 评论ID
     * @return 更新后的评论状态
     */
    @PutMapping("/{id}/approve")
    public Result<AdminCommentStatusResponse> approve(@PathVariable Long id) {
        return Result.success(commentService.approve(id));
    }

    /**
     * 拒绝评论
     *
     * @param id 评论ID
     * @return 更新后的评论状态
     */
    @PutMapping("/{id}/reject")
    public Result<AdminCommentStatusResponse> reject(@PathVariable Long id) {
        return Result.success(commentService.reject(id));
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 空结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.success(null);
    }
}

