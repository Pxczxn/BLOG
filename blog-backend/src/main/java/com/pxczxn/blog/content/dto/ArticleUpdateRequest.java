package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.ArticleStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleUpdateRequest {

    @Size(max = 200, message = "title length must be <= 200")
    private String title;

    @Size(max = 200, message = "slug length must be <= 200")
    private String slug;

    @Size(max = 500, message = "summary length must be <= 500")
    private String summary;

    private String content;

    @Size(max = 500, message = "coverImage length must be <= 500")
    private String coverImage;

    private Long categoryId;

    private ArticleStatus status;
}
