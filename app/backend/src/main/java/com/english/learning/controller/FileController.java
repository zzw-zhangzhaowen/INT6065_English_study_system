package com.english.learning.controller;

import com.english.learning.common.result.Result;
import com.english.learning.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name="\u6587\u4ef6\u4e0a\u4f20", description="\u6587\u4ef6\u4e0a\u4f20\u7ba1\u7406\u63a5\u53e3")
@RestController
@RequestMapping(value={"/api/file"})
public class FileController {
    private final FileService fileService;

    @Operation(summary="\u4e0a\u4f20\u6587\u4ef6")
    @PostMapping(value={"/upload"})
    public Result<Map<String, String>> uploadFile(@RequestParam(value="file") MultipartFile file, @RequestParam(required=false) String subDir) {
        String url = this.fileService.uploadFile(file, subDir);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary="\u4e0a\u4f20\u56fe\u7247")
    @PostMapping(value={"/upload/image"})
    public Result<Map<String, String>> uploadImage(@RequestParam(value="file") MultipartFile file) {
        String url = this.fileService.uploadImage(file);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary="\u4e0a\u4f20\u5934\u50cf")
    @PostMapping(value={"/upload/avatar"})
    public Result<Map<String, String>> uploadAvatar(@RequestParam(value="file") MultipartFile file) {
        String url = this.fileService.uploadImage(file);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary="\u4e0a\u4f20\u97f3\u9891")
    @PostMapping(value={"/upload/audio"})
    public Result<Map<String, String>> uploadAudio(@RequestParam(value="file") MultipartFile file) {
        String url = this.fileService.uploadAudio(file);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary="\u4e0a\u4f20\u89c6\u9891")
    @PostMapping(value={"/upload/video"})
    public Result<Map<String, String>> uploadVideo(@RequestParam(value="file") MultipartFile file) {
        String url = this.fileService.uploadVideo(file);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary="\u4e0a\u4f20\u6587\u6863")
    @PostMapping(value={"/upload/document"})
    public Result<Map<String, String>> uploadDocument(@RequestParam(value="file") MultipartFile file) {
        String url = this.fileService.uploadDocument(file);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary="\u5220\u9664\u6587\u4ef6")
    @DeleteMapping
    public Result<Void> deleteFile(@RequestParam String filePath) {
        this.fileService.deleteFile(filePath);
        return Result.success();
    }

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
}

