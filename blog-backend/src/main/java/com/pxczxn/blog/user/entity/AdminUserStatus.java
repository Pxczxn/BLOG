/**
 * 管理员用户状态枚举
 * <p>
 * 定义管理员账户的可用状态
 */
package com.pxczxn.blog.user.entity;

/**
 * 管理员用户状态枚举
 */
public enum AdminUserStatus {

    /** 正常状态 - 账户可用 */
    ACTIVE,

    /** 禁用状态 - 账户被禁止登录 */
    BANNED
}
