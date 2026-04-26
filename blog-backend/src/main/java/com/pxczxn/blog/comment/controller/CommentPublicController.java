/**
 * 评论公开控制器
 * <p>
 * 提供评论的创建和按文章查询已通过评论的接口，无需管理员权限。
 * 支持社区用户和游客两种身份发表评论。
 */
package com.pxczxn.blog.comment.controller;

import com.pxczxn.blog.comment.dto.CommentItemResponse;
import com.pxczxn.blog.comment.dto.CreateCommentRequest;
import com.pxczxn.blog.comment.dto.CreateCommentResponse;
import com.pxczxn.blog.comment.service.CommentService;
import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentService commentService;

    /**
     * 创建评论
     * <p>
     * 支持社区用户登录发表和游客发表两种方式。
     * 登录用户自动填充昵称和邮箱，游客需手动填写。
     *
     * @param req            评论请求DTO
     * @param authentication 认证信息，可能为空（游客）
     * @return 新创建的评论信息
     */
    @PostMapping("/comments")
    public Result<CreateCommentResponse> create(@RequestBody @Valid CreateCommentRequest req,
                                                Authentication authentication) {
        AuthenticatedUserPrincipal principal = authentication != null && authentication.getPrincipal() instanceof AuthenticatedUserPrincipal user
                ? user
                : null;
        return Result.success(commentService.create(req, principal));
    }

    /**
     * 获取文章的已通过评论列表
     *
     * @param articleId 文章ID
     * @return 评论列表
     */
    @GetMapping("/articles/{articleId}/comments")
    public Result<List<CommentItemResponse>> list(@PathVariable Long articleId) {
        return Result.success(commentService.listApprovedByArticle(articleId));
    }

    /**
     * 通过查询参数获取文章的已通过评论列表
     *
     * @param articleId 文章ID
     * @return 评论列表
     */
    @GetMapping("/comments")
    public Result<List<CommentItemResponse>> listByQuery(@RequestParam Long articleId) {
        return Result.success(commentService.listApprovedByArticle(articleId));
    }
}

