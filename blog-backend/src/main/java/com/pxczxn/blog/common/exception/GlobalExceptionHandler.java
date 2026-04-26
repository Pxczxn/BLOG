





package com.pxczxn.blog.common.exception;

import com.pxczxn.blog.auth.exception.LoginException;
import com.pxczxn.blog.category.exception.CategoryInUseException;
import com.pxczxn.blog.category.exception.CategoryNotFoundException;
import com.pxczxn.blog.comment.exception.CommentNotFoundException;
import com.pxczxn.blog.comment.exception.ParentCommentInvalidException;
import com.pxczxn.blog.common.response.ApiErrorCode;
import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.exception.CommunityAuthException;
import com.pxczxn.blog.community.exception.CommunityUserNotFoundException;
import com.pxczxn.blog.community.moderation.exception.ContentReportNotFoundException;
import com.pxczxn.blog.community.moderation.exception.ModerationTaskNotFoundException;
import com.pxczxn.blog.community.node.exception.CommunityNodeNotFoundException;
import com.pxczxn.blog.community.post.exception.CommunityPostNotFoundException;
import com.pxczxn.blog.content.exception.ArticleNotFoundException;
import com.pxczxn.blog.content.exception.SlugAlreadyExistsException;
import com.pxczxn.blog.tag.exception.TagNotFoundException;
import com.pxczxn.blog.upload.exception.UploadException;
import com.pxczxn.blog.user.exception.UserAlreadyExistsException;
import com.pxczxn.blog.user.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    





    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return build(ApiErrorCode.BAD_REQUEST, "请求参数校验失败", errors);
    }

    





    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<String>> handleConstraintViolation(ConstraintViolationException ex) {
        return build(ApiErrorCode.BAD_REQUEST, "请求参数校验失败", ex.getMessage());
    }

    







    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Result<Void>> handleBadRequest(Exception ex) {
        return build(ApiErrorCode.INVALID_REQUEST_BODY, ApiErrorCode.INVALID_REQUEST_BODY.getDefaultMessage(), null);
    }

    





    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFound(NoResourceFoundException ex) {
        return build(ApiErrorCode.NOT_FOUND, ApiErrorCode.NOT_FOUND.getDefaultMessage(), null);
    }

    







    @ExceptionHandler({SlugAlreadyExistsException.class, UserAlreadyExistsException.class})
    public ResponseEntity<Result<Void>> handleConflict(RuntimeException ex) {
        return build(ApiErrorCode.CONFLICT, ex.getMessage(), null);
    }

    





    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<Result<Void>> handleArticleNotFound(ArticleNotFoundException ex) {
        return build(ApiErrorCode.ARTICLE_NOT_FOUND, ApiErrorCode.ARTICLE_NOT_FOUND.getDefaultMessage(), null);
    }

    





    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCategoryNotFound(CategoryNotFoundException ex) {
        return build(ApiErrorCode.CATEGORY_NOT_FOUND, ApiErrorCode.CATEGORY_NOT_FOUND.getDefaultMessage(), null);
    }

    





    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<Result<Void>> handleTagNotFound(TagNotFoundException ex) {
        return build(ApiErrorCode.TAG_NOT_FOUND, ApiErrorCode.TAG_NOT_FOUND.getDefaultMessage(), null);
    }

    





    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<Result<Void>> handleCategoryInUse(CategoryInUseException ex) {
        return build(ApiErrorCode.CATEGORY_IN_USE, ApiErrorCode.CATEGORY_IN_USE.getDefaultMessage(), null);
    }

    





    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCommentNotFound(CommentNotFoundException ex) {
        return build(ApiErrorCode.COMMENT_NOT_FOUND, ApiErrorCode.COMMENT_NOT_FOUND.getDefaultMessage(), null);
    }

    





    @ExceptionHandler(ParentCommentInvalidException.class)
    public ResponseEntity<Result<Void>> handleParentInvalid(ParentCommentInvalidException ex) {
        return build(ApiErrorCode.COMMENT_PARENT_INVALID, ApiErrorCode.COMMENT_PARENT_INVALID.getDefaultMessage(), null);
    }

    





    @ExceptionHandler(CommunityNodeNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCommunityNodeNotFound(CommunityNodeNotFoundException ex) {
        return build(ApiErrorCode.NODE_NOT_FOUND, ex.getMessage(), null);
    }

    





    @ExceptionHandler(CommunityPostNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCommunityPostNotFound(CommunityPostNotFoundException ex) {
        return build(ApiErrorCode.POST_NOT_FOUND, ex.getMessage(), null);
    }

    





    @ExceptionHandler(ModerationTaskNotFoundException.class)
    public ResponseEntity<Result<Void>> handleModerationTaskNotFound(ModerationTaskNotFoundException ex) {
        return build(ApiErrorCode.MODERATION_TASK_NOT_FOUND, ex.getMessage(), null);
    }

    





    @ExceptionHandler(ContentReportNotFoundException.class)
    public ResponseEntity<Result<Void>> handleContentReportNotFound(ContentReportNotFoundException ex) {
        return build(ApiErrorCode.REPORT_NOT_FOUND, ex.getMessage(), null);
    }

    





    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Result<Void>> handleUserNotFound(UserNotFoundException ex) {
        return build(ApiErrorCode.USER_NOT_FOUND, ex.getMessage(), null);
    }

    





    @ExceptionHandler(CommunityUserNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCommunityUserNotFound(CommunityUserNotFoundException ex) {
        return build(ApiErrorCode.USER_NOT_FOUND, ex.getMessage(), null);
    }

    





    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Result<Void>> handleLoginException(LoginException ex) {
        return build(ex.getErrorCode(), ex.getMessage(), null);
    }

    





    @ExceptionHandler(CommunityAuthException.class)
    public ResponseEntity<Result<Void>> handleCommunityAuthException(CommunityAuthException ex) {
        return build(ex.getErrorCode(), ex.getMessage(), null);
    }

    





    @ExceptionHandler(UploadException.class)
    public ResponseEntity<Result<Void>> handleUploadException(UploadException ex) {
        return build(ex.getErrorCode(), ex.getMessage(), null);
    }

    





    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        return build(ApiErrorCode.FILE_TOO_LARGE, ApiErrorCode.FILE_TOO_LARGE.getDefaultMessage(), null);
    }

    





    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return build(ApiErrorCode.INVALID_ARGUMENT, ex.getMessage(), null);
    }

    





    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Result<Void>> handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        return build(ApiErrorCode.AUTH_REQUIRED, ex.getMessage(), null);
    }

    





    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Database constraint violated: {}", ex.getMessage());
        return build(ApiErrorCode.DATABASE_CONSTRAINT, ApiErrorCode.DATABASE_CONSTRAINT.getDefaultMessage(), null);
    }

    







    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return build(ApiErrorCode.INTERNAL_SERVER_ERROR, ApiErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(), null);
    }

    







    private <T> ResponseEntity<Result<T>> build(ApiErrorCode errorCode, String message, T data) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.error(errorCode, message, data));
    }
}
