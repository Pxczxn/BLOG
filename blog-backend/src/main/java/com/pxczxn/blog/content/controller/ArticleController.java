/**
 * 文章管理端控制器
 * <p>
 * 提供文章的管理接口，包括创建、查询、更新、删除、发布/下架及标签关联等操作，
 * 所有接口需要管理员认证才能访问。
 */
package com.pxczxn.blog.content.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.content.dto.ArticleAdminDetailResponse;
import com.pxczxn.blog.content.dto.ArticleAdminListItemResponse;
import com.pxczxn.blog.content.dto.ArticleCreateRequest;
import com.pxczxn.blog.content.dto.ArticleResponse;
import com.pxczxn.blog.content.dto.ArticleStatusUpdateResponse;
import com.pxczxn.blog.content.dto.ArticleTagUpdateRequest;
import com.pxczxn.blog.content.dto.ArticleUpdateRequest;
import com.pxczxn.blog.content.entity.ArticleStatus;
import com.pxczxn.blog.content.service.ArticleService;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class ArticleController {

    /** 文章服务 */
    private final ArticleService articleService;

    /**
     * 创建新文章
     *
     * @param request 文章创建请求
     * @return 创建后的文章响应
     */
    @PostMapping
    public Result<ArticleResponse> create(@Valid @RequestBody ArticleCreateRequest request) {
        Long authorId = extractUserId();
        ArticleResponse response = articleService.create(request, authorId);
        return Result.success("Article created", response);
    }

    /**
     * 根据ID获取文章详情（管理端）
     *
     * @param id 文章ID
     * @return 文章管理端详情响应，含标签ID列表
     */
    @GetMapping("/{id}")
    public Result<ArticleAdminDetailResponse> getById(@PathVariable Long id) {
        return Result.success(articleService.getAdminDetail(id));
    }

    /**
     * 文章列表查询（管理端）
     * <p>
     * 支持分页和非分页两种模式，可按状态和关键词筛选。
     * page和size必须同时提供或同时不提供。
     *
     * @param page    页码（从1开始，需与size同时提供）
     * @param size    每页数量（需与page同时提供）
     * @param status  文章状态筛选条件（选填）
     * @param keyword 标题关键词筛选（选填）
     * @return 分页或全量文章列表
     */
    @GetMapping
    public Result<?> list(@RequestParam(required = false) Integer page,
                          @RequestParam(required = false) Integer size,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) String keyword) {
        if ((page == null) != (size == null)) {
            throw new IllegalArgumentException("page and size must be provided together");
        }

        if (page != null) {
            int safePage = Math.max(page, 1);
            int safeSize = Math.max(size, 1);
            PageResponse<ArticleAdminListItemResponse> response = articleService.listAdmin(safePage, safeSize, status, keyword);
            return Result.success(response);
        }

        List<ArticleAdminListItemResponse> response = articleService.listAdmin(status, keyword);
        return Result.success(response);
    }

    /**
     * 根据状态查询文章列表
     *
     * @param status 文章状态
     * @return 指定状态的文章列表
     */
    @GetMapping("/status/{status}")
    public Result<List<ArticleResponse>> listByStatus(@PathVariable ArticleStatus status) {
        return Result.success(articleService.listByStatus(status));
    }

    /**
     * 更新文章
     *
     * @param id      文章ID
     * @param request 文章更新请求（仅更新非null字段）
     * @return 更新后的文章响应
     */
    @PutMapping("/{id}")
    public Result<ArticleResponse> update(@PathVariable Long id,
                                          @Valid @RequestBody ArticleUpdateRequest request) {
        Long authorId = extractUserId();
        return Result.success("Article updated", articleService.update(id, request, authorId));
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 删除成功响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success("Article deleted", null);
    }

    /**
     * 发布文章（将状态改为PUBLISHED）
     *
     * @param id 文章ID
     * @return 状态更新响应
     */
    @PutMapping("/{id}/publish")
    public Result<ArticleStatusUpdateResponse> publish(@PathVariable Long id) {
        return Result.success(articleService.publishToPublished(id));
    }

    /**
     * 将文章下架为草稿（将状态改为DRAFT）
     *
     * @param id 文章ID
     * @return 状态更新响应
     */
    @PutMapping("/{id}/draft")
    public Result<ArticleStatusUpdateResponse> draft(@PathVariable Long id) {
        return Result.success(articleService.publishToDraft(id));
    }

    /**
     * 更新文章关联的标签
     *
     * @param id      文章ID
     * @param request 标签更新请求，包含标签ID列表
     * @return 更新成功响应
     */
    @PutMapping("/{id}/tags")
    public Result<Void> updateTags(@PathVariable Long id, @RequestBody ArticleTagUpdateRequest request) {
        articleService.updateTags(id, request.getTagIds());
        return Result.success(null);
    }

    /**
     * 从安全上下文中提取当前认证用户的ID
     *
     * @return 当前用户ID
     * @throws IllegalStateException 如果请求未认证或认证主体类型无效
     */
    private Long extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated request");
        }
        if (authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal) {
            return principal.userId();
        }
        throw new IllegalStateException("Invalid authenticated principal");
    }
}
