




package com.pxczxn.blog.tag.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.tag.entity.Tag;
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

    
    private final TagService tagService;

    




    @GetMapping
    public Result<List<Tag>> list() {
        return Result.success(tagService.list());
    }
}

