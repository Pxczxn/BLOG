




package com.pxczxn.blog.category.repository;

import com.pxczxn.blog.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    





    boolean existsBySlug(String slug);

    





    boolean existsByName(String name);

    





    Optional<Category> findBySlug(String slug);

    




    List<Category> findAllByOrderByCreatedAtDesc();
}

