/**
 * 审核关键词规则数据访问接口
 * <p>
 * 提供关键词规则的查询功能
 */
package com.pxczxn.blog.community.moderation.repository;

import com.pxczxn.blog.community.moderation.entity.ModerationKeywordRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModerationKeywordRuleRepository extends JpaRepository<ModerationKeywordRule, Long> {

    /**
     * 查询所有已启用的关键词规则
     *
     * @return 已启用的关键词规则列表
     */
    List<ModerationKeywordRule> findAllByEnabledTrue();
}
