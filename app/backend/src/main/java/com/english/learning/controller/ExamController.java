package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.common.result.Result;
import com.english.learning.dto.ExamRequest;
import com.english.learning.dto.ExamSubmitRequest;
import com.english.learning.entity.Exam;
import com.english.learning.entity.ExamRecord;
import com.english.learning.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
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

@Tag(name="\u8003\u8bd5\u7ba1\u7406")
@RestController
@RequestMapping(value={"/api/exam"})
public class ExamController {
    private final ExamService examService;

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u8003\u8bd5")
    @GetMapping(value={"/page"})
    public Result<Page<Exam>> getExamPage(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String examType, @RequestParam(required=false) String level, @RequestParam(required=false) String keyword, @RequestParam(required=false) String status, @RequestParam(required=false, defaultValue="false") boolean forTeacher, Authentication authentication) {
        Long teacherId = null;
        if (forTeacher && authentication != null) {
            teacherId = (Long)authentication.getDetails();
        }
        return Result.success(this.examService.getExamPage(pageNum, pageSize, examType, level, keyword, status, forTeacher, teacherId));
    }

    @Operation(summary="\u83b7\u53d6\u8003\u8bd5\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<Map<String, Object>> getExamDetail(@PathVariable Long id) {
        return Result.success(this.examService.getExamDetail(id));
    }

    @Operation(summary="\u5f00\u59cb\u8003\u8bd5")
    @PostMapping(value={"/start"})
    public Result<Map<String, Object>> startExam(Authentication authentication, @RequestParam Long examId) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.examService.startExam(examId, userId));
    }

    @Operation(summary="\u63d0\u4ea4\u8003\u8bd5\uff08\u5b8c\u6574\u7248\uff09")
    @PostMapping(value={"/submit"})
    public Result<Map<String, Object>> submitExam(@RequestBody ExamSubmitRequest request, Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        ExamRecord record = this.examService.getExamRecordById(request.getRecordId());
        if (!record.getStudentId().equals(userId)) {
            throw new BusinessException("\u65e0\u6743\u64cd\u4f5c\u6b64\u8003\u8bd5\u8bb0\u5f55");
        }
        return Result.success(this.examService.submitExam(request.getRecordId(), request));
    }

    @Operation(summary="\u63d0\u4ea4\u8003\u8bd5\uff08\u7b80\u5316\u7248\uff09")
    @PostMapping(value={"/submit/simple"})
    @PreAuthorize(value="hasRole('ADMIN')")
    public Result<Void> submitExamSimple(@RequestParam Long recordId, @RequestParam BigDecimal obtainedScore, @RequestParam Integer correctCount, @RequestParam Integer wrongCount) {
        this.examService.submitExam(recordId, obtainedScore, correctCount, wrongCount);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u8003\u8bd5\u8bb0\u5f55\u5217\u8868")
    @GetMapping(value={"/record/page"})
    public Result<Page<Map<String, Object>>> getExamRecordPage(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String status) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.examService.getExamRecordPageWithExamName(pageNum, pageSize, userId, status));
    }

    @Operation(summary="\u83b7\u53d6\u8003\u8bd5\u8bb0\u5f55\u8be6\u60c5")
    @GetMapping(value={"/record/{id}"})
    public Result<Map<String, Object>> getExamRecordDetail(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        ExamRecord record = this.examService.getExamRecordById(id);
        if (!record.getStudentId().equals(userId)) {
            throw new BusinessException("\u65e0\u6743\u67e5\u770b\u6b64\u8003\u8bd5\u8bb0\u5f55");
        }
        return Result.success(this.examService.getExamRecordDetail(id));
    }

    @Operation(summary="\u83b7\u53d6\u8003\u8bd5\u9898\u76ee\u5217\u8868")
    @GetMapping(value={"/{examId}/questions"})
    public Result<List<Map<String, Object>>> getExamQuestions(@PathVariable Long examId) {
        return Result.success(this.examService.getExamQuestionList(examId));
    }

    @Operation(summary="\u4fdd\u5b58\u5355\u9898\u7b54\u6848")
    @PostMapping(value={"/record/{recordId}/answer"})
    public Result<Void> saveAnswer(@PathVariable Long recordId, @RequestBody Map<String, Object> body, Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        ExamRecord record = this.examService.getExamRecordById(recordId);
        if (!record.getStudentId().equals(userId)) {
            throw new BusinessException("\u65e0\u6743\u64cd\u4f5c\u6b64\u8003\u8bd5\u8bb0\u5f55");
        }
        Long questionId = Long.valueOf(body.get("questionId").toString());
        String answer = body.get("answer").toString();
        this.examService.saveAnswer(recordId, questionId, answer);
        return Result.success();
    }

    @Operation(summary="\u521b\u5efa\u8003\u8bd5\uff08\u6559\u5e08\uff09")
    @PostMapping
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> createExam(@Valid @RequestBody ExamRequest request, Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        this.examService.createExam(request, userId);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u8003\u8bd5\uff08\u6559\u5e08\uff09")
    @PutMapping(value={"/{id}"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> updateExam(@PathVariable Long id, @Valid @RequestBody ExamRequest request, Authentication authentication) {
        Long operatorId = (Long)authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        this.examService.updateExam(id, request, operatorId, isAdmin);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u8003\u8bd5\uff08\u6559\u5e08\uff09")
    @DeleteMapping(value={"/{id}"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteExam(@PathVariable Long id, Authentication authentication) {
        Long operatorId = (Long)authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        this.examService.deleteExam(id, operatorId, isAdmin);
        return Result.success();
    }

    @Operation(summary="\u53d1\u5e03\u8003\u8bd5\uff08\u6559\u5e08\uff09")
    @PostMapping(value={"/{id}/publish"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> publishExam(@PathVariable Long id, Authentication authentication) {
        Long operatorId = (Long)authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        this.examService.publishExam(id, operatorId, isAdmin);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u8003\u8bd5\u7edf\u8ba1\uff08\u6559\u5e08\uff09")
    @GetMapping(value={"/{id}/stats"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Map<String, Object>> getExamStats(@PathVariable Long id, Authentication authentication) {
        Long operatorId = (Long)authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return Result.success(this.examService.getExamStats(id, operatorId, isAdmin));
    }

    @Operation(summary="\u83b7\u53d6\u8fdb\u884c\u4e2d\u7684\u8003\u8bd5")
    @GetMapping(value={"/ongoing"})
    public Result<List<Map<String, Object>>> getOngoingExams(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.examService.getOngoingExams(userId));
    }

    @Operation(summary="\u83b7\u53d6\u6559\u5e08\u521b\u5efa\u7684\u8003\u8bd5\u5217\u8868")
    @GetMapping(value={"/teacher"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Page<Exam>> getTeacherExams(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.examService.getTeacherExams(teacherId, pageNum, pageSize));
    }

    @Operation(summary="\u83b7\u53d6\u8003\u8bd5\u56de\u987e\u8be6\u60c5")
    @GetMapping(value={"/review/{recordId}"})
    public Result<Map<String, Object>> getExamReview(@PathVariable Long recordId, Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.examService.getExamReview(recordId, userId));
    }

    @Operation(summary="\u6dfb\u52a0\u8003\u8bd5\u9898\u76ee")
    @PostMapping(value={"/{examId}/question"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> addExamQuestion(Authentication authentication, @PathVariable Long examId, @RequestParam Long questionId, @RequestParam(defaultValue="0") Integer questionOrder, @RequestParam(defaultValue="5") BigDecimal questionScore) {
        Long operatorId = (Long)authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        this.examService.addExamQuestion(examId, questionId, questionOrder, questionScore, operatorId, isAdmin);
        return Result.success();
    }

    @Operation(summary="\u6279\u91cf\u6dfb\u52a0\u8003\u8bd5\u9898\u76ee")
    @PostMapping(value={"/{examId}/questions/batch"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> batchAddExamQuestions(Authentication authentication, @PathVariable Long examId, @RequestBody List<Map<String, Object>> questions) {
        Long operatorId = (Long)authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        this.examService.batchAddExamQuestions(examId, questions, operatorId, isAdmin);
        return Result.success();
    }

    @Operation(summary="\u79fb\u9664\u8003\u8bd5\u9898\u76ee")
    @DeleteMapping(value={"/{examId}/question/{questionId}"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> removeExamQuestion(Authentication authentication, @PathVariable Long examId, @PathVariable Long questionId) {
        Long operatorId = (Long)authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        this.examService.removeExamQuestion(examId, questionId, operatorId, isAdmin);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u8003\u8bd5\u5b66\u751f\u6210\u7ee9\u5217\u8868")
    @GetMapping(value={"/{examId}/records"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<Map<String, Object>>> getExamStudentRecords(@PathVariable Long examId, Authentication authentication) {
        Long operatorId = (Long)authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return Result.success(this.examService.getExamStudentRecords(examId, operatorId, isAdmin));
    }

    @Operation(summary="\u83b7\u53d6\u8003\u8bd5\u9898\u76ee\u6b63\u786e\u7387\u5206\u6790")
    @GetMapping(value={"/{examId}/question-analysis"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<Map<String, Object>>> getQuestionAnalysis(@PathVariable Long examId, Authentication authentication) {
        Long operatorId = (Long)authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return Result.success(this.examService.getQuestionAnalysis(examId, operatorId, isAdmin));
    }

    @Operation(summary="\u83b7\u53d6\u8003\u8bd5\u76d1\u63a7\u6570\u636e")
    @GetMapping(value={"/{examId}/monitor"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Map<String, Object>> getExamMonitor(@PathVariable Long examId, Authentication authentication) {
        Long operatorId = (Long)authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return Result.success(this.examService.getExamMonitor(examId, operatorId, isAdmin));
    }

    public ExamController(ExamService examService) {
        this.examService = examService;
    }
}

