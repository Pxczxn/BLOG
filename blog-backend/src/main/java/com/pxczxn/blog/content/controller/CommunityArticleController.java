package com.pxczxn.blog.content.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.content.dto.ArticleCreateRequest;
import com.pxczxn.blog.content.dto.ArticleResponse;
import com.pxczxn.blog.content.service.ArticleService;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community/articles")
@RequiredArgsConstructor
public class CommunityArticleController {

    private final ArticleService articleService;

    @PostMapping
    public Result<ArticleResponse> create(Authentication authentication,
                                          @Valid @RequestBody ArticleCreateRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success("Article created", articleService.createFromCommunity(request, principal.userId()));
    }
}
