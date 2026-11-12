package com.english.learning.controller;

import com.english.learning.common.result.Result;
import com.english.learning.entity.QuestionCategory;
import com.english.learning.service.QuestionCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u9898\u76ee\u5206\u7c7b", description="\u9898\u76ee\u5206\u7c7b\u7ba1\u7406\u63a5\u53e3")
@RestController
@RequestMapping(value={"/api/question-category"})
public class QuestionCategoryController {
    private final QuestionCategoryService questionCategoryService;

    @Operation(summary="\u83b7\u53d6\u5206\u7c7b\u5217\u8868")
    @GetMapping(value={"/list"})
    public Result<List<QuestionCategory>> getCategoryList(@RequestParam(required=false) String level, @RequestParam(required=false) String skillType) {
        return Result.success(this.questionCategoryService.getCategoryList(level, skillType));
    }

    @Operation(summary="\u83b7\u53d6\u5206\u7c7b\u6811")
    @GetMapping(value={"/tree"})
    public Result<List<Map<String, Object>>> getCategoryTree() {
        return Result.success(this.questionCategoryService.getCategoryTree());
    }

    @Operation(summary="\u83b7\u53d6\u5206\u7c7b\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<QuestionCategory> getCategoryDetail(@PathVariable Long id) {
        return Result.success(this.questionCategoryService.getCategoryDetail(id));
    }

    @Operation(summary="\u521b\u5efa\u5206\u7c7b")
    @PostMapping
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> createCategory(@RequestBody QuestionCategory category) {
        this.questionCategoryService.createCategory(category);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u5206\u7c7b")
    @PutMapping(value={"/{id}"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody QuestionCategory category) {
        this.questionCategoryService.updateCategory(id, category);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u5206\u7c7b")
    @DeleteMapping(value={"/{id}"})
    @PreAuthorize(value="hasRole('ADMIN')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        this.questionCategoryService.deleteCategory(id);
        return Result.success();
    }

    public QuestionCategoryController(QuestionCategoryService questionCategoryService) {
        this.questionCategoryService = questionCategoryService;
    }
}

