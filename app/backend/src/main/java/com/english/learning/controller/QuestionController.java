package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.dto.QuestionRequest;
import com.english.learning.entity.Question;
import com.english.learning.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

@Tag(name="\u9898\u5e93\u7ba1\u7406")
@RestController
@RequestMapping(value={"/api/question"})
public class QuestionController {
    private final QuestionService questionService;

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u9898\u76ee")
    @GetMapping(value={"/page"})
    public Result<Page<Question>> getQuestionPage(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) Long categoryId, @RequestParam(required=false) String questionType, @RequestParam(required=false) Integer difficulty, @RequestParam(required=false) String keyword) {
        return Result.success(this.questionService.getQuestionPage(pageNum, pageSize, categoryId, questionType, difficulty, keyword));
    }

    @Operation(summary="\u83b7\u53d6\u9898\u76ee\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<Question> getQuestionById(@PathVariable Long id) {
        return Result.success(this.questionService.getQuestionById(id));
    }

    @Operation(summary="\u521b\u5efa\u9898\u76ee")
    @PostMapping
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> createQuestion(@Valid @RequestBody QuestionRequest request, Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        this.questionService.createQuestion(request, userId);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u9898\u76ee")
    @PutMapping(value={"/{id}"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
        this.questionService.updateQuestion(id, request);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u9898\u76ee")
    @DeleteMapping(value={"/{id}"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        this.questionService.deleteQuestion(id);
        return Result.success();
    }

    @Operation(summary="\u6279\u91cf\u5bfc\u5165\u9898\u76ee")
    @PostMapping(value={"/import"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Map<String, Object>> importQuestions(@RequestParam(value="file") MultipartFile file, @RequestParam(required=false) Long categoryId) {
        int count = this.questionService.importQuestions(file, categoryId);
        return Result.success(Map.of("importedCount", count, "message", "\u6210\u529f\u5bfc\u5165 " + count + " \u9053\u9898\u76ee"));
    }

    @Operation(summary="\u5bfc\u51fa\u9898\u76ee")
    @GetMapping(value={"/export"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> exportQuestions(@RequestParam(required=false) Long categoryId, @RequestParam(required=false) String questionType, @RequestParam(required=false) Integer difficulty) {
        byte[] data = this.questionService.exportQuestions(categoryId, questionType, difficulty);
        String filename = URLEncoder.encode("\u9898\u76ee\u5217\u8868.xlsx", StandardCharsets.UTF_8);
        return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=\"" + filename + "\"").contentType(MediaType.APPLICATION_OCTET_STREAM).body(data);
    }

    @Operation(summary="\u4e0b\u8f7d\u5bfc\u5165\u6a21\u677f")
    @GetMapping(value={"/template"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] data = this.questionService.downloadTemplate();
        String filename = URLEncoder.encode("\u9898\u76ee\u5bfc\u5165\u6a21\u677f.xlsx", StandardCharsets.UTF_8);
        return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=\"" + filename + "\"").contentType(MediaType.APPLICATION_OCTET_STREAM).body(data);
    }

    @Operation(summary="\u968f\u673a\u83b7\u53d6\u7ec3\u4e60\u9898")
    @GetMapping(value={"/random"})
    public Result<List<Question>> getRandomQuestions(@RequestParam(required=false) Long categoryId, @RequestParam(required=false) String questionType, @RequestParam(required=false) Integer difficulty, @RequestParam(defaultValue="10") @Max(value=100L, message="\u5355\u6b21\u6700\u591a\u83b7\u53d6100\u9053\u9898") @Max(value=100L, message="\u5355\u6b21\u6700\u591a\u83b7\u53d6100\u9053\u9898") int count) {
        return Result.success(this.questionService.getRandomQuestions(categoryId, questionType, difficulty, count));
    }

    @Operation(summary="\u83b7\u53d6\u9898\u5e93\u7edf\u8ba1")
    @GetMapping(value={"/stats"})
    public Result<Map<String, Object>> getQuestionStats() {
        return Result.success(this.questionService.getQuestionStats());
    }

    @Operation(summary="\u6279\u91cf\u5220\u9664\u9898\u76ee")
    @DeleteMapping(value={"/batch"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> batchDeleteQuestions(@RequestBody @Size(max=500, message="\u5355\u6b21\u6700\u591a\u5220\u9664500\u6761") @Size(max=500, message="\u5355\u6b21\u6700\u591a\u5220\u9664500\u6761") List<Long> ids) {
        this.questionService.batchDeleteQuestions(ids);
        return Result.success();
    }

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
}

