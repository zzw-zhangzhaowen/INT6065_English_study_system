package com.english.learning.controller;

import com.english.learning.common.result.Result;
import com.english.learning.entity.AiChatRecord;
import com.english.learning.entity.PronunciationEvaluation;
import com.english.learning.entity.WritingCorrection;
import com.english.learning.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="AI\u667a\u80fd\u529f\u80fd")
@RestController
@RequestMapping(value={"/api/ai"})
public class AiController {
    private final AIService aiService;

    @Operation(summary="AI\u667a\u80fd\u5bf9\u8bdd")
    @PostMapping(value={"/chat"})
    public Result<Map<String, Object>> chat(Authentication authentication, @RequestBody Map<String, String> body) {
        Long userId = (Long)authentication.getDetails();
        String question = body.get("question");
        if (question == null || question.isEmpty()) {
            question = body.get("message");
        }
        String sessionId = body.get("sessionId");
        return Result.success(this.aiService.chat(question, userId, sessionId));
    }

    @Operation(summary="\u83b7\u53d6\u5bf9\u8bdd\u5386\u53f2")
    @GetMapping(value={"/chat/history"})
    public Result<List<AiChatRecord>> getChatHistory(Authentication authentication, @RequestParam(required=false) String sessionId) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.getChatHistory(userId, sessionId));
    }

    @Operation(summary="\u83b7\u53d6\u4f1a\u8bdd\u5217\u8868")
    @GetMapping(value={"/chat/sessions"})
    public Result<List<Map<String, Object>>> getSessionList(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.getSessionList(userId));
    }

    @Operation(summary="AI\u5199\u4f5c\u6279\u6539")
    @PostMapping(value={"/writing/correct"})
    public Result<Map<String, Object>> correctWriting(Authentication authentication, @RequestBody Map<String, String> body) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.correctWriting(body.get("content"), body.get("title"), body.get("level"), userId));
    }

    @Operation(summary="\u83b7\u53d6\u5199\u4f5c\u6279\u6539\u5386\u53f2")
    @GetMapping(value={"/writing/history"})
    public Result<List<WritingCorrection>> getWritingHistory(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.getWritingHistory(userId));
    }

    @Operation(summary="AI\u53d1\u97f3\u8bc4\u4f30")
    @PostMapping(value={"/pronunciation/evaluate"})
    public Result<Map<String, Object>> evaluatePronunciation(Authentication authentication, @RequestBody Map<String, String> body) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.evaluatePronunciation(body.get("audioUrl"), body.get("referenceText"), userId));
    }

    @Operation(summary="\u83b7\u53d6\u53d1\u97f3\u8bc4\u4f30\u5386\u53f2")
    @GetMapping(value={"/pronunciation/history"})
    public Result<List<PronunciationEvaluation>> getPronunciationHistory(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.getPronunciationHistory(userId));
    }

    @Operation(summary="\u8bed\u6cd5\u68c0\u67e5")
    @PostMapping(value={"/grammar/check"})
    public Result<Map<String, Object>> checkGrammar(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        return Result.success(this.aiService.checkGrammar(text));
    }

    @Operation(summary="\u5b66\u4e60\u8def\u5f84\u63a8\u8350")
    @GetMapping(value={"/learning-path/recommend"})
    public Result<Map<String, Object>> recommendLearningPath(Authentication authentication, @RequestParam(required=false) String currentLevel, @RequestParam(required=false) String targetLevel) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.recommendLearningPath(userId, currentLevel, targetLevel));
    }

    @Operation(summary="\u6e05\u7a7a\u5bf9\u8bdd\u5386\u53f2")
    @DeleteMapping(value={"/chat/history"})
    public Result<Void> clearChatHistory(Authentication authentication, @RequestParam(required=false) String sessionId) {
        Long userId = (Long)authentication.getDetails();
        this.aiService.clearChatHistory(userId, sessionId);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u4f5c\u6587\u6279\u6539\u8be6\u60c5")
    @GetMapping(value={"/writing/{id}"})
    public Result<WritingCorrection> getWritingDetail(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.getWritingDetail(id, userId));
    }

    @Operation(summary="\u83b7\u53d6\u53d1\u97f3\u8bc4\u4f30\u8be6\u60c5")
    @GetMapping(value={"/pronunciation/{id}"})
    public Result<PronunciationEvaluation> getPronunciationDetail(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.getPronunciationDetail(id, userId));
    }

    @Operation(summary="\u83b7\u53d6AI\u4f7f\u7528\u7edf\u8ba1")
    @GetMapping(value={"/stats"})
    public Result<Map<String, Object>> getUsageStats(Authentication authentication, @RequestParam(required=false) Integer days) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.getUsageStats(userId, days));
    }

    @Operation(summary="\u83b7\u53d6\u6700\u8fd1\u4f7f\u7528\u8bb0\u5f55")
    @GetMapping(value={"/recent"})
    public Result<List<Map<String, Object>>> getRecentUsage(Authentication authentication, @RequestParam(defaultValue="5") Integer limit) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.getRecentUsage(userId, limit));
    }

    @Operation(summary="\u521b\u5efa\u65b0\u4f1a\u8bdd")
    @PostMapping(value={"/chat/sessions"})
    public Result<Map<String, Object>> createSession(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.aiService.createSession(userId));
    }

    @Operation(summary="\u5220\u9664\u4f1a\u8bdd")
    @DeleteMapping(value={"/chat/sessions/{sessionId}"})
    public Result<Void> deleteSession(Authentication authentication, @PathVariable String sessionId) {
        Long userId = (Long)authentication.getDetails();
        this.aiService.deleteSession(userId, sessionId);
        return Result.success();
    }

    public AiController(AIService aiService) {
        this.aiService = aiService;
    }
}

