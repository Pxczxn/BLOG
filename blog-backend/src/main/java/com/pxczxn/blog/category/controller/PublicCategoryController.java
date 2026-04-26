/**
 * 分类公开控制器
 * <p>
 * 提供分类列表查询接口，无需认证即可访问。
 */
package com.pxczxn.blog.category.controller;

import com.pxczxn.blog.category.entity.Category;
import com.pxczxn.blog.category.service.CategoryService;
import com.pxczxn.blog.common.response.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    /**
     * 获取所有分类列表
     *
     * @return 按创建时间降序排列的分类列表
     */
    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }
}

