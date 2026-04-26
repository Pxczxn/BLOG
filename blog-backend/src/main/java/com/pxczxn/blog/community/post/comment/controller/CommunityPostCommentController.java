/**
 * 社区帖子评论控制器
 * <p>
 * 需要社区用户登录认证
 */
package com.pxczxn.blog.community.post.comment.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.post.comment.dto.CommunityPostCommentCreateRequest;
import com.pxczxn.blog.community.post.comment.dto.CommunityPostCommentItemResponse;
import com.pxczxn.blog.community.post.comment.service.CommunityPostCommentService;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class CommunityPostCommentController {

    private final CommunityPostCommentService communityPostCommentService;

    /**
     * 创建帖子评论
     *
     * @param authentication 认证信息
     * @param postId         帖子ID
     * @param request        评论创建请求
     * @return 创建后的评论信息
     */
    @PostMapping("/{postId}/comments")
    public Result<CommunityPostCommentItemResponse> create(Authentication authentication,
                                                           @PathVariable Long postId,
                                                           @Valid @RequestBody CommunityPostCommentCreateRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityPostCommentService.create(principal.userId(), postId, request));
    }
}

