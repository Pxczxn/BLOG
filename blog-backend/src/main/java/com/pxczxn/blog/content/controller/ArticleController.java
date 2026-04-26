





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

    
    private final ArticleService articleService;

    





    @PostMapping
    public Result<ArticleResponse> create(@Valid @RequestBody ArticleCreateRequest request) {
        Long authorId = extractUserId();
        ArticleResponse response = articleService.create(request, authorId);
        return Result.success("Article created", response);
    }

    





    @GetMapping("/{id}")
    public Result<ArticleAdminDetailResponse> getById(@PathVariable Long id) {
        return Result.success(articleService.getAdminDetail(id));
    }

    











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

    





    @GetMapping("/status/{status}")
    public Result<List<ArticleResponse>> listByStatus(@PathVariable ArticleStatus status) {
        return Result.success(articleService.listByStatus(status));
    }

    






    @PutMapping("/{id}")
    public Result<ArticleResponse> update(@PathVariable Long id,
                                          @Valid @RequestBody ArticleUpdateRequest request) {
        Long authorId = extractUserId();
        return Result.success("Article updated", articleService.update(id, request, authorId));
    }

    





    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success("Article deleted", null);
    }

    





    @PutMapping("/{id}/publish")
    public Result<ArticleStatusUpdateResponse> publish(@PathVariable Long id) {
        return Result.success(articleService.publishToPublished(id));
    }

    





    @PutMapping("/{id}/draft")
    public Result<ArticleStatusUpdateResponse> draft(@PathVariable Long id) {
        return Result.success(articleService.publishToDraft(id));
    }

    






    @PutMapping("/{id}/tags")
    public Result<Void> updateTags(@PathVariable Long id, @RequestBody ArticleTagUpdateRequest request) {
        articleService.updateTags(id, request.getTagIds());
        return Result.success(null);
    }

    





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
