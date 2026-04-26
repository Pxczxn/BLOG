/**
 * API 错误码枚举
 * <p>
 * 定义统一的错误码和对应的 HTTP 状态码、错误标识、默认消息。
 * 前端可根据错误码进行统一处理和国际化。
 */
package com.pxczxn.blog.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiErrorCode {

    /** 请求参数错误 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "请求参数错误"),
    /** 请求体格式错误 */
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_BODY", "请求体格式错误"),
    /** 请求参数无效 */
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", "请求参数无效"),
    /** 资源不存在 */
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "资源不存在"),
    /** 资源冲突 */
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT", "资源冲突"),
    /** 数据库约束冲突 */
    DATABASE_CONSTRAINT(HttpStatus.CONFLICT, "DATABASE_CONSTRAINT", "数据库约束冲突"),

    /** 请先登录 */
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED", "请先登录"),
    /** 登录凭证无效 */
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_TOKEN", "登录凭证无效"),
    /** 登录凭证已过期 */
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_EXPIRED", "登录凭证已过期"),
    /** 登录会话已失效 */
    AUTH_SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_SESSION_EXPIRED", "登录会话已失效"),
    /** 用户名或密码错误 */
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_CREDENTIALS", "用户名或密码错误"),
    /** 账号已被锁定 */
    AUTH_ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "AUTH_ACCOUNT_LOCKED", "账号已被锁定"),
    /** 无权限执行此操作 */
    AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_FORBIDDEN", "无权限执行此操作"),
    /** 账号已被禁用 */
    AUTH_ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "AUTH_ACCOUNT_DISABLED", "账号已被禁用"),

    /** 文章不存在 */
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE_NOT_FOUND", "文章不存在"),
    /** 分类不存在 */
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "分类不存在"),
    /** 分类下仍有关联内容，无法删除 */
    CATEGORY_IN_USE(HttpStatus.BAD_REQUEST, "CATEGORY_IN_USE", "分类下仍有关联内容，无法删除"),
    /** 标签不存在 */
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "TAG_NOT_FOUND", "标签不存在"),
    /** 评论不存在 */
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "评论不存在"),
    /** 父评论无效 */
    COMMENT_PARENT_INVALID(HttpStatus.BAD_REQUEST, "COMMENT_PARENT_INVALID", "父评论无效"),
    /** 社区节点不存在 */
    NODE_NOT_FOUND(HttpStatus.NOT_FOUND, "NODE_NOT_FOUND", "社区节点不存在"),
    /** 社区帖子不存在 */
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", "社区帖子不存在"),
    /** 审核任务不存在 */
    MODERATION_TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "MODERATION_TASK_NOT_FOUND", "审核任务不存在"),
    /** 举报记录不存在 */
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_NOT_FOUND", "举报记录不存在"),
    /** 用户不存在 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在"),

    /** 上传文件不能为空 */
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "EMPTY_FILE", "上传文件不能为空"),
    /** 上传文件过大 */
    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "FILE_TOO_LARGE", "上传文件过大"),
    /** 不支持的文件类型 */
    INVALID_FILE_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "INVALID_FILE_TYPE", "不支持的文件类型"),
    /** 上传目录无效 */
    INVALID_DIRECTORY(HttpStatus.BAD_REQUEST, "INVALID_DIRECTORY", "上传目录无效"),
    /** 文件名无效 */
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "INVALID_FILE_NAME", "文件名无效"),
    /** 文件上传失败 */
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "UPLOAD_FAILED", "文件上传失败"),

    /** 服务器内部错误 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "服务器内部错误");

    /** HTTP 状态码 */
    private final HttpStatus httpStatus;

    /** 错误标识，用于前端识别错误类型 */
    private final String error;

    /** 默认错误消息 */
    private final String defaultMessage;
}
