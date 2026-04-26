/**
 * 审核规则命中数据访问接口
 * <p>
 * 提供规则命中记录的查询和删除功能
 */
package com.pxczxn.blog.community.moderation.repository;

import com.pxczxn.blog.community.moderation.entity.ModerationRuleHit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModerationRuleHitRepository extends JpaRepository<ModerationRuleHit, Long> {

    /**
     * 按任务 ID 查询所有命中记录（按 ID 升序）
     *
     * @param taskId 任务 ID
     * @return 命中记录列表
     */
    List<ModerationRuleHit> findAllByTaskIdOrderByIdAsc(Long taskId);

    /**
     * 按任务 ID 删除所有命中记录
     *
     * @param taskId 任务 ID
     */
    void deleteByTaskId(Long taskId);
}
