/**
 * 认证用户主体
 * <p>
 * 封装已认证用户的基本信息（用户ID、用户名、角色、类型）
 */
package com.pxczxn.blog.security;

/**
 * 认证用户主体
 * <p>
 * 封装已认证用户的基本信息，用于在安全上下文中传递用户身份信息
 *
 * @param userId   用户ID，唯一标识用户
 * @param username 用户名，用于显示和日志记录
 * @param role     用户角色，定义用户权限范围
 * @param userType 用户类型，区分管理员和社区用户
 */
public record AuthenticatedUserPrincipal(
        Long userId,
        String username,
        String role,
        AuthenticatedUserType userType
) {
    /**
     * 判断当前用户是否为社区用户
     *
     * @return 如果是社区用户返回 true，否则返回 false
     */
    public boolean isCommunityUser() {
        return userType == AuthenticatedUserType.COMMUNITY;
    }
}
