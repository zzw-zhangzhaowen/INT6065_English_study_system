package com.english.learning.service;

import com.english.learning.common.exception.BusinessException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoService {
    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);
    private static final String PHOTO_DIR = "src/main/resources/photo";
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp");
    private static final long MAX_FILE_SIZE = 0xA00000L;

    private Path getPhotoDirectory() {
        Path devPath = Paths.get(PHOTO_DIR, new String[0]);
        if (Files.exists(devPath, new LinkOption[0])) {
            return devPath;
        }
        try {
            ClassPathResource resource = new ClassPathResource("photo");
            if (resource.exists()) {
                return Paths.get(resource.getURI());
            }
        }
        catch (IOException e) {
            log.error("\u65e0\u6cd5\u8bbf\u95eephoto\u8d44\u6e90\u76ee\u5f55", (Throwable)e);
        }
        try {
            Files.createDirectories(devPath, new FileAttribute[0]);
            return devPath;
        }
        catch (IOException e) {
            throw new BusinessException("\u65e0\u6cd5\u521b\u5efaphoto\u76ee\u5f55");
        }
    }

    public List<Map<String, Object>> listPhotos() {
        Path photoDir = this.getPhotoDirectory();
        try (Stream<Path> paths = Files.list(photoDir)) {
            return paths.filter(path -> Files.isRegularFile(path, new LinkOption[0])).filter(path -> {
                String filename = path.getFileName().toString().toLowerCase();
                return ALLOWED_EXTENSIONS.stream().anyMatch(filename::endsWith);
            }).map(path -> {
                HashMap<String, Object> photo = new HashMap<String, Object>();
                String filename = path.getFileName().toString();
                photo.put("filename", filename);
                photo.put("url", "/photo/" + filename);
                photo.put("apiUrl", "/api/photo/" + filename);
                try {
                    photo.put("size", Files.size(path));
                    photo.put("lastModified", Files.getLastModifiedTime(path, new LinkOption[0]).toString());
                } catch (IOException e) {
                    log.warn("\u65e0\u6cd5\u83b7\u53d6\u6587\u4ef6\u4fe1\u606f: {}", filename, e);
                }
                return photo;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("\u8bfb\u53d6photo\u76ee\u5f55\u5931\u8d25", e);
            return new ArrayList<Map<String, Object>>();
        }
    }

    public String uploadPhoto(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("\u4e0a\u4f20\u6587\u4ef6\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("\u6587\u4ef6\u5927\u5c0f\u4e0d\u80fd\u8d85\u8fc710MB");
        }
        String originalFilename = file.getOriginalFilename();
        String extension = this.getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("\u53ea\u652f\u6301\u56fe\u7247\u6587\u4ef6\uff08jpg\u3001jpeg\u3001png\u3001gif\u3001webp\u3001bmp\uff09");
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String newFilename = "photo_" + timestamp + "." + extension;
        Path photoDir = this.getPhotoDirectory();
        Path targetPath = photoDir.resolve(newFilename);
        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("\u56fe\u7247\u4e0a\u4f20\u6210\u529f: {}",newFilename);
            return "/photo/" + newFilename;
        }
        catch (IOException e) {
            log.error("\u56fe\u7247\u4e0a\u4f20\u5931\u8d25", (Throwable)e);
            throw new BusinessException("\u56fe\u7247\u4e0a\u4f20\u5931\u8d25");
        }
    }

    public void deletePhoto(String filename) {
        if (!StringUtils.hasText((String)filename)) {
            throw new BusinessException("\u6587\u4ef6\u540d\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new BusinessException("\u975e\u6cd5\u7684\u6587\u4ef6\u540d");
        }
        Path photoDir = this.getPhotoDirectory();
        Path filePath = photoDir.resolve(filename);
        if (!Files.exists(filePath, new LinkOption[0])) {
            throw new BusinessException("\u6587\u4ef6\u4e0d\u5b58\u5728");
        }
        try {
            Files.delete(filePath);
            log.info("\u56fe\u7247\u5220\u9664\u6210\u529f: {}",filename);
        }
        catch (IOException e) {
            log.error("\u56fe\u7247\u5220\u9664\u5931\u8d25: {}",filename,e);
            throw new BusinessException("\u56fe\u7247\u5220\u9664\u5931\u8d25");
        }
    }

    public String replacePhoto(String oldFilename, MultipartFile newFile) {
        this.deletePhoto(oldFilename);
        String extension = this.getFileExtension(newFile.getOriginalFilename());
        String baseFilename = oldFilename.substring(0, oldFilename.lastIndexOf(46));
        String newFilename = baseFilename + "." + extension;
        Path photoDir = this.getPhotoDirectory();
        Path targetPath = photoDir.resolve(newFilename);
        try {
            Files.copy(newFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("\u56fe\u7247\u66ff\u6362\u6210\u529f: {} -> {}",oldFilename,newFilename);
            return "/photo/" + newFilename;
        }
        catch (IOException e) {
            log.error("\u56fe\u7247\u66ff\u6362\u5931\u8d25", (Throwable)e);
            throw new BusinessException("\u56fe\u7247\u66ff\u6362\u5931\u8d25");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}

