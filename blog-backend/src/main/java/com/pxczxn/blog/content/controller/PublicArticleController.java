/**
 * 文章公开控制器
 * <p>
 * 提供文章的公开访问接口，无需认证即可访问，
 * 仅返回已发布状态的文章，支持分页、分类和标签筛选。
 */
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

    /** 文章服务 */
    private final ArticleService articleService;

    /**
     * 分页查询已发布文章列表
     * <p>
     * 支持按分类ID或slug、标签ID或slug筛选，按创建时间倒序排列。
     *
     * @param page       页码（从1开始，默认1）
     * @param size       每页数量（默认10，最大100）
     * @param categoryId 分类ID或slug（选填）
     * @param tagId      标签ID或slug（选填）
     * @return 分页文章列表响应
     */
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

    /**
     * 根据slug获取已发布文章详情
     *
     * @param slug 文章URL标识
     * @return 文章详情响应，含分类和标签信息
     */
    @GetMapping("/{slug}")
    public Result<PublicArticleDetailResponse> getBySlug(@PathVariable String slug) {
        return Result.success(articleService.getPublishedBySlug(slug));
    }

    /**
     * 获取文章的上下篇导航信息
     *
     * @param slug 当前文章的URL标识
     * @return 包含上一篇和下一篇文章的导航数据
     */
    @GetMapping("/{slug}/navigation")
    public Result<ArticleNavigationResponse.ArticleNavigationData> getNavigation(@PathVariable String slug) {
        return Result.success(articleService.getNavigation(slug));
    }

    /**
     * 增加文章浏览次数
     *
     * @param slug 文章URL标识
     * @return 浏览次数增加成功响应
     */
    @PostMapping("/{slug}/view")
    public Result<Void> incrementViewCount(@PathVariable String slug) {
        articleService.incrementViewCount(slug);
        return Result.success(null);
    }
}
