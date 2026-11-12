package com.english.learning.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class HomeworkGradeRequest {
    @NotNull(message="\u5206\u6570\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u5206\u6570\u4e0d\u80fd\u4e3a\u7a7a") BigDecimal score;
    private String feedback;
}
