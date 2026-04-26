




package com.pxczxn.blog.community.post.controller;

import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.post.dto.PublicCommunityPostDetailResponse;
import com.pxczxn.blog.community.post.dto.PublicCommunityPostListItemResponse;
import com.pxczxn.blog.community.post.service.CommunityPostService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/public/community/posts")
@RequiredArgsConstructor
public class PublicCommunityPostController {

    private final CommunityPostService communityPostService;

    









    @GetMapping
    public Result<PageResponse<PublicCommunityPostListItemResponse>> list(
            Authentication authentication,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于等于1") @Max(value = 100, message = "每页数量必须小于等于100") int size,
            @RequestParam(required = false) String node,
            @RequestParam(required = false) String username) {
        PageRequest pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "publishedAt", "createdAt"));
        return Result.success(communityPostService.listPublished(pageable, page, node, username, resolveViewerUserId(authentication)));
    }

    






    @GetMapping("/{slug}")
    public Result<PublicCommunityPostDetailResponse> detail(Authentication authentication, @PathVariable String slug) {
        return Result.success(communityPostService.getPublishedBySlug(slug, resolveViewerUserId(authentication)));
    }

    





    @PostMapping("/{slug}/view")
    public Result<Void> incrementViewCount(@PathVariable String slug) {
        communityPostService.incrementViewCount(slug);
        return Result.success(null);
    }

    





    private Long resolveViewerUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof com.pxczxn.blog.security.AuthenticatedUserPrincipal principal
                && principal.isCommunityUser()) {
            return principal.userId();
        }
        return null;
    }
}

