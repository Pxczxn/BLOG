




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

    







    @PostMapping("/{postId}/comments")
    public Result<CommunityPostCommentItemResponse> create(Authentication authentication,
                                                           @PathVariable Long postId,
                                                           @Valid @RequestBody CommunityPostCommentCreateRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityPostCommentService.create(principal.userId(), postId, request));
    }
}

