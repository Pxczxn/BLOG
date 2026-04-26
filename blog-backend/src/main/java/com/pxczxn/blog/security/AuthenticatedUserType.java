/**
 * 认证用户类型枚举
 * <p>
 * 区分管理员和社区用户两种认证类型
 */
package com.pxczxn.blog.security;

/**
 * 认证用户类型枚举
 * <p>
 * 用于区分系统中不同类型的认证用户，主要分为管理员和社区用户两类
 */
public enum AuthenticatedUserType {
    /**
     * 管理员用户
     * <p>
     * 拥有后台管理权限的用户，可以管理文章、评论、用户等内容
     */
    ADMIN,

    /**
     * 社区用户
     * <p>
     * 注册的普通社区用户，可以发布内容、评论、点赞等
     */
    COMMUNITY
}
