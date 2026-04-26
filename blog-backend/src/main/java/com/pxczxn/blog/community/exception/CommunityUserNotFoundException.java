/**
 * 社区用户未找到异常，当根据 ID 或用户名查询用户不存在时抛出
 */
package com.pxczxn.blog.community.exception;

public class CommunityUserNotFoundException extends RuntimeException {

    /**
     * 根据用户 ID 构造异常
     *
     * @param id 未找到的用户 ID
     */
    public CommunityUserNotFoundException(Long id) {
        super("Community user not found: " + id);
    }

    /**
     * 根据用户名构造异常
     *
     * @param username 未找到的用户名
     */
    public CommunityUserNotFoundException(String username) {
        super("Community user not found: " + username);
    }
}
