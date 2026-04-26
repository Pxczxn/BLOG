


package com.pxczxn.blog.community.repository;

import com.pxczxn.blog.community.entity.CommunityTokenRevoked;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityTokenRevokedRepository extends JpaRepository<CommunityTokenRevoked, Long> {

    





    boolean existsByJti(String jti);
}
