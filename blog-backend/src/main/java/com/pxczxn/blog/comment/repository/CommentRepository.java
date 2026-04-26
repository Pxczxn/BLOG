/**
 * 评论数据访问层
 * <p>
 * 提供评论实体的 CRUD 操作及按文章、状态查询等方法。
 */
package com.pxczxn.blog.comment.repository;

import com.pxczxn.blog.comment.entity.Comment;
import com.pxczxn.blog.comment.entity.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 按审核状态分页查询评论，按创建时间倒序
     *
     * @param status   审核状态
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByStatusOrderByCreatedAtDesc(CommentStatus status, Pageable pageable);

    /**
     * 按文章ID和审核状态查询评论，按创建时间正序
     *
     * @param articleId 文章ID
     * @param status    审核状态
     * @return 评论列表
     */
    List<Comment> findByArticleIdAndStatusOrderByCreatedAtAsc(Long articleId, CommentStatus status);

    /**
     * 统计指定文章下指定状态的评论数量
     *
     * @param articleId 文章ID
     * @param status    审核状态
     * @return 评论数量
     */
    long countByArticleIdAndStatus(Long articleId, CommentStatus status);

    /**
     * 按ID查询评论
     *
     * @param id 评论ID
     * @return 评论实体（可能为空）
     */
    Optional<Comment> findById(Long id);
}

