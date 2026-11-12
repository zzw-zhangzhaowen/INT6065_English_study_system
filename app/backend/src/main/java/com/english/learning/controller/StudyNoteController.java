package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.dto.StudyNoteRequest;
import com.english.learning.entity.StudyNote;
import com.english.learning.service.StudyNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name="\u5b66\u4e60\u7b14\u8bb0", description="\u5b66\u4e60\u7b14\u8bb0\u7ba1\u7406\u63a5\u53e3")
@RestController
@RequestMapping(value={"/api/study-note"})
public class StudyNoteController {
    private final StudyNoteService studyNoteService;

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u6211\u7684\u7b14\u8bb0")
    @GetMapping(value={"/page"})
    public Result<Page<StudyNote>> getNotePage(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String category) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.studyNoteService.getNotePage(pageNum, pageSize, userId, category));
    }

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u516c\u5f00\u7b14\u8bb0")
    @GetMapping(value={"/shared/page"})
    public Result<Page<StudyNote>> getSharedNotePage(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String category) {
        return Result.success(this.studyNoteService.getSharedNotePage(pageNum, pageSize, category));
    }

    @Operation(summary="\u83b7\u53d6\u7b14\u8bb0\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<StudyNote> getNoteDetail(@PathVariable Long id) {
        return Result.success(this.studyNoteService.getNoteDetail(id));
    }

    @Operation(summary="\u521b\u5efa\u7b14\u8bb0")
    @PostMapping
    public Result<Void> createNote(Authentication authentication, @RequestBody StudyNoteRequest request) {
        Long userId = (Long)authentication.getDetails();
        this.studyNoteService.createNote(userId, request);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u7b14\u8bb0")
    @PutMapping(value={"/{id}"})
    public Result<Void> updateNote(Authentication authentication, @PathVariable Long id, @RequestBody StudyNoteRequest request) {
        Long userId = (Long)authentication.getDetails();
        this.studyNoteService.updateNote(id, userId, request);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u7b14\u8bb0")
    @DeleteMapping(value={"/{id}"})
    public Result<Void> deleteNote(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.studyNoteService.deleteNote(id, userId);
        return Result.success();
    }

    @Operation(summary="\u70b9\u8d5e\u7b14\u8bb0")
    @PostMapping(value={"/{id}/like"})
    public Result<Void> likeNote(@PathVariable Long id) {
        this.studyNoteService.likeNote(id);
        return Result.success();
    }

    public StudyNoteController(StudyNoteService studyNoteService) {
        this.studyNoteService = studyNoteService;
    }
}

