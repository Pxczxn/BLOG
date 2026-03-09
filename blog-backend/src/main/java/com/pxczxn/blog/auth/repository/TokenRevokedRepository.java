package com.pxczxn.blog.auth.repository;

import com.pxczxn.blog.auth.entity.TokenRevoked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRevokedRepository extends JpaRepository<TokenRevoked, Long> {

    boolean existsByJti(String jti);

    Optional<TokenRevoked> findByJti(String jti);
}
