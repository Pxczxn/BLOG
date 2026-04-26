


package com.pxczxn.blog.community.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.community.dto.CommunityAuthResponse;
import com.pxczxn.blog.community.dto.CommunityLoginRequest;
import com.pxczxn.blog.community.dto.CommunityRegisterRequest;
import com.pxczxn.blog.community.service.CommunityAuthService;
import com.pxczxn.blog.community.util.CommunityTokenResolver;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityAuthController {

    private final CommunityAuthService communityAuthService;

    
    @Value("${app.security.cookie-secure:true}")
    private boolean cookieSecure;

    







    @PostMapping("/auth/register")
    public Result<CommunityAuthResponse> register(@Valid @RequestBody CommunityRegisterRequest request,
                                                  HttpServletRequest httpRequest,
                                                  HttpServletResponse httpResponse) {
        CommunityAuthResponse response = communityAuthService.register(request, httpRequest);
        writeJwtCookie(httpResponse, response.getToken(), 86400);
        return Result.success(response);
    }

    







    @PostMapping("/auth/login")
    public Result<CommunityAuthResponse> login(@Valid @RequestBody CommunityLoginRequest request,
                                               HttpServletRequest httpRequest,
                                               HttpServletResponse httpResponse) {
        CommunityAuthResponse response = communityAuthService.login(request, httpRequest);
        writeJwtCookie(httpResponse, response.getToken(), 86400);
        return Result.success(response);
    }

    






    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = CommunityTokenResolver.resolve(request);
        if (token != null) {
            communityAuthService.logout(token);
        }
        writeJwtCookie(response, "", 0);
        return Result.success("退出成功", null);
    }

    






    private void writeJwtCookie(HttpServletResponse response, String token, int maxAge) {
        Cookie cookie = new Cookie("community_jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }
}
