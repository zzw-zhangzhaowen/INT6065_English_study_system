package com.english.learning.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message="\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(min=3, max=20, message="\u7528\u6237\u540d\u957f\u5ea6\u4e3a3-20\u4e2a\u5b57\u7b26")
    private @NotBlank(message="\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a") @Size(min=3, max=20, message="\u7528\u6237\u540d\u957f\u5ea6\u4e3a3-20\u4e2a\u5b57\u7b26") String username;
    @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(min=6, max=20, message="\u5bc6\u7801\u957f\u5ea6\u4e3a6-20\u4e2a\u5b57\u7b26")
    private @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a") @Size(min=6, max=20, message="\u5bc6\u7801\u957f\u5ea6\u4e3a6-20\u4e2a\u5b57\u7b26") String password;
    @NotBlank(message="\u90ae\u7bb1\u4e0d\u80fd\u4e3a\u7a7a")
    @Email(message="\u90ae\u7bb1\u683c\u5f0f\u4e0d\u6b63\u786e")
    private @NotBlank(message="\u90ae\u7bb1\u4e0d\u80fd\u4e3a\u7a7a") @Email(message="\u90ae\u7bb1\u683c\u5f0f\u4e0d\u6b63\u786e") String email;
    @Pattern(regexp="^1[3-9]\\d{9}$", message="\u624b\u673a\u53f7\u683c\u5f0f\u4e0d\u6b63\u786e")
    private @Pattern(regexp="^1[3-9]\\d{9}$", message="\u624b\u673a\u53f7\u683c\u5f0f\u4e0d\u6b63\u786e") String phone;
    private String realName;
    @NotBlank(message="\u89d2\u8272\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u89d2\u8272\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a") String roleType;
}
