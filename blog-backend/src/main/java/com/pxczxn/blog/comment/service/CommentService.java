package com.pxczxn.blog.comment.service;

import com.pxczxn.blog.comment.dto.AdminCommentItemResponse;
import com.pxczxn.blog.comment.dto.AdminCommentStatusResponse;
import com.pxczxn.blog.comment.dto.CommentItemResponse;
import com.pxczxn.blog.comment.dto.CreateCommentRequest;
import com.pxczxn.blog.comment.dto.CreateCommentResponse;
import com.pxczxn.blog.comment.entity.Comment;
import com.pxczxn.blog.comment.entity.CommentStatus;
import com.pxczxn.blog.comment.exception.CommentNotFoundException;
import com.pxczxn.blog.comment.exception.ParentCommentInvalidException;
import com.pxczxn.blog.comment.repository.CommentRepository;
import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.content.exception.ArticleNotFoundException;
import com.pxczxn.blog.content.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public CreateCommentResponse create(CreateCommentRequest request) {
        Long articleId = request.getArticleId();
        if (!articleRepository.existsById(articleId)) {
            throw new ArticleNotFoundException(articleId);
        }

        Long parentId = request.getParentId();
        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(ParentCommentInvalidException::new);
            if (!parent.getArticleId().equals(articleId)) {
                throw new ParentCommentInvalidException();
            }
        }

        Comment comment = Comment.builder()
                .articleId(articleId)
                .parentId(parentId)
                .nickname(request.getNickname().trim())
                .email(trimToNull(request.getEmail()))
                .content(request.getContent().trim())
                .status(CommentStatus.PENDING)
                .build();

        return CreateCommentResponse.from(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentItemResponse> listApprovedByArticle(Long articleId) {
        return commentRepository.findByArticleIdAndStatusOrderByCreatedAtAsc(articleId, CommentStatus.APPROVED).stream()
                .map(CommentItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminCommentItemResponse> listByStatus(CommentStatus status, Pageable pageable, int page) {
        Page<Comment> result = commentRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        List<AdminCommentItemResponse> items = result.getContent().stream()
                .map(AdminCommentItemResponse::from)
                .toList();
        return new PageResponse<>(items, result.getTotalElements(), page, result.getSize());
    }

    @Transactional
    public AdminCommentStatusResponse approve(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        comment.setStatus(CommentStatus.APPROVED);
        return AdminCommentStatusResponse.from(commentRepository.save(comment));
    }

    @Transactional
    public AdminCommentStatusResponse reject(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        comment.setStatus(CommentStatus.REJECTED);
        return AdminCommentStatusResponse.from(commentRepository.save(comment));
    }

    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        commentRepository.delete(comment);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
