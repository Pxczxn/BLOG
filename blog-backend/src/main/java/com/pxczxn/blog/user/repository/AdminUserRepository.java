/**
 * 管理员用户数据访问层
 * <p>
 * 提供管理员用户实体的数据库操作接口
 */
package com.pxczxn.blog.user.repository;

import com.pxczxn.blog.user.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理员用户 Repository
 */
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体（可能为空）
     */
    Optional<AdminUser> findByUsername(String username);

    /**
     * 根据用户名或邮箱查询用户
     *
     * @param username 用户名
     * @param email 邮箱地址
     * @return 用户实体（可能为空）
     */
    Optional<AdminUser> findByUsernameOrEmail(String username, String email);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱地址
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByEmail(String email);
}
