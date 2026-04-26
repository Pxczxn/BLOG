/**
 * 社区帖子控制器
 * <p>
 * 需要社区用户登录认证
 */
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

    /**
     * 创建帖子
     *
     * @param authentication 认证信息
     * @param request        帖子写入请求
     * @return 创建后的帖子编辑器信息
     */
    @PostMapping
    public Result<CommunityPostEditorResponse> create(Authentication authentication,
                                                      @Valid @RequestBody CommunityPostWriteRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityPostService.create(principal.userId(), request));
    }

    /**
     * 更新帖子
     *
     * @param authentication 认证信息
     * @param id             帖子ID
     * @param request        帖子写入请求
     * @return 更新后的帖子编辑器信息
     */
    @PutMapping("/{id}")
    public Result<CommunityPostEditorResponse> update(Authentication authentication,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody CommunityPostWriteRequest request) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityPostService.update(principal.userId(), id, request));
    }

    /**
     * 获取帖子编辑器数据
     *
     * @param authentication 认证信息
     * @param id             帖子ID
     * @return 帖子编辑器信息
     */
    @GetMapping("/{id}")
    public Result<CommunityPostEditorResponse> getEditor(Authentication authentication, @PathVariable Long id) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityPostService.getEditor(principal.userId(), id));
    }

    /**
     * 获取当前用户的帖子列表
     *
     * @param authentication 认证信息
     * @return 帖子列表
     */
    @GetMapping("/mine")
    public Result<List<CommunityPostMineItemResponse>> mine(Authentication authentication) {
        AuthenticatedUserPrincipal principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();
        return Result.success(communityPostService.listMine(principal.userId()));
    }
}

