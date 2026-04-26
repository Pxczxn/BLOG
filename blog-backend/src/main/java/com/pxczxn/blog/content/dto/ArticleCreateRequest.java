/**
 * 创建文章请求 DTO
 * <p>
 * 管理端创建文章时提交的请求数据，标题和内容为必填项。
 */
package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.ArticleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ArticleCreateRequest {

    /** 文章标题（必填，最大200字符） */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200")
    private String title;

    /** 文章摘要（选填，最大500字符） */
    @Size(max = 500, message = "文章摘要长度不能超过500")
    private String summary;

    /** 文章正文（必填，富文本内容） */
    @NotBlank(message = "文章内容不能为空")
    private String content;

    /** 封面图片URL（选填，最大500字符） */
    @Size(max = 500, message = "封面图片链接长度不能超过500")
    private String coverImage;

    /** 所属分类ID（选填） */
    private Long categoryId;

    /** 发布状态（选填，默认为DRAFT） */
    private ArticleStatus status;

    @Size(max = 8, message = "文章标签最多选择8个")
    private List<Long> tagIds;
}
