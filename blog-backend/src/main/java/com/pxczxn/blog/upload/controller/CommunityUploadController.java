




package com.pxczxn.blog.upload.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.upload.dto.UploadResponse;
import com.pxczxn.blog.upload.exception.UploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/community")
public class CommunityUploadController {

    
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    
    private static final Set<String> IMAGE_CONTENT_TYPES = Set.of("image/png", "image/jpeg", "image/webp", "image/gif");
    
    private static final Set<String> PNG_EXTENSIONS = Set.of("png");
    
    private static final Set<String> JPEG_EXTENSIONS = Set.of("jpg", "jpeg");
    
    private static final Set<String> WEBP_EXTENSIONS = Set.of("webp");
    
    private static final Set<String> GIF_EXTENSIONS = Set.of("gif");

    
    @Value("${file.upload-dir:./upload}")
    private String uploadDir;

    





    @PostMapping("/upload/avatar")
    public Result<UploadResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        validateFile(file);

        try {
            DetectedImageType imageType = detectImageType(file);
            validateFileMetadata(file, imageType);

            String relativePath = buildFilePath(imageType.extension());
            Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path fullPath = uploadRoot.resolve(relativePath).normalize();
            if (!fullPath.startsWith(uploadRoot)) {
                throw UploadException.invalidFileName(relativePath);
            }

            Files.createDirectories(fullPath.getParent());
            file.transferTo(fullPath);

            log.info("社区用户头像上传成功: {}", relativePath);
            UploadResponse response = UploadResponse.builder()
                    .url("/uploads/" + relativePath.replace('\\', '/'))
                    .filename(fullPath.getFileName().toString())
                    .size(file.getSize())
                    .build();

            return Result.success(response);
        } catch (IOException ex) {
            log.error("头像上传失败", ex);
            throw UploadException.uploadFailed(ex.getMessage());
        }
    }

    




    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw UploadException.emptyFile();
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw UploadException.fileTooLarge(file.getSize(), MAX_FILE_SIZE);
        }
    }

    





    private void validateFileMetadata(MultipartFile file, DetectedImageType imageType) {
        String contentType = file.getContentType();
        if (contentType != null) {
            String normalizedContentType = contentType.trim().toLowerCase(Locale.ROOT);
            if (!IMAGE_CONTENT_TYPES.contains(normalizedContentType) || !imageType.contentType().equals(normalizedContentType)) {
                throw UploadException.invalidFileType(contentType);
            }
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!imageType.allowedExtensions().contains(extension)) {
            throw UploadException.invalidFileType(file.getOriginalFilename());
        }
    }

    






    private DetectedImageType detectImageType(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = inputStream.readNBytes(16);
            if (matches(header, (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)) {
                return new DetectedImageType("png", "image/png", PNG_EXTENSIONS);
            }
            if (matches(header, (byte) 0xFF, (byte) 0xD8, (byte) 0xFF)) {
                return new DetectedImageType("jpg", "image/jpeg", JPEG_EXTENSIONS);
            }
            if (matchesAscii(header, "GIF87a") || matchesAscii(header, "GIF89a")) {
                return new DetectedImageType("gif", "image/gif", GIF_EXTENSIONS);
            }
            if (matchesAscii(header, "RIFF") && header.length >= 12 && matchesAscii(header, 8, "WEBP")) {
                return new DetectedImageType("webp", "image/webp", WEBP_EXTENSIONS);
            }
        }
        throw UploadException.invalidFileType(file.getContentType());
    }

    






    private boolean matches(byte[] source, int... expected) {
        if (source.length < expected.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            if ((source[i] & 0xFF) != (expected[i] & 0xFF)) {
                return false;
            }
        }
        return true;
    }

    






    private boolean matchesAscii(byte[] source, String expected) {
        return matchesAscii(source, 0, expected);
    }

    







    private boolean matchesAscii(byte[] source, int offset, String expected) {
        byte[] bytes = expected.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        if (source.length < offset + bytes.length) {
            return false;
        }
        for (int i = 0; i < bytes.length; i++) {
            if (source[offset + i] != bytes[i]) {
                return false;
            }
        }
        return true;
    }

    





    private String buildFilePath(String extension) {
        YearMonth now = YearMonth.now();
        String monthPath = now.format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        return Path.of("avatars", monthPath, filename).toString();
    }

    





    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw UploadException.invalidFileName("文件名缺少有效扩展名");
        }

        String extension = filename.substring(filename.lastIndexOf('.') + 1).trim().toLowerCase(Locale.ROOT);
        if (!extension.matches("[a-z0-9]{2,5}")) {
            throw UploadException.invalidFileName("文件扩展名格式无效");
        }
        return extension;
    }

    






    private record DetectedImageType(String extension, String contentType, Set<String> allowedExtensions) {
    }
}