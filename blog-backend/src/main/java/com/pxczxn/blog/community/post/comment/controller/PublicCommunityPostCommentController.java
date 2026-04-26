/**
 * 公开社区帖子评论控制器
 * <p>
 * 无需认证即可访问，提供评论的公开查询功能
 */
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

    /**
     * 根据帖子Slug查询评论列表
     *
     * @param slug 帖子Slug
     * @return 评论列表
     */
    @GetMapping("/{slug}/comments")
    public Result<List<CommunityPostCommentItemResponse>> list(@PathVariable String slug) {
        return Result.success(communityPostCommentService.listByPostSlug(slug));
    }
}

