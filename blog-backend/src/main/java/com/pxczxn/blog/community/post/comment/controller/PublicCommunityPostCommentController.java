




package com.pxczxn.blog.community.post.comment.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.post.comment.dto.CommunityPostCommentItemResponse;
import com.pxczxn.blog.community.post.comment.service.CommunityPostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/community/posts")
@RequiredArgsConstructor
public class PublicCommunityPostCommentController {

    private final CommunityPostCommentService communityPostCommentService;

    





    @GetMapping("/{slug}/comments")
    public Result<List<CommunityPostCommentItemResponse>> list(@PathVariable String slug) {
        return Result.success(communityPostCommentService.listByPostSlug(slug));
    }
}

