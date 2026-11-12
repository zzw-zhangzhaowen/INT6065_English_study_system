package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.entity.Exam;
import com.english.learning.entity.Question;
import com.english.learning.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
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

@Tag(name="\u7cfb\u7edf\u7ba1\u7406")
@RestController
@RequestMapping(value={"/api/admin"})
@PreAuthorize(value="hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @Operation(summary="\u521b\u5efa\u7528\u6237")
    @PostMapping(value={"/user"})
    public Result<Void> createUser(@RequestBody Map<String, Object> userData) {
        this.adminService.createUser(userData);
        return Result.success();
    }

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u7528\u6237")
    @GetMapping(value={"/user/page"})
    public Result<Page<Map<String, Object>>> getUserPage(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String keyword, @RequestParam(required=false) Integer status, @RequestParam(required=false) String role) {
        return Result.success(this.adminService.getUserPage(pageNum, pageSize, keyword, status, role));
    }

    @Operation(summary="\u83b7\u53d6\u7528\u6237\u8be6\u60c5")
    @GetMapping(value={"/user/{id}"})
    public Result<Map<String, Object>> getUserDetail(@PathVariable Long id) {
        return Result.success(this.adminService.getUserDetail(id));
    }

    @Operation(summary="\u66f4\u65b0\u7528\u6237\u72b6\u6001")
    @PutMapping(value={"/user/{id}/status"})
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam(required=false) Integer status) {
        if (status == null) {
            this.adminService.toggleUserStatus(id);
        } else {
            this.adminService.updateUserStatus(id, status);
        }
        return Result.success();
    }

    @Operation(summary="\u91cd\u7f6e\u7528\u6237\u5bc6\u7801")
    @PutMapping(value={"/user/{id}/password"})
    public Result<Void> resetUserPassword(@PathVariable Long id, @RequestParam String newPassword) {
        this.adminService.resetUserPassword(id, newPassword);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u7528\u6237")
    @DeleteMapping(value={"/user/{id}"})
    public Result<Void> deleteUser(@PathVariable Long id) {
        this.adminService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u7528\u6237\u4fe1\u606f")
    @PutMapping(value={"/user/{id}"})
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        this.adminService.updateUser(id, userData);
        return Result.success();
    }

    @Operation(summary="\u5206\u914d\u7528\u6237\u89d2\u8272")
    @PutMapping(value={"/user/{id}/roles"})
    public Result<Void> assignUserRole(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        this.adminService.assignUserRole(id, roleIds);
        return Result.success();
    }

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u9898\u76ee")
    @GetMapping(value={"/question/page"})
    public Result<Page<Question>> getQuestionPage(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) Long categoryId, @RequestParam(required=false) String questionType, @RequestParam(required=false) Integer difficulty) {
        return Result.success(this.adminService.getQuestionPage(pageNum, pageSize, categoryId, questionType, difficulty));
    }

    @Operation(summary="\u521b\u5efa\u9898\u76ee")
    @PostMapping(value={"/question"})
    public Result<Void> createQuestion(@RequestBody Question question) {
        this.adminService.createQuestion(question);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u9898\u76ee")
    @PutMapping(value={"/question/{id}"})
    public Result<Void> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        this.adminService.updateQuestion(id, question);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u9898\u76ee")
    @DeleteMapping(value={"/question/{id}"})
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        this.adminService.deleteQuestion(id);
        return Result.success();
    }

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u8003\u8bd5")
    @GetMapping(value={"/exam/page"})
    public Result<Page<Exam>> getExamPage(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String examType, @RequestParam(required=false) String level) {
        return Result.success(this.adminService.getExamPage(pageNum, pageSize, examType, level));
    }

    @Operation(summary="\u521b\u5efa\u8003\u8bd5")
    @PostMapping(value={"/exam"})
    public Result<Void> createExam(@RequestBody Exam exam) {
        this.adminService.createExam(exam);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u8003\u8bd5")
    @PutMapping(value={"/exam/{id}"})
    public Result<Void> updateExam(@PathVariable Long id, @RequestBody Exam exam) {
        this.adminService.updateExam(id, exam);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u8003\u8bd5")
    @DeleteMapping(value={"/exam/{id}"})
    public Result<Void> deleteExam(@PathVariable Long id) {
        this.adminService.deleteExam(id);
        return Result.success();
    }

    @Operation(summary="\u6dfb\u52a0\u8003\u8bd5\u9898\u76ee")
    @PostMapping(value={"/exam/{examId}/question"})
    public Result<Void> addExamQuestion(@PathVariable Long examId, @RequestParam Long questionId, @RequestParam(defaultValue="0") Integer order, @RequestParam(defaultValue="1") BigDecimal score) {
        this.adminService.addExamQuestion(examId, questionId, order, score);
        return Result.success();
    }

    @Operation(summary="\u79fb\u9664\u8003\u8bd5\u9898\u76ee")
    @DeleteMapping(value={"/exam/{examId}/question/{questionId}"})
    public Result<Void> removeExamQuestion(@PathVariable Long examId, @PathVariable Long questionId) {
        this.adminService.removeExamQuestion(examId, questionId);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u7cfb\u7edf\u914d\u7f6e\u5217\u8868")
    @GetMapping(value={"/config/list"})
    public Result<Map<String, String>> getConfigList() {
        return Result.success(this.adminService.getConfigMap());
    }

    @Operation(summary="\u83b7\u53d6\u914d\u7f6e\u503c")
    @GetMapping(value={"/config/{key}"})
    public Result<String> getConfigValue(@PathVariable String key) {
        return Result.success(this.adminService.getConfigValue(key));
    }

    @Operation(summary="\u66f4\u65b0\u914d\u7f6e")
    @PutMapping(value={"/config/{key}"})
    public Result<Void> updateConfig(@PathVariable String key, @RequestParam String value) {
        this.adminService.updateConfig(key, value);
        return Result.success();
    }

    @Operation(summary="\u6279\u91cf\u66f4\u65b0\u914d\u7f6e")
    @PutMapping(value={"/config/batch"})
    public Result<Void> updateConfigs(@RequestBody Map<String, String> configMap) {
        this.adminService.updateConfigs(configMap);
        return Result.success();
    }

    @Operation(summary="\u6d4b\u8bd5\u90ae\u4ef6\u53d1\u9001")
    @PostMapping(value={"/config/test-email"})
    public Result<Void> testEmail() {
        this.adminService.testEmail();
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u9898\u5e93\u7edf\u8ba1")
    @GetMapping(value={"/question/stats"})
    public Result<Map<String, Object>> getQuestionStats() {
        return Result.success(this.adminService.getQuestionStats());
    }

    @Operation(summary="\u83b7\u53d6\u8003\u8bd5\u7edf\u8ba1")
    @GetMapping(value={"/exam/stats"})
    public Result<Map<String, Object>> getExamStats() {
        return Result.success(this.adminService.getExamStats());
    }

    @Operation(summary="\u83b7\u53d6\u89d2\u8272\u5217\u8868")
    @GetMapping(value={"/role/list"})
    public Result<List<Map<String, Object>>> getRoleList() {
        return Result.success(this.adminService.getRoleList());
    }

    @Operation(summary="\u521b\u5efa\u89d2\u8272")
    @PostMapping(value={"/role"})
    public Result<Void> createRole(@RequestBody Map<String, Object> roleData) {
        this.adminService.createRole(roleData);
        return Result.success();
    }

    @Operation(summary="\u66f4\u65b0\u89d2\u8272")
    @PutMapping(value={"/role/{id}"})
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> roleData) {
        this.adminService.updateRole(id, roleData);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u89d2\u8272")
    @DeleteMapping(value={"/role/{id}"})
    public Result<Void> deleteRole(@PathVariable Long id) {
        this.adminService.deleteRole(id);
        return Result.success();
    }

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
}

