




package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.ArticleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleCreateRequest {

    
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200")
    private String title;

    
    @Size(max = 500, message = "文章摘要长度不能超过500")
    private String summary;

    
    @NotBlank(message = "文章内容不能为空")
    private String content;

    
    @Size(max = 500, message = "封面图片链接长度不能超过500")
    private String coverImage;

    
    private Long categoryId;

    
    private ArticleStatus status;
}
