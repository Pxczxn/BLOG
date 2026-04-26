/**
 * 审核内容类型枚举
 * <p>
 * 定义可被审核和举报的内容类型
 */
package com.pxczxn.blog.community.moderation.entity;

public enum ModerationContentType {
    /** 社区帖子 */
    POST,
    /** 文章评论 */
    COMMENT,
    /** 社区帖子评论 */
    POST_COMMENT
}
