/**
 * 全局异常处理器
 * <p>
 * 统一捕获和处理各类异常，返回标准化的错误响应格式。
 * 包括参数校验异常、业务异常、数据库异常等。
 */
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

    /**
     * 处理请求体参数校验异常
     *
     * @param ex 方法参数校验异常
     * @return 包含字段错误信息的响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return build(ApiErrorCode.BAD_REQUEST, "请求参数校验失败", errors);
    }

    /**
     * 处理约束违反异常
     *
     * @param ex 约束违反异常
     * @return 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<String>> handleConstraintViolation(ConstraintViolationException ex) {
        return build(ApiErrorCode.BAD_REQUEST, "请求参数校验失败", ex.getMessage());
    }

    /**
     * 处理请求参数格式错误异常
     * <p>
     * 包括：参数类型不匹配、缺少必需参数、请求体无法解析等。
     *
     * @param ex 异常
     * @return 错误响应
     */
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Result<Void>> handleBadRequest(Exception ex) {
        return build(ApiErrorCode.INVALID_REQUEST_BODY, ApiErrorCode.INVALID_REQUEST_BODY.getDefaultMessage(), null);
    }

    /**
     * 处理资源不存在异常
     *
     * @param ex 资源不存在异常
     * @return 404 响应
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFound(NoResourceFoundException ex) {
        return build(ApiErrorCode.NOT_FOUND, ApiErrorCode.NOT_FOUND.getDefaultMessage(), null);
    }

    /**
     * 处理资源冲突异常
     * <p>
     * 包括：Slug 已存在、用户已存在等。
     *
     * @param ex 运行时异常
     * @return 409 冲突响应
     */
    @ExceptionHandler({SlugAlreadyExistsException.class, UserAlreadyExistsException.class})
    public ResponseEntity<Result<Void>> handleConflict(RuntimeException ex) {
        return build(ApiErrorCode.CONFLICT, ex.getMessage(), null);
    }

    /**
     * 处理文章不存在异常
     *
     * @param ex 文章不存在异常
     * @return 404 响应
     */
    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<Result<Void>> handleArticleNotFound(ArticleNotFoundException ex) {
        return build(ApiErrorCode.ARTICLE_NOT_FOUND, ApiErrorCode.ARTICLE_NOT_FOUND.getDefaultMessage(), null);
    }

    /**
     * 处理分类不存在异常
     *
     * @param ex 分类不存在异常
     * @return 404 响应
     */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCategoryNotFound(CategoryNotFoundException ex) {
        return build(ApiErrorCode.CATEGORY_NOT_FOUND, ApiErrorCode.CATEGORY_NOT_FOUND.getDefaultMessage(), null);
    }

    /**
     * 处理标签不存在异常
     *
     * @param ex 标签不存在异常
     * @return 404 响应
     */
    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<Result<Void>> handleTagNotFound(TagNotFoundException ex) {
        return build(ApiErrorCode.TAG_NOT_FOUND, ApiErrorCode.TAG_NOT_FOUND.getDefaultMessage(), null);
    }

    /**
     * 处理分类使用中异常
     *
     * @param ex 分类使用中异常
     * @return 400 响应
     */
    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<Result<Void>> handleCategoryInUse(CategoryInUseException ex) {
        return build(ApiErrorCode.CATEGORY_IN_USE, ApiErrorCode.CATEGORY_IN_USE.getDefaultMessage(), null);
    }

    /**
     * 处理评论不存在异常
     *
     * @param ex 评论不存在异常
     * @return 404 响应
     */
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCommentNotFound(CommentNotFoundException ex) {
        return build(ApiErrorCode.COMMENT_NOT_FOUND, ApiErrorCode.COMMENT_NOT_FOUND.getDefaultMessage(), null);
    }

    /**
     * 处理父评论无效异常
     *
     * @param ex 父评论无效异常
     * @return 400 响应
     */
    @ExceptionHandler(ParentCommentInvalidException.class)
    public ResponseEntity<Result<Void>> handleParentInvalid(ParentCommentInvalidException ex) {
        return build(ApiErrorCode.COMMENT_PARENT_INVALID, ApiErrorCode.COMMENT_PARENT_INVALID.getDefaultMessage(), null);
    }

    /**
     * 处理社区节点不存在异常
     *
     * @param ex 社区节点不存在异常
     * @return 404 响应
     */
    @ExceptionHandler(CommunityNodeNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCommunityNodeNotFound(CommunityNodeNotFoundException ex) {
        return build(ApiErrorCode.NODE_NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * 处理社区帖子不存在异常
     *
     * @param ex 社区帖子不存在异常
     * @return 404 响应
     */
    @ExceptionHandler(CommunityPostNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCommunityPostNotFound(CommunityPostNotFoundException ex) {
        return build(ApiErrorCode.POST_NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * 处理审核任务不存在异常
     *
     * @param ex 审核任务不存在异常
     * @return 404 响应
     */
    @ExceptionHandler(ModerationTaskNotFoundException.class)
    public ResponseEntity<Result<Void>> handleModerationTaskNotFound(ModerationTaskNotFoundException ex) {
        return build(ApiErrorCode.MODERATION_TASK_NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * 处理举报记录不存在异常
     *
     * @param ex 举报记录不存在异常
     * @return 404 响应
     */
    @ExceptionHandler(ContentReportNotFoundException.class)
    public ResponseEntity<Result<Void>> handleContentReportNotFound(ContentReportNotFoundException ex) {
        return build(ApiErrorCode.REPORT_NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * 处理管理员用户不存在异常
     *
     * @param ex 用户不存在异常
     * @return 404 响应
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Result<Void>> handleUserNotFound(UserNotFoundException ex) {
        return build(ApiErrorCode.USER_NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * 处理社区用户不存在异常
     *
     * @param ex 社区用户不存在异常
     * @return 404 响应
     */
    @ExceptionHandler(CommunityUserNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCommunityUserNotFound(CommunityUserNotFoundException ex) {
        return build(ApiErrorCode.USER_NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * 处理登录异常
     *
     * @param ex 登录异常
     * @return 对应状态的错误响应
     */
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Result<Void>> handleLoginException(LoginException ex) {
        return build(ex.getErrorCode(), ex.getMessage(), null);
    }

    /**
     * 处理社区认证异常
     *
     * @param ex 社区认证异常
     * @return 对应状态的错误响应
     */
    @ExceptionHandler(CommunityAuthException.class)
    public ResponseEntity<Result<Void>> handleCommunityAuthException(CommunityAuthException ex) {
        return build(ex.getErrorCode(), ex.getMessage(), null);
    }

    /**
     * 处理上传异常
     *
     * @param ex 上传异常
     * @return 对应状态的错误响应
     */
    @ExceptionHandler(UploadException.class)
    public ResponseEntity<Result<Void>> handleUploadException(UploadException ex) {
        return build(ex.getErrorCode(), ex.getMessage(), null);
    }

    /**
     * 处理文件大小超限异常
     *
     * @param ex 文件大小超限异常
     * @return 413 响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        return build(ApiErrorCode.FILE_TOO_LARGE, ApiErrorCode.FILE_TOO_LARGE.getDefaultMessage(), null);
    }

    /**
     * 处理非法参数异常
     *
     * @param ex 非法参数异常
     * @return 400 响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return build(ApiErrorCode.INVALID_ARGUMENT, ex.getMessage(), null);
    }

    /**
     * 处理非法状态异常
     *
     * @param ex 非法状态异常
     * @return 401 响应
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Result<Void>> handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        return build(ApiErrorCode.AUTH_REQUIRED, ex.getMessage(), null);
    }

    /**
     * 处理数据库完整性约束违反异常
     *
     * @param ex 数据库完整性约束违反异常
     * @return 409 响应
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Database constraint violated: {}", ex.getMessage());
        return build(ApiErrorCode.DATABASE_CONSTRAINT, ApiErrorCode.DATABASE_CONSTRAINT.getDefaultMessage(), null);
    }

    /**
     * 处理未捕获的异常
     * <p>
     * 作为兜底处理，记录错误日志并返回 500 响应。
     *
     * @param ex 异常
     * @return 500 响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return build(ApiErrorCode.INTERNAL_SERVER_ERROR, ApiErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(), null);
    }

    /**
     * 构建错误响应
     *
     * @param errorCode 错误码枚举
     * @param message 错误消息
     * @param data 附加数据
     * @return 错误响应实体
     */
    private <T> ResponseEntity<Result<T>> build(ApiErrorCode errorCode, String message, T data) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.error(errorCode, message, data));
    }
}
