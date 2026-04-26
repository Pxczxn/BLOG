/**
 * 文件上传业务异常
 */
package com.pxczxn.blog.upload.exception;

import com.pxczxn.blog.common.response.ApiErrorCode;
import lombok.Getter;

/**
 * 文件上传相关的业务异常，包含错误码和详细提示信息
 */
@Getter
public class UploadException extends RuntimeException {

    /** 错误码 */
    private final ApiErrorCode errorCode;

    /**
     * 构造上传异常
     *
     * @param errorCode 错误码
     * @param message   错误详细信息
     */
    public UploadException(ApiErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 创建空文件异常
     *
     * @return 上传文件为空时的异常实例
     */
    public static UploadException emptyFile() {
        return new UploadException(ApiErrorCode.EMPTY_FILE, ApiErrorCode.EMPTY_FILE.getDefaultMessage());
    }

    /**
     * 创建文件过大异常
     *
     * @param actualSize 实际文件大小（字节）
     * @param maxSize    允许的最大文件大小（字节）
     * @return 文件超过大小限制时的异常实例
     */
    public static UploadException fileTooLarge(long actualSize, long maxSize) {
        double actualMB = actualSize / 1024.0 / 1024.0;
        double maxMB = maxSize / 1024.0 / 1024.0;
        return new UploadException(ApiErrorCode.FILE_TOO_LARGE,
                String.format("上传文件大小 %.2fMB，超过限制 %.2fMB", actualMB, maxMB));
    }

    /**
     * 创建文件类型无效异常
     *
     * @param contentType 实际的文件类型
     * @return 文件类型不合法时的异常实例
     */
    public static UploadException invalidFileType(String contentType) {
        return new UploadException(ApiErrorCode.INVALID_FILE_TYPE,
                "仅支持 PNG、JPEG、WebP、GIF 格式图片，当前类型: " + contentType);
    }

    /**
     * 创建上传目录无效异常
     *
     * @param dir 用户指定的上传目录
     * @return 上传目录不合法时的异常实例
     */
    public static UploadException invalidDirectory(String dir) {
        return new UploadException(ApiErrorCode.INVALID_DIRECTORY,
                "上传目录无效，仅支持 avatars、covers、misc，当前目录: " + dir);
    }

    /**
     * 创建文件名无效异常
     *
     * @param reason 文件名无效的原因
     * @return 文件名不合法时的异常实例
     */
    public static UploadException invalidFileName(String reason) {
        return new UploadException(ApiErrorCode.INVALID_FILE_NAME, "文件名无效: " + reason);
    }

    /**
     * 创建上传失败异常
     *
     * @param reason 上传失败的原因
     * @return 文件上传失败时的异常实例
     */
    public static UploadException uploadFailed(String reason) {
        return new UploadException(ApiErrorCode.UPLOAD_FAILED, "文件上传失败: " + reason);
    }
}