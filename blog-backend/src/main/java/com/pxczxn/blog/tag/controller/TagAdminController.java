




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

    
    private final TagService tagService;

    





    @PostMapping
    public Result<Tag> create(@Valid @RequestBody TagCreateRequest request) {
        return Result.success(tagService.create(request));
    }

    





    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.success(null);
    }
}

