/**
 * 后台管理文件上传控制器
 * <p>
 * 提供管理员上传图片文件的功能，支持头像、封面和杂项图片
 */
package com.pxczxn.blog.upload.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.upload.dto.UploadResponse;
import com.pxczxn.blog.upload.exception.UploadException;
import lombok.RequiredArgsConstructor;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UploadController {

    /** 最大文件大小限制：10MB */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    /** 允许的上传目录 */
    private static final Set<String> VALID_DIRS = Set.of("avatars", "covers", "misc");
    /** 允许的图片 Content-Type */
    private static final Set<String> IMAGE_CONTENT_TYPES = Set.of("image/png", "image/jpeg", "image/webp", "image/gif");
    /** PNG 文件扩展名 */
    private static final Set<String> PNG_EXTENSIONS = Set.of("png");
    /** JPEG 文件扩展名 */
    private static final Set<String> JPEG_EXTENSIONS = Set.of("jpg", "jpeg");
    /** WebP 文件扩展名 */
    private static final Set<String> WEBP_EXTENSIONS = Set.of("webp");
    /** GIF 文件扩展名 */
    private static final Set<String> GIF_EXTENSIONS = Set.of("gif");

    /** 文件上传根目录 */
    @Value("${file.upload-dir:./upload}")
    private String uploadDir;

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @param dir  目标目录（avatars/covers/misc），默认 misc
     * @return 上传结果，包含文件访问 URL
     */
    @PostMapping("/upload")
    public Result<UploadResponse> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "dir", defaultValue = "misc") String dir) {
        validateFile(file);

        String normalizedDir = normalizeDir(dir);
        if (!VALID_DIRS.contains(normalizedDir)) {
            throw UploadException.invalidDirectory(dir);
        }

        try {
            DetectedImageType imageType = detectImageType(file);
            validateFileMetadata(file, imageType);

            String relativePath = buildFilePath(normalizedDir, imageType.extension());
            Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path fullPath = uploadRoot.resolve(relativePath).normalize();
            if (!fullPath.startsWith(uploadRoot)) {
                throw UploadException.invalidFileName(relativePath);
            }

            Files.createDirectories(fullPath.getParent());
            file.transferTo(fullPath);

            log.info("文件上传成功: {}", relativePath);
            UploadResponse response = UploadResponse.builder()
                    .url("/uploads/" + relativePath.replace('\\', '/'))
                    .filename(fullPath.getFileName().toString())
                    .size(file.getSize())
                    .build();

            return Result.success(response);
        } catch (IOException ex) {
            log.error("文件上传失败", ex);
            throw UploadException.uploadFailed(ex.getMessage());
        }
    }

    /**
     * 校验文件基本信息（非空、大小限制）
     *
     * @param file 待校验的文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw UploadException.emptyFile();
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw UploadException.fileTooLarge(file.getSize(), MAX_FILE_SIZE);
        }
    }

    /**
     * 标准化目录名
     *
     * @param dir 原始目录名
     * @return 标准化后的目录名（小写、去空格）
     */
    private String normalizeDir(String dir) {
        if (dir == null) {
            return "misc";
        }
        return dir.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 校验文件元数据（Content-Type 和扩展名）
     *
     * @param file      待校验的文件
     * @param imageType 检测到的图片类型
     */
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

    /**
     * 通过文件头魔数检测图片实际类型
     *
     * @param file 待检测的文件
     * @return 检测到的图片类型信息
     * @throws IOException 读取文件失败时抛出
     */
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

    /**
     * 比较字节数组开头是否匹配预期字节序列
     *
     * @param source   源字节数组
     * @param expected 预期字节序列
     * @return 是否匹配
     */
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

    /**
     * 从偏移量0开始比较字节数组是否匹配 ASCII 字符串
     *
     * @param source   源字节数组
     * @param expected 预期 ASCII 字符串
     * @return 是否匹配
     */
    private boolean matchesAscii(byte[] source, String expected) {
        return matchesAscii(source, 0, expected);
    }

    /**
     * 从指定偏移量开始比较字节数组是否匹配 ASCII 字符串
     *
     * @param source   源字节数组
     * @param offset   起始偏移量
     * @param expected 预期 ASCII 字符串
     * @return 是否匹配
     */
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

    /**
     * 构建文件存储相对路径，格式为 {dir}/{yyyy/MM}/{uuid}.{extension}
     *
     * @param dir       目标目录名
     * @param extension 文件扩展名
     * @return 相对于上传根目录的文件路径
     */
    private String buildFilePath(String dir, String extension) {
        YearMonth now = YearMonth.now();
        String monthPath = now.format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        return Path.of(dir, monthPath, filename).toString();
    }

    /**
     * 提取并校验文件扩展名
     *
     * @param filename 原始文件名
     * @return 标准化后的文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw UploadException.invalidFileName("文件名缺少扩展名");
        }

        String extension = filename.substring(filename.lastIndexOf('.') + 1).trim().toLowerCase(Locale.ROOT);
        if (!extension.matches("[a-z0-9]{2,5}")) {
            throw UploadException.invalidFileName("扩展名格式不正确");
        }
        return extension;
    }

    /**
     * 检测到的图片类型信息记录
     *
     * @param extension         文件扩展名
     * @param contentType       Content-Type
     * @param allowedExtensions 该类型允许的扩展名集合
     */
    private record DetectedImageType(String extension, String contentType, Set<String> allowedExtensions) {
    }
}

