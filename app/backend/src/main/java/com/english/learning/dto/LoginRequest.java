package com.english.learning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message="\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a") String username;
    @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a") String password;
    private String captcha;
    private String captchaKey;
}
