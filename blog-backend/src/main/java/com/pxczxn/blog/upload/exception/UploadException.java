package com.pxczxn.blog.upload.exception;

public class UploadException extends RuntimeException {

    private final String errorCode;

    public UploadException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static UploadException emptyFile() {
        return new UploadException("EMPTY_FILE", "上传文件不能为空");
    }

    public static UploadException fileTooLarge(long actualSize, long maxSize) {
        double actualMB = actualSize / 1024.0 / 1024.0;
        double maxMB = maxSize / 1024.0 / 1024.0;
        return new UploadException("FILE_TOO_LARGE",
                String.format("文件大小 %.2fMB 超过限制 %.2fMB", actualMB, maxMB));
    }

    public static UploadException invalidFileType(String contentType) {
        return new UploadException("INVALID_FILE_TYPE",
                "只支持 PNG、JPEG、WebP、GIF 格式的图片，当前类型: " + contentType);
    }

    public static UploadException invalidDirectory(String dir) {
        return new UploadException("INVALID_DIRECTORY",
                "无效的目录参数，只支持：avatars, covers, misc，当前：" + dir);
    }
    
    public static UploadException invalidFileName(String reason) {
        return new UploadException("INVALID_FILE_NAME", "无效的文件名：" + reason);
    }
    
    public static UploadException uploadFailed(String reason) {
        return new UploadException("UPLOAD_FAILED", "文件上传失败: " + reason);
    }
}
