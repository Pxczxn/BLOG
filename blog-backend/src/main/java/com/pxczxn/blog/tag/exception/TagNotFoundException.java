package com.pxczxn.blog.tag.exception;

public class TagNotFoundException extends RuntimeException {

    public TagNotFoundException(Long id) {
        super("Tag not found: " + id);
    }
}
