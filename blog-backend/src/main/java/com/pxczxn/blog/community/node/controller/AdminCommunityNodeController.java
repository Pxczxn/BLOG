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

/**
 * 社区节点管理控制器
 * <p>
 * 需要管理员权限，提供节点的列表查询、创建和更新功能
 */
@RestController
@RequestMapping("/api/admin/community/nodes")
@RequiredArgsConstructor
public class AdminCommunityNodeController {

    private final CommunityNodeService communityNodeService;

    /**
     * 获取所有社区节点列表（管理员视图）
     *
     * @return 节点响应列表，包含所有状态的节点
     */
    @GetMapping
    public Result<List<CommunityNodeResponse>> list() {
        return Result.success(communityNodeService.listAdmin());
    }

    /**
     * 创建新的社区节点
     *
     * @param request 节点创建请求
     * @return 创建成功的节点响应
     */
    @PostMapping
    public Result<CommunityNodeResponse> create(@Valid @RequestBody CommunityNodeRequest request) {
        return Result.success(communityNodeService.create(request));
    }

    /**
     * 更新指定的社区节点
     *
     * @param id      节点ID
     * @param request 节点更新请求
     * @return 更新后的节点响应
     */
    @PutMapping("/{id}")
    public Result<CommunityNodeResponse> update(@PathVariable Long id, @Valid @RequestBody CommunityNodeRequest request) {
        return Result.success(communityNodeService.update(id, request));
    }
}
