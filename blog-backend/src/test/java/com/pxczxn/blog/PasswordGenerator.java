/*
 * 功能：项目入口和基础文件。
 */
package com.pxczxn.blog;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {
    @Test
    public void generatePassword() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "Pxczxnpxczxn";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password: " + encodedPassword);
    }
}

