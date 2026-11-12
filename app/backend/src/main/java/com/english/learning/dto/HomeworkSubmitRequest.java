package com.english.learning.dto;

import lombok.Data;

@Data
public class HomeworkSubmitRequest {
    private String content;
    private String attachmentUrl;
}
