package com.english.learning.dto;

import lombok.Data;

@Data
public class StudyNoteRequest {
    private String title;
    private String content;
    private String category;
    private String tags;
    private Integer isShared;
}
