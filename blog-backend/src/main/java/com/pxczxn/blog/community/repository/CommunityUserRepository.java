


package com.pxczxn.blog.community.repository;

import com.pxczxn.blog.community.entity.CommunityUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityUserRepository extends JpaRepository<CommunityUser, Long> {

    





    Optional<CommunityUser> findByUsername(String username);

    






    Optional<CommunityUser> findByUsernameOrEmail(String username, String email);

    





    boolean existsByUsername(String username);

    





    boolean existsByEmail(String email);
}
