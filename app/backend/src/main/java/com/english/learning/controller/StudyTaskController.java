package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.dto.StudyTaskRequest;
import com.english.learning.entity.StudyTask;
import com.english.learning.service.StudyTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u5b66\u4e60\u4efb\u52a1", description="\u5b66\u4e60\u4efb\u52a1\u7ba1\u7406\u63a5\u53e3")
@RestController
@RequestMapping(value={"/api/study-task"})
public class StudyTaskController {
    private final StudyTaskService studyTaskService;

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u5b66\u4e60\u4efb\u52a1")
    @GetMapping(value={"/page"})
    public Result<Page<StudyTask>> getTaskPage(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String status) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.studyTaskService.getTaskPage(pageNum, pageSize, userId, status));
    }

    @Operation(summary="\u83b7\u53d6\u4efb\u52a1\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<StudyTask> getTaskDetail(@PathVariable Long id) {
        return Result.success(this.studyTaskService.getTaskDetail(id));
    }

    @Operation(summary="\u521b\u5efa\u5b66\u4e60\u4efb\u52a1")
    @PostMapping
    public Result<Void> createTask(Authentication authentication, @RequestBody StudyTaskRequest request) {
        Long userId = (Long)authentication.getDetails();
        this.studyTaskService.createTask(userId, request);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u5b66\u4e60\u4efb\u52a1")
    @PutMapping(value={"/{id}"})
    public Result<Void> updateTask(Authentication authentication, @PathVariable Long id, @RequestBody StudyTaskRequest request) {
        Long userId = (Long)authentication.getDetails();
        this.studyTaskService.updateTask(id, userId, request);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u5b66\u4e60\u4efb\u52a1")
    @DeleteMapping(value={"/{id}"})
    public Result<Void> deleteTask(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.studyTaskService.deleteTask(id, userId);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u4efb\u52a1\u7edf\u8ba1")
    @GetMapping(value={"/statistics"})
    public Result<Map<String, Object>> getTaskStatistics(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.studyTaskService.getTaskStatistics(userId));
    }

    @Operation(summary="\u5207\u6362\u4efb\u52a1\u5b8c\u6210\u72b6\u6001")
    @PutMapping(value={"/{id}/toggle"})
    public Result<Void> toggleTaskStatus(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.studyTaskService.toggleTaskStatus(id, userId);
        return Result.success();
    }

    @Operation(summary="\u6309\u8ba1\u5212ID\u67e5\u8be2\u4efb\u52a1")
    @GetMapping(value={"/plan/{planId}"})
    public Result<List<StudyTask>> getTasksByPlanId(Authentication authentication, @PathVariable Long planId) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.studyTaskService.getTasksByPlanId(planId, userId));
    }

    public StudyTaskController(StudyTaskService studyTaskService) {
        this.studyTaskService = studyTaskService;
    }
}

