package com.pxczxn.blog.community.node.exception;






public class CommunityNodeNotFoundException extends RuntimeException {

    




    public CommunityNodeNotFoundException(Long id) {
        super("Community node not found: " + id);
    }

    




    public CommunityNodeNotFoundException(String slug) {
        super("Community node not found: " + slug);
    }
}
