package com.english.learning.controller;

import com.english.learning.common.exception.BusinessException;
import com.english.learning.common.result.Result;
import com.english.learning.entity.ClassroomAnswer;
import com.english.learning.entity.ClassroomQuestion;
import com.english.learning.service.ClassroomPollService;
import com.english.learning.service.ClassroomQaService;
import com.english.learning.service.ClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u8bfe\u5802\u4e92\u52a8")
@RestController
@RequestMapping(value={"/api/classroom"})
public class ClassroomInteractiveController {
    private final ClassroomQaService qaService;
    private final ClassroomPollService pollService;
    private final ClassroomService classroomService;

    @Operation(summary="\u83b7\u53d6\u8bfe\u5802\u95ee\u9898\u5217\u8868")
    @GetMapping(value={"/{classroomId}/questions"})
    public Result<List<Map<String, Object>>> getQuestions(Authentication authentication, @PathVariable Long classroomId) {
        this.ensureClassroomAccess(authentication, classroomId);
        return Result.success(this.qaService.getQuestions(classroomId));
    }

    @Operation(summary="\u521b\u5efa\u95ee\u9898")
    @PostMapping(value={"/{classroomId}/questions"})
    public Result<Map<String, Object>> createQuestion(Authentication authentication, @PathVariable Long classroomId, @RequestBody Map<String, Object> body) {
        this.ensureClassroomAccess(authentication, classroomId);
        Long userId = (Long)authentication.getDetails();
        String content = (String)body.get("content");
        Boolean isAnonymous = (Boolean)body.get("isAnonymous");
        ClassroomQuestion question = this.qaService.createQuestion(classroomId, userId, content, isAnonymous);
        List<Map<String, Object>> questions = this.qaService.getQuestions(classroomId);
        Map<String, Object> created = questions.stream()
            .filter(q -> q.get("id").equals(question.getId()))
            .findFirst()
            .orElse(null);
        return Result.success(created);
    }

    @Operation(summary="\u83b7\u53d6\u95ee\u9898\u7684\u56de\u7b54\u5217\u8868")
    @GetMapping(value={"/{classroomId}/questions/{questionId}/answers"})
    public Result<List<Map<String, Object>>> getAnswers(Authentication authentication, @PathVariable Long classroomId, @PathVariable Long questionId) {
        this.ensureClassroomAccess(authentication, classroomId);
        return Result.success(this.qaService.getAnswers(classroomId, questionId));
    }

    @Operation(summary="\u56de\u7b54\u95ee\u9898")
    @PostMapping(value={"/{classroomId}/questions/{questionId}/answers"})
    public Result<Map<String, Object>> createAnswer(Authentication authentication, @PathVariable Long classroomId, @PathVariable Long questionId, @RequestBody Map<String, String> body) {
        this.ensureClassroomAccess(authentication, classroomId);
        Long userId = (Long)authentication.getDetails();
        ClassroomAnswer answer = this.qaService.createAnswer(questionId, classroomId, userId, body.get("content"));
        Map<String, Object> result = new HashMap<>();
        result.put("id", answer.getId());
        result.put("questionId", answer.getQuestionId());
        result.put("userId", answer.getUserId());
        result.put("content", answer.getContent());
        result.put("isBest", answer.getIsBest());
        result.put("createTime", answer.getCreateTime().toString());
        return Result.success(result);
    }

    @Operation(summary="\u70b9\u8d5e\u95ee\u9898")
    @PostMapping(value={"/{classroomId}/questions/{questionId}/like"})
    public Result<Integer> likeQuestion(Authentication authentication, @PathVariable Long classroomId, @PathVariable Long questionId) {
        this.ensureClassroomAccess(authentication, classroomId);
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.qaService.likeQuestion(classroomId, questionId, userId));
    }

    @Operation(summary="\u5173\u95ed\u95ee\u9898")
    @PostMapping(value={"/{classroomId}/questions/{questionId}/close"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> closeQuestion(Authentication authentication, @PathVariable Long classroomId, @PathVariable Long questionId) {
        this.ensureTeacherOrAdminAccess(authentication, classroomId);
        Long userId = (Long)authentication.getDetails();
        this.qaService.closeQuestion(classroomId, questionId, userId, this.isAdmin(authentication));
        return Result.success();
    }

    @Operation(summary="\u6807\u8bb0\u6700\u4f73\u56de\u7b54")
    @PostMapping(value={"/{classroomId}/answers/{answerId}/best"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> markBestAnswer(Authentication authentication, @PathVariable Long classroomId, @PathVariable Long answerId) {
        this.ensureTeacherOrAdminAccess(authentication, classroomId);
        Long userId = (Long)authentication.getDetails();
        this.qaService.markBestAnswer(classroomId, answerId, userId, this.isAdmin(authentication));
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u8bfe\u5802\u6295\u7968\u5217\u8868")
    @GetMapping(value={"/{classroomId}/polls"})
    public Result<List<Map<String, Object>>> getPolls(Authentication authentication, @PathVariable Long classroomId) {
        this.ensureClassroomAccess(authentication, classroomId);
        return Result.success(this.pollService.getPolls(classroomId));
    }

    @Operation(summary="\u521b\u5efa\u6295\u7968")
    @PostMapping(value={"/{classroomId}/polls"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Map<String, Object>> createPoll(Authentication authentication, @PathVariable Long classroomId, @RequestBody Map<String, Object> body) {
        this.ensureTeacherOrAdminAccess(authentication, classroomId);
        Long userId = (Long)authentication.getDetails();
        String title = (String)body.get("title");
        String pollType = (String)body.get("pollType");
        Boolean anonymous = (Boolean)body.get("anonymous");
        List<String> options = (List<String>)body.get("options");
        return Result.success(this.pollService.createPoll(classroomId, userId, title, pollType, anonymous, options));
    }

    @Operation(summary="\u83b7\u53d6\u6295\u7968\u8be6\u60c5")
    @GetMapping(value={"/{classroomId}/polls/{pollId}"})
    public Result<Map<String, Object>> getPollDetail(Authentication authentication, @PathVariable Long classroomId, @PathVariable Long pollId) {
        this.ensureClassroomAccess(authentication, classroomId);
        return Result.success(this.pollService.getPollDetail(classroomId, pollId));
    }

    @Operation(summary="\u6295\u7968")
    @PostMapping(value={"/{classroomId}/polls/{pollId}/vote"})
    public Result<Map<String, Object>> vote(Authentication authentication, @PathVariable Long classroomId, @PathVariable Long pollId, @RequestBody Map<String, Object> body) {
        this.ensureClassroomAccess(authentication, classroomId);
        Long userId = (Long)authentication.getDetails();
        List<?> optionIds = (List<?>)body.get("optionIds");
        List<Long> ids = optionIds.stream().map(obj -> ((Number)obj).longValue()).toList();
        return Result.success(this.pollService.vote(classroomId, pollId, userId, ids));
    }

    @Operation(summary="\u5173\u95ed\u6295\u7968")
    @PostMapping(value={"/{classroomId}/polls/{pollId}/close"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Map<String, Object>> closePoll(Authentication authentication, @PathVariable Long classroomId, @PathVariable Long pollId) {
        this.ensureTeacherOrAdminAccess(authentication, classroomId);
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.pollService.closePoll(classroomId, pollId, userId, this.isAdmin(authentication)));
    }

    @Operation(summary="\u68c0\u67e5\u662f\u5426\u5df2\u6295\u7968")
    @GetMapping(value={"/{classroomId}/polls/{pollId}/voted"})
    public Result<Boolean> hasVoted(Authentication authentication, @PathVariable Long classroomId, @PathVariable Long pollId) {
        this.ensureClassroomAccess(authentication, classroomId);
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.pollService.hasVoted(classroomId, pollId, userId));
    }

    private void ensureClassroomAccess(Authentication authentication, Long classroomId) {
        String role;
        Long userId = (Long)authentication.getDetails();
        if (!this.classroomService.hasClassroomAccess(classroomId, userId, role = ((GrantedAuthority)authentication.getAuthorities().iterator().next()).getAuthority())) {
            throw new BusinessException("\u65e0\u6743\u8bbf\u95ee\u6b64\u8bfe\u5802");
        }
    }

    private void ensureTeacherOrAdminAccess(Authentication authentication, Long classroomId) {
        String role;
        Long userId = (Long)authentication.getDetails();
        if (!this.classroomService.hasClassroomAccess(classroomId, userId, role = ((GrantedAuthority)authentication.getAuthorities().iterator().next()).getAuthority())) {
            throw new BusinessException("\u65e0\u6743\u8bbf\u95ee\u6b64\u8bfe\u5802");
        }
        boolean teacherOrAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_TEACHER".equals(a.getAuthority()) || "ROLE_ADMIN".equals(a.getAuthority()));
        if (!teacherOrAdmin) {
            throw new BusinessException("\u65e0\u6743\u6267\u884c\u8be5\u64cd\u4f5c");
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    public ClassroomInteractiveController(ClassroomQaService qaService, ClassroomPollService pollService, ClassroomService classroomService) {
        this.qaService = qaService;
        this.pollService = pollService;
        this.classroomService = classroomService;
    }
}

