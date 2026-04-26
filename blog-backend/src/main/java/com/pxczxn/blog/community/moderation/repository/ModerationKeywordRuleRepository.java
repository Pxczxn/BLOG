




package com.pxczxn.blog.community.moderation.repository;

import com.pxczxn.blog.community.moderation.entity.ModerationKeywordRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModerationKeywordRuleRepository extends JpaRepository<ModerationKeywordRule, Long> {

    




    List<ModerationKeywordRule> findAllByEnabledTrue();
}
