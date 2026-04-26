/**
 * 社区用户已吊销令牌数据访问层，提供对 community_token_revoked 表的操作
 */
package com.pxczxn.blog.community.repository;

import com.pxczxn.blog.community.entity.CommunityTokenRevoked;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityTokenRevokedRepository extends JpaRepository<CommunityTokenRevoked, Long> {

    /**
     * 根据令牌唯一标识符（JTI）判断令牌是否已被吊销
     *
     * @param jti JWT 令牌的 JTI 标识
     * @return 若已吊销返回 true，否则返回 false
     */
    boolean existsByJti(String jti);
}
