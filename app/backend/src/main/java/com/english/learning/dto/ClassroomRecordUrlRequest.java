package com.english.learning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClassroomRecordUrlRequest {
    @NotBlank(message="\u5f55\u64ad\u5730\u5740\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5f55\u64ad\u5730\u5740\u4e0d\u80fd\u4e3a\u7a7a") String recordUrl;
}
