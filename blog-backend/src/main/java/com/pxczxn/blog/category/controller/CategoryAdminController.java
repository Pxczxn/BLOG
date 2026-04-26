/**
 * 分类管理控制器（管理员端）
 * <p>
 * 提供分类的增删改操作，需要管理员权限。
 */
package com.pxczxn.blog.category.controller;

import com.pxczxn.blog.category.dto.CategoryCreateRequest;
import com.pxczxn.blog.category.dto.CategoryCreateResponse;
import com.pxczxn.blog.category.dto.CategoryUpdateRequest;
import com.pxczxn.blog.category.dto.CategoryUpdateResponse;
import com.pxczxn.blog.category.service.CategoryService;
import com.pxczxn.blog.common.response.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService categoryService;

    /**
     * 创建新分类
     *
     * @param request 分类创建请求，包含分类名称
     * @return 创建成功后的分类信息
     */
    @PostMapping
    public Result<CategoryCreateResponse> create(@Valid @RequestBody CategoryCreateRequest request) {
        return Result.success(categoryService.create(request));
    }

    /**
     * 更新分类信息
     *
     * @param id 分类ID
     * @param request 分类更新请求，包含新的分类名称
     * @return 更新后的分类信息
     */
    @PutMapping("/{id}")
    public Result<CategoryUpdateResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody CategoryUpdateRequest request) {
        return Result.success(categoryService.update(id, request));
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success(null);
    }
}

