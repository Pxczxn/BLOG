




package com.pxczxn.blog.community.moderation.exception;

public class ModerationTaskNotFoundException extends RuntimeException {

    




    public ModerationTaskNotFoundException(Long id) {
        super("Moderation task not found: " + id);
    }
}
