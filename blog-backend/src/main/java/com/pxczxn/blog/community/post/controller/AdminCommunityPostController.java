




package com.pxczxn.blog.community.post.controller;

import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.post.dto.AdminCommunityPostListItemResponse;
import com.pxczxn.blog.community.post.dto.AdminCommunityPostStatusRequest;
import com.pxczxn.blog.community.post.dto.CommunityPostEditorResponse;
import com.pxczxn.blog.community.post.service.CommunityPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/community/posts")
@RequiredArgsConstructor
public class AdminCommunityPostController {

    private final CommunityPostService communityPostService;

    









    @GetMapping
    public Result<PageResponse<AdminCommunityPostListItemResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String node,
            @RequestParam(required = false) String keyword) {
        PageRequest pageable = PageRequest.of(Math.max(page - 1, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "createdAt"));
        return Result.success(communityPostService.listAdmin(pageable, page, status, node, keyword));
    }

    






    @PutMapping("/{id}/status")
    public Result<CommunityPostEditorResponse> updateStatus(@PathVariable Long id,
                                                            @Valid @RequestBody AdminCommunityPostStatusRequest request) {
        return Result.success(communityPostService.updateStatus(id, request));
    }

    





    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        communityPostService.delete(id);
        return Result.success(null);
    }
}

