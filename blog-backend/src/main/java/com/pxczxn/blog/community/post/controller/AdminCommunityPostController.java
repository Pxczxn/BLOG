/**
 * 管理端社区帖子控制器
 * <p>
 * 提供帖子的管理功能，需要管理员权限
 */
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

    /**
     * 分页查询帖子列表
     *
     * @param page    页码
     * @param size    每页数量
     * @param status  状态筛选
     * @param node    节点筛选
     * @param keyword 关键词搜索
     * @return 帖子列表
     */
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

    /**
     * 更新帖子状态
     *
     * @param id      帖子ID
     * @param request 状态更新请求
     * @return 更新后的帖子信息
     */
    @PutMapping("/{id}/status")
    public Result<CommunityPostEditorResponse> updateStatus(@PathVariable Long id,
                                                            @Valid @RequestBody AdminCommunityPostStatusRequest request) {
        return Result.success(communityPostService.updateStatus(id, request));
    }

    /**
     * 删除帖子
     *
     * @param id 帖子ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        communityPostService.delete(id);
        return Result.success(null);
    }
}

