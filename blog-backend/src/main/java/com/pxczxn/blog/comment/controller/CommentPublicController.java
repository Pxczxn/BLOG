package com.pxczxn.blog.comment.controller;

import com.pxczxn.blog.comment.dto.CommentItemResponse;
import com.pxczxn.blog.comment.dto.CreateCommentRequest;
import com.pxczxn.blog.comment.dto.CreateCommentResponse;
import com.pxczxn.blog.comment.service.CommentService;
import com.pxczxn.blog.common.response.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/comments")
    public Result<CreateCommentResponse> create(@RequestBody @Valid CreateCommentRequest req) {
        return Result.success(commentService.create(req));
    }

    @GetMapping("/articles/{articleId}/comments")
    public Result<List<CommentItemResponse>> list(@PathVariable Long articleId) {
        return Result.success(commentService.listApprovedByArticle(articleId));
    }

    @GetMapping("/comments")
    public Result<List<CommentItemResponse>> listByQuery(@RequestParam Long articleId) {
        return Result.success(commentService.listApprovedByArticle(articleId));
    }
}
