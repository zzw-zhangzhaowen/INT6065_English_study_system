package com.english.learning.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class StudyPlanRequest {
    private String planName;
    private String targetLevel;
    private BigDecimal targetScore;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer dailyDuration;
    private Integer dailyQuestions;
}
