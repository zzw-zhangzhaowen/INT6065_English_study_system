package com.english.learning.controller;

import com.english.learning.common.result.Result;
import com.english.learning.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u4eea\u8868\u76d8", description="\u6570\u636e\u7edf\u8ba1\u4eea\u8868\u76d8\u63a5\u53e3")
@RestController
@RequestMapping(value={"/api/dashboard"})
public class DashboardController {
    private final DashboardService dashboardService;

    @Operation(summary="\u83b7\u53d6\u5b66\u751f\u4eea\u8868\u76d8\u6570\u636e")
    @GetMapping(value={"/student"})
    public Result<Map<String, Object>> getStudentDashboard(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.dashboardService.getStudentDashboard(userId));
    }

    @Operation(summary="\u83b7\u53d6\u7ba1\u7406\u5458\u4eea\u8868\u76d8\u6570\u636e")
    @GetMapping(value={"/admin"})
    @PreAuthorize(value="hasRole('ADMIN')")
    public Result<Map<String, Object>> getAdminDashboard() {
        return Result.success(this.dashboardService.getAdminDashboard());
    }

    @Operation(summary="\u83b7\u53d6\u5b66\u4e60\u8d8b\u52bf\u6570\u636e")
    @GetMapping(value={"/trend"})
    public Result<Map<String, Object>> getStudyTrend(Authentication authentication, @RequestParam(defaultValue="7") int days) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.dashboardService.getStudyTrend(userId, days));
    }

    @Operation(summary="\u83b7\u53d6\u6559\u5e08\u4eea\u8868\u76d8\u6570\u636e")
    @GetMapping(value={"/teacher"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Map<String, Object>> getTeacherDashboard(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.dashboardService.getTeacherDashboard(userId));
    }

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
}

