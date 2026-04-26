package com.pxczxn.blog.community.node.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.node.dto.CommunityNodeRequest;
import com.pxczxn.blog.community.node.dto.CommunityNodeResponse;
import com.pxczxn.blog.community.node.service.CommunityNodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;






@RestController
@RequestMapping("/api/admin/community/nodes")
@RequiredArgsConstructor
public class AdminCommunityNodeController {

    private final CommunityNodeService communityNodeService;

    




    @GetMapping
    public Result<List<CommunityNodeResponse>> list() {
        return Result.success(communityNodeService.listAdmin());
    }

    





    @PostMapping
    public Result<CommunityNodeResponse> create(@Valid @RequestBody CommunityNodeRequest request) {
        return Result.success(communityNodeService.create(request));
    }

    






    @PutMapping("/{id}")
    public Result<CommunityNodeResponse> update(@PathVariable Long id, @Valid @RequestBody CommunityNodeRequest request) {
        return Result.success(communityNodeService.update(id, request));
    }
}
