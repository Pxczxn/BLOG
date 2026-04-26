




package com.pxczxn.blog.community.moderation.repository;

import com.pxczxn.blog.community.moderation.entity.ModerationContentType;
import com.pxczxn.blog.community.moderation.entity.ModerationTask;
import com.pxczxn.blog.community.moderation.entity.ModerationTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ModerationTaskRepository extends JpaRepository<ModerationTask, Long>, JpaSpecificationExecutor<ModerationTask> {

    







    Optional<ModerationTask> findFirstByContentTypeAndContentIdAndStatusOrderByCreatedAtDesc(
            ModerationContentType contentType,
            Long contentId,
            ModerationTaskStatus status
    );
}
