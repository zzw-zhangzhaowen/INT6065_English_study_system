package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.entity.SysLog;
import com.english.learning.service.SysLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u64cd\u4f5c\u65e5\u5fd7")
@RestController
@RequestMapping(value={"/api/log"})
@PreAuthorize(value="hasRole('ADMIN')")
public class SysLogController {
    private final SysLogService sysLogService;

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u65e5\u5fd7")
    @GetMapping(value={"/page"})
    public Result<Page<SysLog>> getLogPage(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String username, @RequestParam(required=false) String operation, @RequestParam(required=false) Integer status, @RequestParam(required=false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime startTime, @RequestParam(required=false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(this.sysLogService.getLogPage(pageNum, pageSize, username, operation, status, startTime, endTime));
    }

    @Operation(summary="\u83b7\u53d6\u65e5\u5fd7\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<SysLog> getLogById(@PathVariable Long id) {
        return Result.success(this.sysLogService.getLogById(id));
    }

    @Operation(summary="\u5220\u9664\u65e5\u5fd7")
    @DeleteMapping(value={"/{id}"})
    public Result<Void> deleteLog(@PathVariable Long id) {
        this.sysLogService.deleteLog(id);
        return Result.success();
    }

    @Operation(summary="\u6279\u91cf\u5220\u9664\u65e5\u5fd7")
    @DeleteMapping(value={"/batch"})
    public Result<Void> deleteLogs(@RequestBody List<Long> ids) {
        this.sysLogService.deleteLogs(ids);
        return Result.success();
    }

    @Operation(summary="\u6e05\u7a7a\u65e5\u5fd7")
    @DeleteMapping(value={"/clear"})
    public Result<Void> clearLogs() {
        this.sysLogService.clearLogs();
        return Result.success();
    }

    @Operation(summary="\u6e05\u7406\u65e7\u65e5\u5fd7")
    @DeleteMapping(value={"/clean/{days}"})
    public Result<Map<String, Object>> cleanOldLogs(@PathVariable int days) {
        int count = this.sysLogService.cleanOldLogs(days);
        return Result.success(Map.of("deletedCount", count));
    }

    @Operation(summary="\u83b7\u53d6\u65e5\u5fd7\u7edf\u8ba1")
    @GetMapping(value={"/stats"})
    public Result<Map<String, Object>> getLogStats() {
        return Result.success(this.sysLogService.getLogStats());
    }

    @Operation(summary="\u83b7\u53d6\u5f53\u524d\u7528\u6237\u64cd\u4f5c\u65e5\u5fd7")
    @GetMapping(value={"/my"})
    public Result<List<SysLog>> getMyLogs(Authentication authentication, @RequestParam(defaultValue="20") int limit) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.sysLogService.getUserLogs(userId, limit));
    }

    @Operation(summary="\u83b7\u53d6\u6700\u8fd1\u64cd\u4f5c\u65e5\u5fd7")
    @GetMapping(value={"/recent"})
    public Result<List<SysLog>> getRecentLogs(@RequestParam(defaultValue="20") int limit) {
        return Result.success(this.sysLogService.getRecentLogs(limit));
    }

    public SysLogController(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }
}

