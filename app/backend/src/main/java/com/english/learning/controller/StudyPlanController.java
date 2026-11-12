package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.dto.StudyPlanRequest;
import com.english.learning.entity.StudyPlan;
import com.english.learning.service.StudyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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

@Tag(name="\u5b66\u4e60\u8ba1\u5212", description="\u5b66\u4e60\u8ba1\u5212\u7ba1\u7406\u63a5\u53e3")
@RestController
@RequestMapping(value={"/api/study-plan"})
public class StudyPlanController {
    private final StudyPlanService studyPlanService;

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u5b66\u4e60\u8ba1\u5212")
    @GetMapping(value={"/page"})
    public Result<Page<StudyPlan>> getPlanPage(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String status) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.studyPlanService.getPlanPage(pageNum, pageSize, userId, status));
    }

    @Operation(summary="\u83b7\u53d6\u8ba1\u5212\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<StudyPlan> getPlanDetail(@PathVariable Long id) {
        return Result.success(this.studyPlanService.getPlanDetail(id));
    }

    @Operation(summary="\u521b\u5efa\u5b66\u4e60\u8ba1\u5212")
    @PostMapping
    public Result<Void> createPlan(Authentication authentication, @RequestBody StudyPlanRequest request) {
        Long userId = (Long)authentication.getDetails();
        this.studyPlanService.createPlan(userId, request);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u5b66\u4e60\u8ba1\u5212")
    @PutMapping(value={"/{id}"})
    public Result<Void> updatePlan(Authentication authentication, @PathVariable Long id, @RequestBody StudyPlanRequest request) {
        Long userId = (Long)authentication.getDetails();
        this.studyPlanService.updatePlan(id, userId, request);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u8ba1\u5212\u8fdb\u5ea6")
    @PutMapping(value={"/{id}/progress"})
    public Result<Void> updateProgress(Authentication authentication, @PathVariable Long id, @RequestParam Integer progress) {
        Long userId = (Long)authentication.getDetails();
        this.studyPlanService.updateProgress(id, userId, progress);
        return Result.success();
    }

    @Operation(summary="\u6682\u505c/\u6062\u590d\u8ba1\u5212")
    @PutMapping(value={"/{id}/toggle"})
    public Result<Void> togglePlanStatus(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.studyPlanService.togglePlanStatus(id, userId);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u5b66\u4e60\u8ba1\u5212")
    @DeleteMapping(value={"/{id}"})
    public Result<Void> deletePlan(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.studyPlanService.deletePlan(id, userId);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u4eca\u65e5\u5b66\u4e60\u4efb\u52a1")
    @GetMapping(value={"/today"})
    public Result<List<StudyPlan>> getTodayTasks(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.studyPlanService.getTodayTasks(userId));
    }

    public StudyPlanController(StudyPlanService studyPlanService) {
        this.studyPlanService = studyPlanService;
    }
}

