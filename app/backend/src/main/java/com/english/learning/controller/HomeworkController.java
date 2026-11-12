package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.dto.HomeworkGradeRequest;
import com.english.learning.dto.HomeworkReturnRequest;
import com.english.learning.dto.HomeworkSubmitRequest;
import com.english.learning.entity.Homework;
import com.english.learning.entity.HomeworkSubmission;
import com.english.learning.service.HomeworkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
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

@Tag(name="\u4f5c\u4e1a\u7ba1\u7406")
@RestController
@RequestMapping(value={"/api/homework"})
public class HomeworkController {
    private final HomeworkService homeworkService;

    @Operation(summary="\u521b\u5efa\u4f5c\u4e1a")
    @PostMapping
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> createHomework(Authentication authentication, @RequestBody Homework homework) {
        Long teacherId = (Long)authentication.getDetails();
        this.homeworkService.createHomework(homework, teacherId);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u4f5c\u4e1a")
    @PutMapping(value={"/{id}"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> updateHomework(Authentication authentication, @PathVariable Long id, @RequestBody Homework homework) {
        Long teacherId = (Long)authentication.getDetails();
        homework.setId(id);
        this.homeworkService.updateHomework(homework, teacherId);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u4f5c\u4e1a")
    @DeleteMapping(value={"/{id}"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteHomework(Authentication authentication, @PathVariable Long id) {
        Long teacherId = (Long)authentication.getDetails();
        this.homeworkService.deleteHomework(id, teacherId);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u6559\u5e08\u53d1\u5e03\u7684\u4f5c\u4e1a\u5217\u8868")
    @GetMapping(value={"/teacher/list"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Page<Homework>> getTeacherHomeworks(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String keyword) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.homeworkService.getTeacherHomeworks(teacherId, pageNum, pageSize, keyword));
    }

    @Operation(summary="\u83b7\u53d6\u4f5c\u4e1a\u63d0\u4ea4\u5217\u8868")
    @GetMapping(value={"/{id}/submissions"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Page<HomeworkSubmission>> getHomeworkSubmissions(Authentication authentication, @PathVariable Long id, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String status) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.homeworkService.getHomeworkSubmissions(id, teacherId, pageNum, pageSize, status));
    }

    @Operation(summary="\u6279\u6539\u4f5c\u4e1a")
    @PostMapping(value={"/submission/{submissionId}/grade"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> gradeSubmission(Authentication authentication, @PathVariable Long submissionId, @Valid @RequestBody HomeworkGradeRequest request) {
        Long teacherId = (Long)authentication.getDetails();
        this.homeworkService.gradeSubmission(submissionId, request.getScore(), request.getFeedback(), teacherId);
        return Result.success();
    }

    @Operation(summary="\u9000\u56de\u4f5c\u4e1a")
    @PostMapping(value={"/submission/{submissionId}/return"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> returnSubmission(Authentication authentication, @PathVariable Long submissionId, @RequestBody HomeworkReturnRequest request) {
        Long teacherId = (Long)authentication.getDetails();
        this.homeworkService.returnSubmission(submissionId, request.getFeedback(), teacherId);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u5f85\u6279\u6539\u4f5c\u4e1a\u6570\u91cf")
    @GetMapping(value={"/teacher/pending-count"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Map<String, Object>> getPendingGradeCount(Authentication authentication) {
        Long teacherId = (Long)authentication.getDetails();
        long count = this.homeworkService.getPendingGradeCount(teacherId);
        return Result.success(Map.of("count", count));
    }

    @Operation(summary="\u83b7\u53d6\u5f85\u6279\u6539\u4f5c\u4e1a\u5217\u8868")
    @GetMapping(value={"/pending"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Page<Map<String, Object>>> getPendingHomework(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.homeworkService.getPendingHomeworkList(teacherId, pageNum, pageSize));
    }

    @Operation(summary="\u83b7\u53d6\u4f5c\u4e1a\u5217\u8868\uff08\u5b66\u751f\uff09")
    @GetMapping(value={"/student/list"})
    public Result<Page<Homework>> getStudentHomeworks(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String level, @RequestParam(required=false) String status, @RequestParam(required=false) String keyword) {
        Long studentId = (Long)authentication.getDetails();
        return Result.success(this.homeworkService.getStudentHomeworks(studentId, pageNum, pageSize, level, status, keyword));
    }

    @Operation(summary="\u63d0\u4ea4\u4f5c\u4e1a")
    @PostMapping(value={"/{id}/submit"})
    public Result<Void> submitHomework(Authentication authentication, @PathVariable Long id, @RequestBody HomeworkSubmitRequest request) {
        Long studentId = (Long)authentication.getDetails();
        this.homeworkService.submitHomework(id, request.getContent(), request.getAttachmentUrl(), studentId);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u5b66\u751f\u7684\u63d0\u4ea4\u8bb0\u5f55")
    @GetMapping(value={"/student/submissions"})
    public Result<List<HomeworkSubmission>> getStudentSubmissions(Authentication authentication) {
        Long studentId = (Long)authentication.getDetails();
        return Result.success(this.homeworkService.getStudentSubmissions(studentId));
    }

    @Operation(summary="\u83b7\u53d6\u5b66\u751f\u67d0\u4e2a\u4f5c\u4e1a\u7684\u63d0\u4ea4\u8be6\u60c5")
    @GetMapping(value={"/{id}/my-submission"})
    public Result<HomeworkSubmission> getMySubmission(Authentication authentication, @PathVariable Long id) {
        Long studentId = (Long)authentication.getDetails();
        return Result.success(this.homeworkService.getStudentSubmission(id, studentId));
    }

    @Operation(summary="\u83b7\u53d6\u4f5c\u4e1a\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<Homework> getHomeworkById(@PathVariable Long id) {
        return Result.success(this.homeworkService.getHomeworkById(id));
    }

    @Operation(summary="\u83b7\u53d6\u4f5c\u4e1a\u7edf\u8ba1")
    @GetMapping(value={"/{id}/stats"})
    public Result<Map<String, Object>> getHomeworkStats(@PathVariable Long id) {
        return Result.success(this.homeworkService.getHomeworkStats(id));
    }

    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }
}

