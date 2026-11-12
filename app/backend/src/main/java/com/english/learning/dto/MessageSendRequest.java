package com.english.learning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageSendRequest {
    @NotBlank(message="\u6807\u9898\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u6807\u9898\u4e0d\u80fd\u4e3a\u7a7a") String title;
    @NotBlank(message="\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a") String content;
    private String type;
}
