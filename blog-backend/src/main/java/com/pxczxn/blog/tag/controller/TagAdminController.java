/**
 * 标签管理接口
 * <p>
 * 需要管理员权限，提供标签的创建和删除功能。
 */
package com.pxczxn.blog.tag.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.tag.dto.TagCreateRequest;
import com.pxczxn.blog.tag.entity.Tag;
import com.pxczxn.blog.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class TagAdminController {

    /** 标签服务 */
    private final TagService tagService;

    /**
     * 创建新标签
     *
     * @param request 标签创建请求
     * @return 创建成功的标签
     */
    @PostMapping
    public Result<Tag> create(@Valid @RequestBody TagCreateRequest request) {
        return Result.success(tagService.create(request));
    }

    /**
     * 根据ID删除标签
     *
     * @param id 标签ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.success(null);
    }
}

