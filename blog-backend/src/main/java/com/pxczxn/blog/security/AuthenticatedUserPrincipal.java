




package com.pxczxn.blog.security;











public record AuthenticatedUserPrincipal(
        Long userId,
        String username,
        String role,
        AuthenticatedUserType userType
) {
    




    public boolean isCommunityUser() {
        return userType == AuthenticatedUserType.COMMUNITY;
    }
}
