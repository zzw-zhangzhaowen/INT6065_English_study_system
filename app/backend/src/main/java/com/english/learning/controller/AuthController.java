package com.english.learning.controller;

import com.english.learning.annotation.Log;
import com.english.learning.common.result.Result;
import com.english.learning.dto.LoginRequest;
import com.english.learning.dto.RegisterRequest;
import com.english.learning.service.AdminService;
import com.english.learning.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u8ba4\u8bc1\u7ba1\u7406")
@RestController
@RequestMapping(value={"/api/auth"})
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;

    @Operation(summary="\u83b7\u53d6\u516c\u5f00\u7cfb\u7edf\u914d\u7f6e\uff08Logo\u3001\u540d\u79f0\uff09")
    @GetMapping(value={"/public-config"})
    public Result<Map<String, String>> getPublicConfig() {
        Map<String, String> allConfig = this.adminService.getConfigMap();
        HashMap<String, String> publicConfig = new HashMap<String, String>();
        publicConfig.put("systemName", allConfig.getOrDefault("systemName", "\u82f1\u8bed\u7b49\u7ea7\u667a\u80fd\u5b66\u4e60\u4e0e\u8003\u8bd5\u7cfb\u7edf"));
        publicConfig.put("systemLogo", allConfig.getOrDefault("systemLogo", ""));
        return Result.success(publicConfig);
    }

    @Operation(summary="\u751f\u6210\u5bc6\u7801\u54c8\u5e0c")
    @GetMapping(value={"/generate-hash"})
    public Result<Map<String, String>> generateHash(@RequestParam String password) {
        String hash = this.passwordEncoder.encode(password);
        boolean verify = this.passwordEncoder.matches(password, hash);
        return Result.success(Map.of("password", password, "hash", hash, "verify", String.valueOf(verify)));
    }

    @Log(value="\u7528\u6237\u767b\u5f55", type=Log.OperationType.LOGIN, saveParams=false)
    @Operation(summary="\u7528\u6237\u767b\u5f55")
    @PostMapping(value={"/login"})
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(this.authService.login(request));
    }

    @Log(value="\u7528\u6237\u6ce8\u518c", type=Log.OperationType.CREATE)
    @Operation(summary="\u7528\u6237\u6ce8\u518c")
    @PostMapping(value={"/register"})
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        this.authService.register(request);
        return Result.success();
    }

    @Log(value="\u9000\u51fa\u767b\u5f55", type=Log.OperationType.LOGOUT)
    @Operation(summary="\u9000\u51fa\u767b\u5f55")
    @PostMapping(value={"/logout"})
    public Result<Void> logout() {
        return Result.success();
    }

    @Operation(summary="\u5fd8\u8bb0\u5bc6\u7801 - \u53d1\u9001\u9a8c\u8bc1\u7801")
    @PostMapping(value={"/forgot-password"})
    public Result<Void> sendResetCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        this.authService.sendResetCode(email);
        return Result.success();
    }

    @Log(value="\u91cd\u7f6e\u5bc6\u7801", type=Log.OperationType.UPDATE, saveParams=false)
    @Operation(summary="\u91cd\u7f6e\u5bc6\u7801")
    @PostMapping(value={"/reset-password"})
    public Result<Void> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        String newPassword = body.get("newPassword");
        this.authService.resetPassword(email, code, newPassword);
        return Result.success();
    }

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder, AdminService adminService) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.adminService = adminService;
    }
}

