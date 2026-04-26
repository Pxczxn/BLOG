


package com.pxczxn.blog.upload.controller;

import com.pxczxn.blog.upload.exception.UploadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UploadControllerTest {

    @TempDir
    Path tempDir;

    @Test
    void uploadAcceptsValidPngSignature() {
        UploadController controller = new UploadController();
        ReflectionTestUtils.setField(controller, "uploadDir", tempDir.toString());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cover.png",
                "image/png",
                new byte[]{
                        (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                        0x00, 0x00, 0x00, 0x00
                }
        );

        var response = controller.upload(file, "covers");

        assertEquals(200, response.getCode());
        assertEquals("image/png", file.getContentType());
        assertEquals(true, response.getData().getUrl().startsWith("/uploads/covers/"));
    }

    @Test
    void uploadRejectsSpoofedImageContent() {
        UploadController controller = new UploadController();
        ReflectionTestUtils.setField(controller, "uploadDir", tempDir.toString());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cover.png",
                "image/png",
                "not-a-real-png".getBytes()
        );

        UploadException exception = assertThrows(UploadException.class, () -> controller.upload(file, "covers"));
        assertEquals("INVALID_FILE_TYPE", exception.getErrorCode().getError());
    }
}

