package com.english.learning.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class StudyTaskRequest {
    private Long planId;
    private String title;
    private String description;
    private String priority;
    private String status;
    private Integer progress;
    private LocalDate deadline;
}
