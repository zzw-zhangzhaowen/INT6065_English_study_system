package com.english.learning.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ExamRequest {
    @NotBlank(message="\u8003\u8bd5\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8003\u8bd5\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") String examName;
    @NotBlank(message="\u8003\u8bd5\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8003\u8bd5\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a") String examType;
    private String level;
    private String description;
    private BigDecimal totalScore;
    private BigDecimal passScore;
    @NotNull(message="\u8003\u8bd5\u65f6\u957f\u4e0d\u80fd\u4e3a\u7a7a")
    @Min(value=10L, message="\u8003\u8bd5\u65f6\u957f\u4e0d\u80fd\u5c11\u4e8e10\u5206\u949f")
    private @NotNull(message="\u8003\u8bd5\u65f6\u957f\u4e0d\u80fd\u4e3a\u7a7a") @Min(value=10L, message="\u8003\u8bd5\u65f6\u957f\u4e0d\u80fd\u5c11\u4e8e10\u5206\u949f") Integer duration;
    @NotNull(message="\u5f00\u59cb\u65f6\u95f4\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u5f00\u59cb\u65f6\u95f4\u4e0d\u80fd\u4e3a\u7a7a") LocalDateTime startTime;
    private Integer maxAttempts;
    private Integer enableAntiCheat;
    private Integer randomQuestion;
    private Integer randomOption;
    private Integer showResult;
}
