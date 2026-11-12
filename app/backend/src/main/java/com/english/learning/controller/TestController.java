package com.english.learning.controller;

import com.english.learning.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u7cfb\u7edf\u6d4b\u8bd5")
@RestController
@RequestMapping(value={"/api/test"})
public class TestController {
    @Operation(summary="\u5065\u5eb7\u68c0\u67e5")
    @GetMapping(value={"/health"})
    public Result<Map<String, Object>> health() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("status", "ok");
        data.put("message", "\u82f1\u8bed\u7b49\u7ea7\u667a\u80fd\u5b66\u4e60\u4e0e\u8003\u8bd5\u7cfb\u7edf\u8fd0\u884c\u6b63\u5e38");
        data.put("timestamp", System.currentTimeMillis());
        return Result.success(data);
    }

    @Operation(summary="\u83b7\u53d6\u7cfb\u7edf\u4fe1\u606f")
    @GetMapping(value={"/info"})
    public Result<Map<String, String>> info() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("name", "\u82f1\u8bed\u7b49\u7ea7\u667a\u80fd\u5b66\u4e60\u4e0e\u8003\u8bd5\u7cfb\u7edf");
        data.put("version", "1.0.0");
        data.put("description", "\u57fa\u4e8eSpring Boot + Vue 3\u7684\u667a\u80fd\u5b66\u4e60\u5e73\u53f0");
        return Result.success(data);
    }
}

