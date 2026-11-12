package com.english.learning.controller;

import com.english.learning.common.result.Result;
import com.english.learning.entity.ResourceCategory;
import com.english.learning.service.ResourceCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u8d44\u6e90\u5206\u7c7b")
@RestController
@RequestMapping(value={"/api/resource-category"})
public class ResourceCategoryController {
    private final ResourceCategoryService resourceCategoryService;

    @Operation(summary="\u83b7\u53d6\u5206\u7c7b\u6811")
    @GetMapping(value={"/tree"})
    public Result<List<ResourceCategoryService.CategoryTreeNode>> getCategoryTree() {
        return Result.success(this.resourceCategoryService.getCategoryTree());
    }

    @Operation(summary="\u83b7\u53d6\u6240\u6709\u5206\u7c7b")
    @GetMapping(value={"/list"})
    public Result<List<ResourceCategory>> getAllCategories() {
        return Result.success(this.resourceCategoryService.getAllCategories());
    }

    @Operation(summary="\u521b\u5efa\u5206\u7c7b")
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping
    public Result<Void> createCategory(Authentication authentication, @RequestBody ResourceCategory category) {
        Long userId = (Long)authentication.getDetails();
        this.resourceCategoryService.createCategory(category, userId);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u5206\u7c7b")
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping(value={"/{id}"})
    public Result<Void> updateCategory(Authentication authentication, @PathVariable Long id, @RequestBody ResourceCategory category) {
        Long userId = (Long)authentication.getDetails();
        this.resourceCategoryService.updateCategory(id, category, userId);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u5206\u7c7b")
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping(value={"/{id}"})
    public Result<Void> deleteCategory(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.resourceCategoryService.deleteCategory(id, userId);
        return Result.success();
    }

    public ResourceCategoryController(ResourceCategoryService resourceCategoryService) {
        this.resourceCategoryService = resourceCategoryService;
    }
}

