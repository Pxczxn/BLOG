package com.pxczxn.blog.tag.repository;

import com.pxczxn.blog.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    Optional<Tag> findBySlug(String slug);

    List<Tag> findAllByOrderByCreatedAtDesc();
}
