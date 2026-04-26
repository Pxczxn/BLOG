/**
 * 更新文章请求 DTO
 * <p>
 * 管理端更新文章时提交的请求数据，所有字段均为选填，
 * 仅更新非null字段（分类ID除外，null表示取消关联）。
 */
package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.ArticleStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleUpdateRequest {

    /** 文章标题（选填，最大200字符） */
    @Size(max = 200, message = "文章标题长度不能超过200")
    private String title;

    /** URL友好标识（选填，最大200字符，需唯一） */
    @Size(max = 200, message = "URL别名长度不能超过200")
    private String slug;

    /** 文章摘要（选填，最大500字符） */
    @Size(max = 500, message = "文章摘要长度不能超过500")
    private String summary;

    /** 文章正文（选填，富文本内容） */
    private String content;

    /** 封面图片URL（选填，最大500字符） */
    @Size(max = 500, message = "封面图片链接长度不能超过500")
    private String coverImage;

    /** 所属分类ID（选填，null表示取消分类关联） */
    private Long categoryId;

    /** 发布状态（选填，修改时自动处理发布时间） */
    private ArticleStatus status;
}
