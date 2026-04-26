/**
 * 社区用户状态枚举，定义用户的账号状态
 */
package com.pxczxn.blog.community.entity;

public enum CommunityUserStatus {

    /** 正常状态，用户可以正常使用所有功能 */
    ACTIVE,

    /** 待激活状态，用户注册后尚未完成邮箱验证 */
    PENDING,

    /** 已封禁状态，用户被禁止登录和使用功能 */
    BANNED
}
