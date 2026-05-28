/**
 * 标签公开接口
 * <p>
 * 无需认证即可访问，提供标签列表查询功能。
 */
package com.pxczxn.blog.tag.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.tag.dto.TagPublicResponse;
import com.pxczxn.blog.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/tags")
@RequiredArgsConstructor
public class PublicTagController {

    /** 标签服务 */
    private final TagService tagService;

    /**
     * 获取所有标签列表
     *
     * @return 标签列表
     */
    @GetMapping
    public Result<List<TagPublicResponse>> list() {
        return Result.success(tagService.list());
    }
}

