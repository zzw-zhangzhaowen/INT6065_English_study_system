package com.english.learning.controller;

import com.english.learning.common.result.Result;
import com.english.learning.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name="\u56fe\u7247\u7ba1\u7406")
@RestController
@RequestMapping(value={"/api/photo"})
@PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
public class PhotoController {
    private final PhotoService photoService;

    @Operation(summary="\u83b7\u53d6photo\u6587\u4ef6\u5939\u4e2d\u7684\u56fe\u7247\u5217\u8868")
    @GetMapping(value={"/list"})
    public Result<List<Map<String, Object>>> listPhotos() {
        List photos = this.photoService.listPhotos();
        return Result.success(photos);
    }

    @Operation(summary="\u4e0a\u4f20\u56fe\u7247\u5230photo\u6587\u4ef6\u5939")
    @PostMapping(value={"/upload"})
    public Result<Map<String, String>> uploadPhoto(@RequestParam(value="file") MultipartFile file) {
        String url = this.photoService.uploadPhoto(file);
        Map<String, String> result = Map.of("url", url, "message", "\u56fe\u7247\u4e0a\u4f20\u6210\u529f");
        return Result.success(result);
    }

    @Operation(summary="\u5220\u9664photo\u6587\u4ef6\u5939\u4e2d\u7684\u56fe\u7247")
    @DeleteMapping(value={"/delete"})
    public Result<Void> deletePhoto(@RequestParam(value="filename") String filename) {
        this.photoService.deletePhoto(filename);
        return Result.success();
    }

    @Operation(summary="\u66ff\u6362photo\u6587\u4ef6\u5939\u4e2d\u7684\u56fe\u7247")
    @PostMapping(value={"/replace"})
    public Result<Map<String, String>> replacePhoto(@RequestParam(value="oldFilename") String oldFilename, @RequestParam(value="file") MultipartFile file) {
        String url = this.photoService.replacePhoto(oldFilename, file);
        Map<String, String> result = Map.of("url", url, "message", "\u56fe\u7247\u66ff\u6362\u6210\u529f");
        return Result.success(result);
    }

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }
}

