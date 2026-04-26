


package com.pxczxn.blog.upload.exception;

import com.pxczxn.blog.common.response.ApiErrorCode;
import lombok.Getter;




@Getter
public class UploadException extends RuntimeException {

    
    private final ApiErrorCode errorCode;

    





    public UploadException(ApiErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    




    public static UploadException emptyFile() {
        return new UploadException(ApiErrorCode.EMPTY_FILE, ApiErrorCode.EMPTY_FILE.getDefaultMessage());
    }

    






    public static UploadException fileTooLarge(long actualSize, long maxSize) {
        double actualMB = actualSize / 1024.0 / 1024.0;
        double maxMB = maxSize / 1024.0 / 1024.0;
        return new UploadException(ApiErrorCode.FILE_TOO_LARGE,
                String.format("上传文件大小 %.2fMB，超过限制 %.2fMB", actualMB, maxMB));
    }

    





    public static UploadException invalidFileType(String contentType) {
        return new UploadException(ApiErrorCode.INVALID_FILE_TYPE,
                "仅支持 PNG、JPEG、WebP、GIF 格式图片，当前类型: " + contentType);
    }

    





    public static UploadException invalidDirectory(String dir) {
        return new UploadException(ApiErrorCode.INVALID_DIRECTORY,
                "上传目录无效，仅支持 avatars、covers、misc，当前目录: " + dir);
    }

    





    public static UploadException invalidFileName(String reason) {
        return new UploadException(ApiErrorCode.INVALID_FILE_NAME, "文件名无效: " + reason);
    }

    





    public static UploadException uploadFailed(String reason) {
        return new UploadException(ApiErrorCode.UPLOAD_FAILED, "文件上传失败: " + reason);
    }
}