




package com.pxczxn.blog.community.moderation.exception;

public class ContentReportNotFoundException extends RuntimeException {

    




    public ContentReportNotFoundException(Long id) {
        super("Content report not found: " + id);
    }
}
