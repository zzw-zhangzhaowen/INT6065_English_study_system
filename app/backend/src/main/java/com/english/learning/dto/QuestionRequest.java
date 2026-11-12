package com.english.learning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class QuestionRequest {
    @NotBlank(message="\u9898\u578b\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u9898\u578b\u4e0d\u80fd\u4e3a\u7a7a") String questionType;
    @NotNull(message="\u5206\u7c7b\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u5206\u7c7b\u4e0d\u80fd\u4e3a\u7a7a") Long categoryId;
    @NotBlank(message="\u9898\u76ee\u6807\u9898\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u9898\u76ee\u6807\u9898\u4e0d\u80fd\u4e3a\u7a7a") String title;
    private String content;
    private Long passageId;
    private String options;
    @NotBlank(message="\u7b54\u6848\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u7b54\u6848\u4e0d\u80fd\u4e3a\u7a7a") String answer;
    private String analysis;
    private Integer difficulty;
    private BigDecimal score;
    private String audioUrl;
    private String videoUrl;
    private String imageUrl;
    private String keywords;
}
