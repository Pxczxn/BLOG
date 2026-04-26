/**
 * 审核任务数据访问接口
 * <p>
 * 提供审核任务的 CRUD 操作和条件查询
 */
package com.pxczxn.blog.community.moderation.repository;

import com.pxczxn.blog.community.moderation.entity.ModerationContentType;
import com.pxczxn.blog.community.moderation.entity.ModerationTask;
import com.pxczxn.blog.community.moderation.entity.ModerationTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ModerationTaskRepository extends JpaRepository<ModerationTask, Long>, JpaSpecificationExecutor<ModerationTask> {

    /**
     * 查找指定内容和状态的最新审核任务
     *
     * @param contentType 内容类型
     * @param contentId   内容 ID
     * @param status      任务状态
     * @return 审核任务（可选）
     */
    Optional<ModerationTask> findFirstByContentTypeAndContentIdAndStatusOrderByCreatedAtDesc(
            ModerationContentType contentType,
            Long contentId,
            ModerationTaskStatus status
    );
}
