




package com.pxczxn.blog.content.exception;

public class ArticleNotFoundException extends RuntimeException {

    
    private final Object identifier;

    




    public ArticleNotFoundException(Long id) {
        super("Article not found: id=" + id);
        this.identifier = id;
    }

    




    public ArticleNotFoundException(String slug) {
        super("Article not found: slug=" + slug);
        this.identifier = slug;
    }

    




    public Object getIdentifier() {
        return identifier;
    }
}
