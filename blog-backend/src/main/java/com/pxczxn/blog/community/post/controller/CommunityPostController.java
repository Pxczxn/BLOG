




package com.pxczxn.blog.community.post.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.post.dto.CommunityPostEditorResponse;
import com.pxczxn.blog.community.post.dto.CommunityPostMineItemResponse;
import com.pxczxn.blog.community.post.dto.CommunityPostWriteRequest;
import com.pxczxn.blog.community.post.service.CommunityPostService;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    






    @PostMapping
    public Result<CommunityPostEditorResponse> create(Authentication authentication,
                                                      @Valid @RequestBody CommunityPostWriteRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityPostService.create(principal.userId(), request));
    }

    







    @PutMapping("/{id}")
    public Result<CommunityPostEditorResponse> update(Authentication authentication,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody CommunityPostWriteRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityPostService.update(principal.userId(), id, request));
    }

    






    @GetMapping("/{id}")
    public Result<CommunityPostEditorResponse> getEditor(Authentication authentication, @PathVariable Long id) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityPostService.getEditor(principal.userId(), id));
    }

    





    @GetMapping("/mine")
    public Result<List<CommunityPostMineItemResponse>> mine(Authentication authentication) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityPostService.listMine(principal.userId()));
    }
}

