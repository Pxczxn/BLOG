




package com.pxczxn.blog.content.exception;

public class SlugAlreadyExistsException extends RuntimeException {

    
    private final String slug;

    




    public SlugAlreadyExistsException(String slug) {
        super("Article slug already exists: " + slug);
        this.slug = slug;
    }

    




    public String getSlug() {
        return slug;
    }
}
