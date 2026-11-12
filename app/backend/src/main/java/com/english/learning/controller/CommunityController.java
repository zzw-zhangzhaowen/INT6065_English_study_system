package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name="\u793e\u533a\u4e92\u52a8")
@RestController
@RequestMapping(value={"/api/community"})
public class CommunityController {
    private final CommunityService communityService;

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u5e16\u5b50")
    @GetMapping(value={"/post/page"})
    public Result<Page<Map<String, Object>>> getPostPage(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) Long categoryId, @RequestParam(required=false) String postType) {
        Long userId = authentication != null ? (Long)authentication.getDetails() : null;
        return Result.success(this.communityService.getPostPage(pageNum, pageSize, categoryId, postType, userId));
    }

    @Operation(summary="\u83b7\u53d6\u5e16\u5b50\u8be6\u60c5")
    @GetMapping(value={"/post/{id}"})
    public Result<Map<String, Object>> getPostDetail(Authentication authentication, @PathVariable Long id) {
        Long userId = authentication != null ? (Long)authentication.getDetails() : null;
        Map detail = this.communityService.getPostDetail(id);
        if (userId != null) {
            detail.put("isLiked", this.communityService.isLiked(id, userId));
            detail.put("isCollected", this.communityService.isCollected(id, userId));
        }
        return Result.success(detail);
    }

    @Operation(summary="\u521b\u5efa\u5e16\u5b50")
    @PostMapping(value={"/post"})
    public Result<Void> createPost(Authentication authentication, @RequestBody Map<String, Object> body) {
        Long userId = (Long)authentication.getDetails();
        Long categoryId = body.get("categoryId") != null ? Long.valueOf(Long.parseLong(body.get("categoryId").toString())) : null;
        this.communityService.createPost(userId, categoryId, (String)body.get("title"), (String)body.get("content"), (String)body.get("postType"));
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u5e16\u5b50")
    @PutMapping(value={"/post/{id}"})
    public Result<Void> updatePost(Authentication authentication, @PathVariable Long id, @RequestBody Map<String, String> body) {
        Long userId = (Long)authentication.getDetails();
        this.communityService.updatePost(id, userId, body.get("title"), body.get("content"));
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u5e16\u5b50")
    @DeleteMapping(value={"/post/{id}"})
    public Result<Void> deletePost(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.communityService.deletePost(id, userId);
        return Result.success();
    }

    @Operation(summary="\u70b9\u8d5e/\u53d6\u6d88\u70b9\u8d5e\u5e16\u5b50")
    @PostMapping(value={"/post/{id}/like"})
    public Result<Map<String, Object>> toggleLikePost(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        boolean isLiked = this.communityService.toggleLikePost(id, userId);
        return Result.success(Map.of("isLiked", isLiked));
    }

    @Operation(summary="\u6536\u85cf/\u53d6\u6d88\u6536\u85cf\u5e16\u5b50")
    @PostMapping(value={"/post/{id}/collect"})
    public Result<Map<String, Object>> toggleCollectPost(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        boolean isCollected = this.communityService.toggleCollectPost(id, userId);
        return Result.success(Map.of("isCollected", isCollected));
    }

    @Operation(summary="\u83b7\u53d6\u70ed\u95e8\u8bdd\u9898")
    @GetMapping(value={"/hot-topics"})
    public Result<List<Map<String, Object>>> getHotTopics(@RequestParam(defaultValue="10") int limit) {
        return Result.success(this.communityService.getHotTopics(limit));
    }

    @Operation(summary="\u83b7\u53d6\u6d3b\u8dc3\u7528\u6237")
    @GetMapping(value={"/active-users"})
    public Result<List<Map<String, Object>>> getActiveUsers(@RequestParam(defaultValue="10") int limit) {
        return Result.success(this.communityService.getActiveUsers(limit));
    }

    @Operation(summary="\u7f6e\u9876\u5e16\u5b50")
    @PostMapping(value={"/post/{id}/top"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> topPost(@PathVariable Long id, @RequestParam boolean isTop) {
        this.communityService.topPost(id, isTop);
        return Result.success();
    }

    @Operation(summary="\u8bbe\u4e3a\u7cbe\u534e")
    @PostMapping(value={"/post/{id}/feature"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> featurePost(@PathVariable Long id, @RequestParam boolean isFeatured) {
        this.communityService.featurePost(id, isFeatured);
        return Result.success();
    }

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }
}

