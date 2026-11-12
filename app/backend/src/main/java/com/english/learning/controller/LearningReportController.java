package com.english.learning.controller;

import com.english.learning.common.result.Result;
import com.english.learning.service.LearningReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u5b66\u4e60\u62a5\u544a")
@RestController
@RequestMapping(value={"/api/learning/report"})
public class LearningReportController {
    private final LearningReportService learningReportService;

    @Operation(summary="\u751f\u6210\u5468\u62a5")
    @GetMapping(value={"/weekly"})
    public Result<Map<String, Object>> generateWeeklyReport(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.learningReportService.generateWeeklyReport(userId));
    }

    @Operation(summary="\u751f\u6210\u6708\u62a5")
    @GetMapping(value={"/monthly"})
    public Result<Map<String, Object>> generateMonthlyReport(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.learningReportService.generateMonthlyReport(userId));
    }

    @Operation(summary="\u751f\u6210\u81ea\u5b9a\u4e49\u65f6\u95f4\u8303\u56f4\u62a5\u544a")
    @GetMapping(value={"/custom"})
    public Result<Map<String, Object>> generateCustomReport(Authentication authentication, @RequestParam String startTime, @RequestParam String endTime) {
        Long userId = (Long)authentication.getDetails();
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        return Result.success(this.learningReportService.generateLearningReport(userId, start, end));
    }

    public LearningReportController(LearningReportService learningReportService) {
        this.learningReportService = learningReportService;
    }
}

