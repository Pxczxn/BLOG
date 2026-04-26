/**
 * 社区通知类型枚举
 */
package com.pxczxn.blog.community.interaction.entity;

public enum CommunityNotificationType {
    /** 帖子被点赞 */
    POST_LIKED,
    /** 帖子被收藏 */
    POST_FAVORITED,
    /** 被用户关注 */
    USER_FOLLOWED,
    /** 评论被回复 */
    COMMENT_REPLIED,
    /** 系统通知 */
    SYSTEM
}

