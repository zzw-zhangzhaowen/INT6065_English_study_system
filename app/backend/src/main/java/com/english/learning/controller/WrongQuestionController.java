package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.service.WrongQuestionService;
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

@Tag(name="\u9519\u9898\u672c", description="\u9519\u9898\u672c\u7ba1\u7406\u63a5\u53e3")
@RestController
@RequestMapping(value={"/api/wrong-question"})
public class WrongQuestionController {
    private final WrongQuestionService wrongQuestionService;

    @Operation(summary="\u83b7\u53d6\u9519\u9898\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<Map<String, Object>> getWrongQuestionDetail(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.wrongQuestionService.getWrongQuestionDetail(id, userId));
    }

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u9519\u9898")
    @GetMapping(value={"/page"})
    public Result<Page<Map<String, Object>>> getWrongQuestionPage(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) Integer masteryLevel, @RequestParam(required=false) String questionType, @RequestParam(required=false) Integer days, @RequestParam(required=false) String keyword) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.wrongQuestionService.getWrongQuestionPage(pageNum, pageSize, userId, masteryLevel, questionType, days, keyword));
    }

    @Operation(summary="\u6dfb\u52a0\u9519\u9898")
    @PostMapping
    public Result<Void> addWrongQuestion(Authentication authentication, @RequestParam Long questionId) {
        Long userId = (Long)authentication.getDetails();
        this.wrongQuestionService.addWrongQuestion(userId, questionId);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u638c\u63e1\u7a0b\u5ea6")
    @PutMapping(value={"/{id}/mastery"})
    public Result<Void> updateMasteryLevel(Authentication authentication, @PathVariable Long id, @RequestParam Integer masteryLevel) {
        Long userId = (Long)authentication.getDetails();
        this.wrongQuestionService.updateMasteryLevel(id, userId, masteryLevel);
        return Result.success();
    }

    @Operation(summary="\u6dfb\u52a0\u7b14\u8bb0")
    @PutMapping(value={"/{id}/notes"})
    public Result<Void> addNotes(Authentication authentication, @PathVariable Long id, @RequestBody Map<String, String> body) {
        Long userId = (Long)authentication.getDetails();
        this.wrongQuestionService.addNotes(id, userId, body.get("notes"));
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u9519\u9898")
    @DeleteMapping(value={"/{id}"})
    public Result<Void> deleteWrongQuestion(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.wrongQuestionService.deleteWrongQuestion(id, userId);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u9519\u9898\u7edf\u8ba1")
    @GetMapping(value={"/statistics"})
    public Result<Map<String, Object>> getStatistics(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.wrongQuestionService.getStatistics(userId));
    }

    @Operation(summary="\u83b7\u53d6\u9519\u9898\u7ec3\u4e60\u5217\u8868")
    @GetMapping(value={"/practice"})
    public Result<List<Map<String, Object>>> getPracticeQuestions(Authentication authentication, @RequestParam(defaultValue="10") int limit, @RequestParam(required=false) String type) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.wrongQuestionService.getPracticeQuestions(userId, limit, type));
    }

    @Operation(summary="\u83b7\u53d6\u76f8\u4f3c\u9898\u76ee")
    @GetMapping(value={"/{id}/similar"})
    public Result<List<Map<String, Object>>> getSimilarQuestions(Authentication authentication, @PathVariable Long id, @RequestParam(defaultValue="5") int limit) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.wrongQuestionService.getSimilarQuestions(id, userId, limit));
    }

    @Operation(summary="\u8bb0\u5f55\u7ec3\u4e60\u7ed3\u679c")
    @PostMapping(value={"/practice/result"})
    public Result<Void> recordPracticeResult(Authentication authentication, @RequestBody Map<String, Object> body) {
        Long userId = (Long)authentication.getDetails();
        Long questionId = Long.valueOf(body.get("questionId").toString());
        boolean isCorrect = Boolean.parseBoolean(body.get("isCorrect").toString());
        this.wrongQuestionService.recordPracticeResult(userId, questionId, isCorrect);
        return Result.success();
    }

    public WrongQuestionController(WrongQuestionService wrongQuestionService) {
        this.wrongQuestionService = wrongQuestionService;
    }
}

