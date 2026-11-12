package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u6559\u5e08\u7ba1\u7406")
@RestController
@RequestMapping(value={"/api/teacher"})
@PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
public class TeacherController {
    private final TeacherService teacherService;

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u5b66\u751f\u5217\u8868")
    @GetMapping(value={"/student/page"})
    public Result<Page<Map<String, Object>>> getStudentPage(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String keyword, @RequestParam(required=false) Long classId) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.teacherService.getStudentPage(teacherId, pageNum, pageSize, keyword, classId));
    }

    @Operation(summary="\u83b7\u53d6\u5b66\u751f\u8be6\u60c5")
    @GetMapping(value={"/student/{id}"})
    public Result<Map<String, Object>> getStudentDetail(Authentication authentication, @PathVariable Long id) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.teacherService.getStudentDetail(teacherId, id));
    }

    @Operation(summary="\u83b7\u53d6\u5b66\u4e60\u9884\u8b66\u5b66\u751f")
    @GetMapping(value={"/student/alerts"})
    public Result<List<Map<String, Object>>> getLearningAlerts(Authentication authentication, @RequestParam(required=false) Integer days) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.teacherService.getLearningAlerts(teacherId, days != null ? days : 7));
    }

    @Operation(summary="\u83b7\u53d6\u6210\u7ee9\u8d8b\u52bf\u6570\u636e")
    @GetMapping(value={"/score/trend"})
    public Result<Map<String, Object>> getScoreTrend(Authentication authentication, @RequestParam(required=false) Long classId, @RequestParam(required=false) Integer days) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.teacherService.getScoreTrend(teacherId, classId, days != null ? days : 30));
    }

    @Operation(summary="\u83b7\u53d6\u73ed\u7ea7\u5217\u8868")
    @GetMapping(value={"/class/list"})
    public Result<List<Map<String, Object>>> getClassList(Authentication authentication) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.teacherService.getClassList(teacherId));
    }

    @Operation(summary="\u83b7\u53d6\u5b66\u751f\u5b66\u4e60\u62a5\u544a")
    @GetMapping(value={"/student/{id}/report"})
    public Result<Map<String, Object>> getStudentReport(Authentication authentication, @PathVariable Long id) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.teacherService.getStudentReport(teacherId, id));
    }

    @Operation(summary="\u83b7\u53d6\u73ed\u7ea7\u7edf\u8ba1")
    @GetMapping(value={"/class/{classId}/stats"})
    public Result<Map<String, Object>> getClassStats(Authentication authentication, @PathVariable Long classId) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.teacherService.getClassStats(teacherId, classId));
    }

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }
}

