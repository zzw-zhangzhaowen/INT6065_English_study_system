package com.english.learning.service;

import com.english.learning.common.exception.BusinessException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    @Value(value="${file.upload-path:./uploads}")
    private String uploadPath;
    @Value(value="${file.max-size:104857600}")
    private long maxSize;
    @Value(value="${file.allowed-extensions:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,ppt,pptx,mp3,mp4,avi,webm}")
    private String allowedExtensions;

    public String uploadFile(MultipartFile file, String subDir) {
        if (file.isEmpty()) {
            throw new BusinessException("\u4e0a\u4f20\u6587\u4ef6\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (file.getSize() > this.maxSize) {
            throw new BusinessException("\u6587\u4ef6\u5927\u5c0f\u8d85\u8fc7\u9650\u5236");
        }
        String originalFilename = file.getOriginalFilename();
        String extension = this.getFileExtension(originalFilename);
        if (!this.isAllowedExtension(extension)) {
            throw new BusinessException("\u4e0d\u652f\u6301\u7684\u6587\u4ef6\u7c7b\u578b");
        }
        String newFilename = this.generateFilename(extension);
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = (String)(subDir != null ? subDir + "/" : "") + datePath;
        Path targetDir = Paths.get(this.uploadPath, relativePath);
        try {
            Files.createDirectories(targetDir, new FileAttribute[0]);
            Path targetPath = targetDir.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + relativePath + "/" + newFilename;
        }
        catch (IOException e) {
            log.error("\u6587\u4ef6\u4e0a\u4f20\u5931\u8d25", (Throwable)e);
            throw new BusinessException("\u6587\u4ef6\u4e0a\u4f20\u5931\u8d25");
        }
    }

    public String uploadImage(MultipartFile file) {
        String extension = this.getFileExtension(file.getOriginalFilename());
        List<String> imageExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
        if (!imageExtensions.contains(extension.toLowerCase())) {
            throw new BusinessException("\u53ea\u652f\u6301\u4e0a\u4f20\u56fe\u7247\u6587\u4ef6");
        }
        return this.uploadFile(file, "images");
    }

    public String uploadAudio(MultipartFile file) {
        String extension = this.getFileExtension(file.getOriginalFilename());
        List<String> audioExtensions = Arrays.asList("mp3", "wav", "ogg", "m4a");
        if (!audioExtensions.contains(extension.toLowerCase())) {
            throw new BusinessException("\u53ea\u652f\u6301\u4e0a\u4f20\u97f3\u9891\u6587\u4ef6");
        }
        return this.uploadFile(file, "audio");
    }

    public String uploadVideo(MultipartFile file) {
        String extension = this.getFileExtension(file.getOriginalFilename());
        List<String> videoExtensions = Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "webm");
        if (!videoExtensions.contains(extension.toLowerCase())) {
            throw new BusinessException("\u53ea\u652f\u6301\u4e0a\u4f20\u89c6\u9891\u6587\u4ef6");
        }
        return this.uploadFile(file, "video");
    }

    public String uploadDocument(MultipartFile file) {
        String extension = this.getFileExtension(file.getOriginalFilename());
        List<String> docExtensions = Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt");
        if (!docExtensions.contains(extension.toLowerCase())) {
            throw new BusinessException("\u53ea\u652f\u6301\u4e0a\u4f20\u6587\u6863\u6587\u4ef6");
        }
        return this.uploadFile(file, "documents");
    }

    public void deleteFile(String filePath) {
        if (!StringUtils.hasText((String)filePath)) {
            return;
        }
        try {
            String relativePath = filePath.replace("/uploads/", "");
            Path path = Paths.get(this.uploadPath, relativePath);
            if (Files.exists(path, new LinkOption[0])) {
                Files.delete(path);
            }
        }
        catch (IOException e) {
            log.error("\u6587\u4ef6\u5220\u9664\u5931\u8d25: {}",filePath,e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isAllowedExtension(String extension) {
        if (!StringUtils.hasText((String)extension)) {
            return false;
        }
        List<String> allowed = Arrays.asList(this.allowedExtensions.split(","));
        return allowed.contains(extension.toLowerCase());
    }

    private String generateFilename(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }
}

