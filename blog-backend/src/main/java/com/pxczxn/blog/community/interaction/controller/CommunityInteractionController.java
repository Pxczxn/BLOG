




package com.pxczxn.blog.community.interaction.controller;

import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.interaction.dto.CommunityFavoritePostItemResponse;
import com.pxczxn.blog.community.interaction.dto.CommunityFollowStatusResponse;
import com.pxczxn.blog.community.interaction.dto.CommunityNotificationListResponse;
import com.pxczxn.blog.community.interaction.dto.PostInteractionResponse;
import com.pxczxn.blog.community.interaction.service.CommunityInteractionService;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityInteractionController {

    private final CommunityInteractionService communityInteractionService;

    






    @PostMapping("/posts/{postId}/likes")
    public Result<PostInteractionResponse> likePost(Authentication authentication, @PathVariable Long postId) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityInteractionService.likePost(principal.userId(), postId));
    }

    






    @DeleteMapping("/posts/{postId}/likes")
    public Result<PostInteractionResponse> unlikePost(Authentication authentication, @PathVariable Long postId) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityInteractionService.unlikePost(principal.userId(), postId));
    }

    






    @PostMapping("/posts/{postId}/favorites")
    public Result<PostInteractionResponse> favoritePost(Authentication authentication, @PathVariable Long postId) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityInteractionService.favoritePost(principal.userId(), postId));
    }

    






    @DeleteMapping("/posts/{postId}/favorites")
    public Result<PostInteractionResponse> unfavoritePost(Authentication authentication, @PathVariable Long postId) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityInteractionService.unfavoritePost(principal.userId(), postId));
    }

    






    @GetMapping("/posts/{postId}/interaction")
    public Result<PostInteractionResponse> getPostInteraction(Authentication authentication, @PathVariable Long postId) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal && principal.isCommunityUser()) {
            userId = principal.userId();
        }
        return Result.success(communityInteractionService.getPostInteraction(postId, userId));
    }

    






    @PostMapping("/users/{username}/follow")
    public Result<CommunityFollowStatusResponse> followUser(Authentication authentication, @PathVariable String username) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityInteractionService.followUser(principal.userId(), username));
    }

    






    @DeleteMapping("/users/{username}/follow")
    public Result<CommunityFollowStatusResponse> unfollowUser(Authentication authentication, @PathVariable String username) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityInteractionService.unfollowUser(principal.userId(), username));
    }

    







    @GetMapping("/me/favorites")
    public Result<PageResponse<CommunityFavoritePostItemResponse>> myFavorites(Authentication authentication,
                                                                               @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
                                                                               @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于等于1") @Max(value = 100, message = "每页数量必须小于等于100") int size) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        PageRequest pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return Result.success(communityInteractionService.listMyFavorites(principal.userId(), pageable, page));
    }

    








    @GetMapping("/notifications")
    public Result<CommunityNotificationListResponse> listNotifications(Authentication authentication,
                                                                       @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
                                                                       @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页数量必须大于等于1") @Max(value = 100, message = "每页数量必须小于等于100") int size,
                                                                       @RequestParam(defaultValue = "false") boolean unreadOnly) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        PageRequest pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return Result.success(communityInteractionService.listNotifications(principal.userId(), pageable, page, unreadOnly));
    }

    






    @PostMapping("/notifications/{id}/read")
    public Result<Void> markRead(Authentication authentication, @PathVariable Long id) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        communityInteractionService.markNotificationRead(principal.userId(), id);
        return Result.success(null);
    }

    





    @PostMapping("/notifications/read-all")
    public Result<Integer> markAllRead(Authentication authentication) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityInteractionService.markAllNotificationsRead(principal.userId()));
    }
}

