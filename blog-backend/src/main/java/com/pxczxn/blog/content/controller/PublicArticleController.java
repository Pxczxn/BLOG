





package com.pxczxn.blog.content.controller;

import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.content.dto.ArticleNavigationResponse;
import com.pxczxn.blog.content.dto.PublicArticleDetailResponse;
import com.pxczxn.blog.content.dto.PublicArticleListItemResponse;
import com.pxczxn.blog.content.service.ArticleService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping({"/api/public/articles", "/api/articles"})
@RequiredArgsConstructor
public class PublicArticleController {

    
    private final ArticleService articleService;

    










    @GetMapping
    public Result<PageResponse<PublicArticleListItemResponse>> listPublished(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于等于1") @Max(value = 100, message = "每页数量必须小于等于100") int size,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String tagId) {

        int p = Math.max(page - 1, 0);
        PageRequest pageable = PageRequest.of(p, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return Result.success(articleService.listPublished(pageable, page, categoryId, tagId));
    }

    





    @GetMapping("/{slug}")
    public Result<PublicArticleDetailResponse> getBySlug(@PathVariable String slug) {
        return Result.success(articleService.getPublishedBySlug(slug));
    }

    





    @GetMapping("/{slug}/navigation")
    public Result<ArticleNavigationResponse.ArticleNavigationData> getNavigation(@PathVariable String slug) {
        return Result.success(articleService.getNavigation(slug));
    }

    





    @PostMapping("/{slug}/view")
    public Result<Void> incrementViewCount(@PathVariable String slug) {
        articleService.incrementViewCount(slug);
        return Result.success(null);
    }
}
