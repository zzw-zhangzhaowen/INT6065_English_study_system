package com.english.learning.controller;

import com.english.learning.common.result.Result;
import com.english.learning.service.SystemMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u7cfb\u7edf\u76d1\u63a7")
@RestController
@RequestMapping(value={"/api/monitor"})
@PreAuthorize(value="hasRole('ADMIN')")
public class SystemMonitorController {
    private final SystemMonitorService systemMonitorService;

    @Operation(summary="\u83b7\u53d6\u7cfb\u7edf\u6982\u89c8")
    @GetMapping(value={"/overview"})
    public Result<Map<String, Object>> getSystemOverview() {
        return Result.success(this.systemMonitorService.getSystemOverview());
    }

    @Operation(summary="\u83b7\u53d6JVM\u4fe1\u606f")
    @GetMapping(value={"/jvm"})
    public Result<Map<String, Object>> getJvmInfo() {
        return Result.success(this.systemMonitorService.getJvmInfo());
    }

    @Operation(summary="\u83b7\u53d6\u5185\u5b58\u4fe1\u606f")
    @GetMapping(value={"/memory"})
    public Result<Map<String, Object>> getMemoryInfo() {
        return Result.success(this.systemMonitorService.getMemoryInfo());
    }

    @Operation(summary="\u83b7\u53d6CPU\u4fe1\u606f")
    @GetMapping(value={"/cpu"})
    public Result<Map<String, Object>> getCpuInfo() {
        return Result.success(this.systemMonitorService.getCpuInfo());
    }

    @Operation(summary="\u83b7\u53d6\u78c1\u76d8\u4fe1\u606f")
    @GetMapping(value={"/disk"})
    public Result<Map<String, Object>> getDiskInfo() {
        return Result.success(this.systemMonitorService.getDiskInfo());
    }

    @Operation(summary="\u83b7\u53d6\u7cfb\u7edf\u4fe1\u606f")
    @GetMapping(value={"/system"})
    public Result<Map<String, Object>> getSystemInfo() {
        return Result.success(this.systemMonitorService.getSystemInfo());
    }

    @Operation(summary="\u83b7\u53d6Redis\u4fe1\u606f")
    @GetMapping(value={"/redis"})
    public Result<Map<String, Object>> getRedisInfo() {
        return Result.success(this.systemMonitorService.getRedisInfo());
    }

    @Operation(summary="\u83b7\u53d6\u5b9e\u65f6\u76d1\u63a7\u6570\u636e")
    @GetMapping(value={"/realtime"})
    public Result<Map<String, Object>> getRealTimeMetrics() {
        return Result.success(this.systemMonitorService.getRealTimeMetrics());
    }

    public SystemMonitorController(SystemMonitorService systemMonitorService) {
        this.systemMonitorService = systemMonitorService;
    }
}

