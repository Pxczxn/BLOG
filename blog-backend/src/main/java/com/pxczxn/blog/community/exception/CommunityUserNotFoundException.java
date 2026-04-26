


package com.pxczxn.blog.community.exception;

public class CommunityUserNotFoundException extends RuntimeException {

    




    public CommunityUserNotFoundException(Long id) {
        super("Community user not found: " + id);
    }

    




    public CommunityUserNotFoundException(String username) {
        super("Community user not found: " + username);
    }
}
