




package com.pxczxn.blog.community.moderation.repository;

import com.pxczxn.blog.community.moderation.entity.ModerationRuleHit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModerationRuleHitRepository extends JpaRepository<ModerationRuleHit, Long> {

    





    List<ModerationRuleHit> findAllByTaskIdOrderByIdAsc(Long taskId);

    




    void deleteByTaskId(Long taskId);
}
