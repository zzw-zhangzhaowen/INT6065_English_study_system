package com.english.learning.dto;

import java.util.List;
import lombok.Data;

@Data
public class ExamSubmitRequest {
    private Long recordId;
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private Long questionId;
        private String answer;
        private Integer answerTime;
    }
}
