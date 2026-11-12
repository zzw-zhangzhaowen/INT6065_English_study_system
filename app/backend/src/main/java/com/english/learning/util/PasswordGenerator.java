package com.english.learning.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String encodedPassword = encoder.encode(password);
        System.out.println("\u539f\u59cb\u5bc6\u7801: " + password);
        System.out.println("BCrypt\u54c8\u5e0c: " + encodedPassword);
        System.out.println();
        System.out.println("SQL\u66f4\u65b0\u8bed\u53e5:");
        System.out.println("UPDATE sys_user SET password = '" + encodedPassword + "' WHERE username = 'student01';");
    }
}

