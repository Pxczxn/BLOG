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

/**
 * 社区节点公开控制器
 * <p>
 * 无需认证即可访问，提供公开的节点查询功能
 */
@RestController
@RequestMapping("/api/public/community/nodes")
@RequiredArgsConstructor
public class PublicCommunityNodeController {

    private final CommunityNodeService communityNodeService;

    /**
     * 获取所有激活状态的社区节点列表
     *
     * @return 激活状态的节点响应列表
     */
    @GetMapping
    public Result<List<CommunityNodeResponse>> list() {
        return Result.success(communityNodeService.listPublic());
    }

    /**
     * 根据URL别名获取社区节点详情
     *
     * @param slug 节点URL别名
     * @return 节点响应
     */
    @GetMapping("/{slug}")
    public Result<CommunityNodeResponse> getBySlug(@PathVariable String slug) {
        return Result.success(communityNodeService.getPublicBySlug(slug));
    }
}
