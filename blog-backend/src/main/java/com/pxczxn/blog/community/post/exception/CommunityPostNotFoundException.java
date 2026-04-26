/**
 * 异常
 */
package com.pxczxn.blog.community.post.exception;

public class CommunityPostNotFoundException extends RuntimeException {

    public CommunityPostNotFoundException(Long id) {
        super("Community post not found: " + id);
    }

    public CommunityPostNotFoundException(String slug) {
        super("Community post not found: " + slug);
    }
}

