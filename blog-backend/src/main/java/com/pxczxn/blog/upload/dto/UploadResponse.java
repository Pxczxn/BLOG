package com.pxczxn.blog.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {

    /**
     * 文件访问URL
     */
    private String url;

    /**
     * 存储的文件名
     */
    private String filename;

    /**
     * 文件大小（字节）
     */
    private Long size;
}
