package com.pxczxn.blog.upload.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.upload.dto.UploadResponse;
import com.pxczxn.blog.upload.exception.UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UploadController {

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/png",
            "image/jpeg",
            "image/webp",
            "image/gif"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private static final List<String> VALID_DIRS = Arrays.asList("avatars", "covers", "misc");

    @Value("${file.upload-dir:./upload}")
    private String uploadDir;

    @PostMapping("/upload")
    public Result<UploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dir", defaultValue = "misc") String dir) {

        // 校验文件
        validateFile(file);

        // 校验目录参数
        if (!VALID_DIRS.contains(dir)) {
            throw UploadException.invalidDirectory(dir);
        }

        try {
            // 生成文件路径
            String extension = getFileExtension(file.getOriginalFilename());
            String relativePath = buildFilePath(dir, extension);
            Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path fullPath = uploadRoot.resolve(relativePath).normalize();

            if (!fullPath.startsWith(uploadRoot)) {
                throw UploadException.invalidFileName(relativePath);
            }

            // 确保目录存在
            Files.createDirectories(fullPath.getParent());

            // 保存文件
            file.transferTo(fullPath.toFile());

            log.info("文件上传成功: {}", relativePath);

            // 构造响应
            UploadResponse response = UploadResponse.builder()
                    .url("/uploads/" + relativePath)
                    .filename(fullPath.getFileName().toString())
                    .size(file.getSize())
                    .build();

            return Result.success(response);

        } catch (IOException | RuntimeException e) {
            log.error("文件上传失败", e);
            throw UploadException.uploadFailed(e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw UploadException.emptyFile();
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw UploadException.fileTooLarge(file.getSize(), MAX_FILE_SIZE);
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw UploadException.invalidFileType(contentType);
        }
    }

    private String buildFilePath(String dir, String extension) {
        // 按年月创建子目录：avatars/2026/03/uuid.png
        YearMonth now = YearMonth.now();
        String monthPath = now.format(DateTimeFormatter.ofPattern("yyyy/MM"));

        String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        return dir + "/" + monthPath + "/" + filename;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw UploadException.invalidFileName("文件名不包含扩展名");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
