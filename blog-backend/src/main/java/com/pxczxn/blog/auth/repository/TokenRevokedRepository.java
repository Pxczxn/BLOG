/**
 * 已撤销令牌数据访问层
 * <p>
 * 提供已撤销令牌实体的 CRUD 操作及按 JTI（令牌唯一标识）查询方法。
 */
package com.pxczxn.blog.auth.repository;

import com.pxczxn.blog.auth.entity.TokenRevoked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRevokedRepository extends JpaRepository<TokenRevoked, Long> {

    /** 检查指定 JTI 是否已被撤销 */
    boolean existsByJti(String jti);

    /** 根据 JTI 查询撤销记录 */
    Optional<TokenRevoked> findByJti(String jti);
}

