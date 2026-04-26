/**
 * 社区用户数据访问层，提供对 community_user 表的操作
 */
package com.pxczxn.blog.community.repository;

import com.pxczxn.blog.community.entity.CommunityUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityUserRepository extends JpaRepository<CommunityUser, Long> {

    /**
     * 根据用户名查找社区用户
     *
     * @param username 用户名
     * @return 匹配的用户，若不存在返回空的 Optional
     */
    Optional<CommunityUser> findByUsername(String username);

    /**
     * 根据用户名或邮箱查找社区用户（用于登录时支持用户名/邮箱两种方式）
     *
     * @param username 用户名
     * @param email    邮箱地址
     * @return 匹配的用户，若不存在返回空的 Optional
     */
    Optional<CommunityUser> findByUsernameOrEmail(String username, String email);

    /**
     * 判断指定用户名是否已存在
     *
     * @param username 用户名
     * @return 若已存在返回 true，否则返回 false
     */
    boolean existsByUsername(String username);

    /**
     * 判断指定邮箱是否已被注册
     *
     * @param email 邮箱地址
     * @return 若已注册返回 true，否则返回 false
     */
    boolean existsByEmail(String email);
}
