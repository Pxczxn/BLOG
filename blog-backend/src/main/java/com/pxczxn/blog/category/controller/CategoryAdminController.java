




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

    





    @PostMapping
    public Result<CategoryCreateResponse> create(@Valid @RequestBody CategoryCreateRequest request) {
        return Result.success(categoryService.create(request));
    }

    






    @PutMapping("/{id}")
    public Result<CategoryUpdateResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody CategoryUpdateRequest request) {
        return Result.success(categoryService.update(id, request));
    }

    





    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success(null);
    }
}

