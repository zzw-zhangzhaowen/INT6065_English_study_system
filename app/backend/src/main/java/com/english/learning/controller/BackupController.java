package com.english.learning.controller;

import com.english.learning.common.result.Result;
import com.english.learning.service.BackupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name="\u6570\u636e\u5907\u4efd\u6062\u590d")
@RestController
@RequestMapping(value={"/api/backup"})
@PreAuthorize(value="hasRole('ADMIN')")
public class BackupController {
    private final BackupService backupService;

    @Operation(summary="\u521b\u5efa\u5907\u4efd")
    @PostMapping
    public Result<Map<String, Object>> createBackup(@RequestBody(required=false) Map<String, String> body) {
        String description = body != null ? body.get("description") : null;
        return Result.success(this.backupService.createBackup(description));
    }

    @Operation(summary="\u83b7\u53d6\u5907\u4efd\u5217\u8868")
    @GetMapping(value={"/list"})
    public Result<List<Map<String, Object>>> getBackupList() {
        return Result.success(this.backupService.getBackupList());
    }

    @Operation(summary="\u6062\u590d\u5907\u4efd")
    @PostMapping(value={"/restore/{filename}"})
    public Result<Void> restoreBackup(@PathVariable String filename) {
        this.backupService.restoreBackup(filename);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u5907\u4efd")
    @DeleteMapping(value={"/{filename}"})
    public Result<Void> deleteBackup(@PathVariable String filename) {
        this.backupService.deleteBackup(filename);
        return Result.success();
    }

    @Operation(summary="\u4e0b\u8f7d\u5907\u4efd")
    @GetMapping(value={"/download/{filename}"})
    public ResponseEntity<byte[]> downloadBackup(@PathVariable String filename) {
        byte[] data = this.backupService.downloadBackup(filename);
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"").contentType(MediaType.APPLICATION_OCTET_STREAM).body(data);
    }

    @Operation(summary="\u4e0a\u4f20\u5e76\u6062\u590d\u5907\u4efd")
    @PostMapping(value={"/upload-restore"})
    public Result<Void> uploadAndRestore(@RequestParam(value="file") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".sql")) {
            return Result.fail((String)"\u8bf7\u4e0a\u4f20.sql\u683c\u5f0f\u7684\u5907\u4efd\u6587\u4ef6");
        }
        this.backupService.uploadAndRestore(file.getBytes(), filename);
        return Result.success();
    }

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }
}

