package com.pxczxn.blog.community.controller;

import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.dto.AdminCommunityUserItemResponse;
import com.pxczxn.blog.community.dto.AdminCommunityUserUpdateRequest;
import com.pxczxn.blog.community.service.AdminCommunityUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminCommunityUserController {

    private final AdminCommunityUserService adminCommunityUserService;

    @GetMapping
    public Result<PageResponse<AdminCommunityUserItemResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {
        PageRequest pageable = PageRequest.of(
                Math.max(page - 1, 0),
                Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return Result.success(adminCommunityUserService.list(pageable, page, status, role, keyword));
    }

    @PutMapping("/{id}")
    public Result<AdminCommunityUserItemResponse> update(@PathVariable Long id,
                                                         @Valid @RequestBody AdminCommunityUserUpdateRequest request) {
        return Result.success("User updated", adminCommunityUserService.update(id, request));
    }
}
