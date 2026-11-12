package com.english.learning.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ClassroomRequest {
    @NotBlank(message="\u8bfe\u5802\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8bfe\u5802\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") String classroomName;
    private String classroomType;
    private String description;
    private String coverImage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Min(value=1L, message="\u6700\u5927\u4eba\u6570\u4e0d\u80fd\u5c0f\u4e8e1")
    private @Min(value=1L, message="\u6700\u5927\u4eba\u6570\u4e0d\u80fd\u5c0f\u4e8e1") Integer maxStudents;
}
