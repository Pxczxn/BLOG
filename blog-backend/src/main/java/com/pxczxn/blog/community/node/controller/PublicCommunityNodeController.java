package com.pxczxn.blog.community.node.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.node.dto.CommunityNodeResponse;
import com.pxczxn.blog.community.node.service.CommunityNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;






@RestController
@RequestMapping("/api/public/community/nodes")
@RequiredArgsConstructor
public class PublicCommunityNodeController {

    private final CommunityNodeService communityNodeService;

    




    @GetMapping
    public Result<List<CommunityNodeResponse>> list() {
        return Result.success(communityNodeService.listPublic());
    }

    





    @GetMapping("/{slug}")
    public Result<CommunityNodeResponse> getBySlug(@PathVariable String slug) {
        return Result.success(communityNodeService.getPublicBySlug(slug));
    }
}
