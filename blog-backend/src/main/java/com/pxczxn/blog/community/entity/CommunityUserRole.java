/**
 * 社区用户角色枚举，定义用户在社区中的权限级别
 */
package com.pxczxn.blog.community.entity;

public enum CommunityUserRole {

    /** 普通用户，拥有基本的社区功能权限 */
    USER,

    /** 版主，拥有内容审核等管理权限 */
    MODERATOR
}
