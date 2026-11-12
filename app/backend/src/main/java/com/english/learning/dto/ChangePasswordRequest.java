package com.english.learning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message="\u65e7\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u65e7\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a") String oldPassword;
    @NotBlank(message="\u65b0\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u65b0\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a") String newPassword;
}
