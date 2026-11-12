package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.service.PostReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name="\u5e16\u5b50\u56de\u590d", description="\u5e16\u5b50\u56de\u590d\u7ba1\u7406\u63a5\u53e3")
@RestController
@RequestMapping(value={"/api/community/reply"})
public class PostReplyController {
    private final PostReplyService postReplyService;

    @Operation(summary="\u83b7\u53d6\u5e16\u5b50\u56de\u590d\u5217\u8868")
    @GetMapping(value={"/page"})
    public Result<Page<Map<String, Object>>> getReplyPage(@RequestParam Long postId, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize) {
        return Result.success(this.postReplyService.getReplyPage(pageNum, pageSize, postId));
    }

    @Operation(summary="\u521b\u5efa\u56de\u590d")
    @PostMapping
    public Result<Void> createReply(Authentication authentication, @RequestBody Map<String, Object> body) {
        Long userId = (Long)authentication.getDetails();
        Long postId = Long.valueOf(body.get("postId").toString());
        String content = body.get("content").toString();
        Long parentId = body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : null;
        this.postReplyService.createReply(postId, userId, content, parentId);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u56de\u590d")
    @DeleteMapping(value={"/{id}"})
    public Result<Void> deleteReply(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.postReplyService.deleteReply(id, userId);
        return Result.success();
    }

    @Operation(summary="\u70b9\u8d5e/\u53d6\u6d88\u70b9\u8d5e\u56de\u590d")
    @PostMapping(value={"/{id}/like"})
    public Result<Map<String, Object>> likeReply(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        boolean isLiked = this.postReplyService.toggleLikeReply(id, userId);
        return Result.success(Map.of("isLiked", isLiked));
    }

    @Operation(summary="\u8bbe\u4e3a\u6700\u4f73\u7b54\u6848")
    @PostMapping(value={"/{id}/best"})
    public Result<Void> setBestAnswer(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.postReplyService.setBestAnswer(id, userId);
        return Result.success();
    }

    public PostReplyController(PostReplyService postReplyService) {
        this.postReplyService = postReplyService;
    }
}

