package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.entity.LearningResource;
import com.english.learning.service.LearningResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u5b66\u4e60\u8d44\u6e90")
@RestController
@RequestMapping(value={"/api/learning"})
public class LearningResourceController {
    private final LearningResourceService learningResourceService;

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u5b66\u4e60\u8d44\u6e90")
    @GetMapping(value={"/resource/page"})
    public Result<Page<LearningResource>> getResourcePage(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String resourceType, @RequestParam(required=false) String level, @RequestParam(required=false) String keyword, @RequestParam(required=false) Long categoryId, @RequestParam(required=false) Integer status) {
        boolean includeOffline = false;
        if (authentication != null && authentication.getAuthorities() != null) {
            includeOffline = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(role -> "ROLE_TEACHER".equals(role) || "ROLE_ADMIN".equals(role));
        }
        return Result.success(this.learningResourceService.getResourcePage(pageNum, pageSize, resourceType, level, keyword, categoryId, status, includeOffline));
    }

    @Operation(summary="\u83b7\u53d6\u8d44\u6e90\u8be6\u60c5")
    @GetMapping(value={"/resource/{id}"})
    public Result<LearningResource> getResourceDetail(@PathVariable Long id) {
        return Result.success(this.learningResourceService.getResourceDetail(id));
    }

    @Operation(summary="\u521b\u5efa\u5b66\u4e60\u8d44\u6e90")
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping(value={"/resource"})
    public Result<Void> createResource(Authentication authentication, @RequestBody LearningResource resource) {
        Long userId = (Long)authentication.getDetails();
        this.learningResourceService.createResource(resource, userId);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u5b66\u4e60\u8d44\u6e90")
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping(value={"/resource/{id}"})
    public Result<Void> updateResource(Authentication authentication, @PathVariable Long id, @RequestBody LearningResource resource) {
        Long userId = (Long)authentication.getDetails();
        this.learningResourceService.updateResource(id, resource, userId);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u5b66\u4e60\u8d44\u6e90")
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping(value={"/resource/{id}"})
    public Result<Void> deleteResource(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.learningResourceService.deleteResource(id, userId);
        return Result.success();
    }

    @Operation(summary="\u6279\u91cf\u5220\u9664\u5b66\u4e60\u8d44\u6e90")
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping(value={"/resource/batch"})
    public Result<Void> batchDeleteResource(Authentication authentication, @RequestBody List<Long> ids) {
        Long userId = (Long)authentication.getDetails();
        this.learningResourceService.batchDeleteResource(ids, userId);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u8d44\u6e90\u72b6\u6001")
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping(value={"/resource/{id}/status"})
    public Result<Void> updateStatus(Authentication authentication, @PathVariable Long id, @RequestParam Integer status) {
        Long userId = (Long)authentication.getDetails();
        this.learningResourceService.updateStatus(id, status, userId);
        return Result.success();
    }

    @Operation(summary="\u70b9\u8d5e/\u53d6\u6d88\u70b9\u8d5e\u8d44\u6e90")
    @PostMapping(value={"/resource/{id}/like"})
    public Result<Boolean> toggleLike(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        boolean liked = this.learningResourceService.toggleLike(id, userId);
        return Result.success(liked);
    }

    @Operation(summary="\u83b7\u53d6\u70ed\u95e8\u8d44\u6e90")
    @GetMapping(value={"/resource/popular"})
    public Result<Page<LearningResource>> getPopularResources(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize) {
        return Result.success(this.learningResourceService.getPopularResources(pageNum, pageSize));
    }

    @Operation(summary="\u6536\u85cf\u8d44\u6e90")
    @PostMapping(value={"/resource/{id}/collect"})
    public Result<Void> collectResource(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.learningResourceService.collectResource(userId, id);
        return Result.success();
    }

    @Operation(summary="\u53d6\u6d88\u6536\u85cf\u8d44\u6e90")
    @DeleteMapping(value={"/resource/{id}/collect"})
    public Result<Void> uncollectResource(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.learningResourceService.uncollectResource(userId, id);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u6211\u7684\u6536\u85cf\u8d44\u6e90")
    @GetMapping(value={"/resource/collected"})
    public Result<Page<LearningResource>> getCollectedResources(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.learningResourceService.getCollectedResources(userId, pageNum, pageSize));
    }

    @Operation(summary="\u68c0\u67e5\u8d44\u6e90\u662f\u5426\u5df2\u6536\u85cf")
    @GetMapping(value={"/resource/{id}/is-collected"})
    public Result<Boolean> isCollected(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.learningResourceService.isCollected(userId, id));
    }

    @Operation(summary="\u4e0b\u8f7d\u8d44\u6e90")
    @GetMapping(value={"/resource/{id}/download"})
    public Result<LearningResource> downloadResource(@PathVariable Long id) {
        return Result.success(this.learningResourceService.downloadResource(id));
    }

    public LearningResourceController(LearningResourceService learningResourceService) {
        this.learningResourceService = learningResourceService;
    }
}

