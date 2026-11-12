package com.english.learning.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * 静态资源控制器 - 处理uploads目录下的文件请求
 * 当文件不存在时返回默认占位图
 */
@RestController
public class ResourceController {
    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    // 默认占位图的缓存
    private static byte[] defaultImageBytes = null;
    private static byte[] defaultVideoBytes = null;

    /**
     * 处理图片请求 - 当文件不存在时返回默认占位图
     */
    @GetMapping({"/uploads/images/**", "/api/uploads/images/**"})
    public ResponseEntity<byte[]> serveImage(jakarta.servlet.http.HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        // 提取文件路径
        String filePath = requestPath.replace("/api/uploads/", "/uploads/")
                                      .replace("/uploads/images/", "images/");
        
        Path fullPath = Paths.get(uploadPath, filePath);
        
        if (Files.exists(fullPath)) {
            try {
                byte[] content = Files.readAllBytes(fullPath);
                String contentType = getContentType(filePath);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(content);
            } catch (IOException e) {
                log.error("读取图片失败: {}", fullPath, e);
            }
        }
        
        // 文件不存在，返回默认占位图
        log.debug("图片不存在，返回占位图: {}", fullPath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .body(getDefaultImage());
    }

    /**
     * 处理视频请求 - 当文件不存在时返回默认占位视频
     */
    @GetMapping({"/uploads/video/**", "/api/uploads/video/**"})
    public ResponseEntity<byte[]> serveVideo(jakarta.servlet.http.HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String filePath = requestPath.replace("/api/uploads/", "/uploads/")
                                      .replace("/uploads/video/", "video/");
        
        Path fullPath = Paths.get(uploadPath, filePath);
        
        if (Files.exists(fullPath)) {
            try {
                byte[] content = Files.readAllBytes(fullPath);
                String contentType = getContentType(filePath);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(content);
            } catch (IOException e) {
                log.error("读取视频失败: {}", fullPath, e);
            }
        }
        
        // 视频文件不存在，返回404
        log.debug("视频不存在: {}", fullPath);
        return ResponseEntity.notFound().build();
    }

    /**
     * 处理音频请求
     */
    @GetMapping({"/uploads/audio/**", "/api/uploads/audio/**"})
    public ResponseEntity<byte[]> serveAudio(jakarta.servlet.http.HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String filePath = requestPath.replace("/api/uploads/", "/uploads/")
                                      .replace("/uploads/audio/", "audio/");
        
        Path fullPath = Paths.get(uploadPath, filePath);
        
        if (Files.exists(fullPath)) {
            try {
                byte[] content = Files.readAllBytes(fullPath);
                String contentType = getContentType(filePath);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(content);
            } catch (IOException e) {
                log.error("读取音频失败: {}", fullPath, e);
            }
        }
        
        log.debug("音频不存在: {}", fullPath);
        return ResponseEntity.notFound().build();
    }

    /**
     * 处理文档请求
     */
    @GetMapping({"/uploads/documents/**", "/api/uploads/documents/**"})
    public ResponseEntity<byte[]> serveDocument(jakarta.servlet.http.HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String filePath = requestPath.replace("/api/uploads/", "/uploads/")
                                      .replace("/uploads/documents/", "documents/");
        
        Path fullPath = Paths.get(uploadPath, filePath);
        
        if (Files.exists(fullPath)) {
            try {
                byte[] content = Files.readAllBytes(fullPath);
                String contentType = getContentType(filePath);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(content);
            } catch (IOException e) {
                log.error("读取文档失败: {}", fullPath, e);
            }
        }
        
        log.debug("文档不存在: {}", fullPath);
        return ResponseEntity.notFound().build();
    }

    /**
     * 处理头像请求 (photo目录)
     */
    @GetMapping({"/photo/**", "/api/photo/**"})
    public ResponseEntity<byte[]> serveAvatar(jakarta.servlet.http.HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String filePath = requestPath.replace("/api/photo/", "photo/")
                                      .replace("/photo/", "");
        
        Path fullPath = Paths.get(uploadPath, "photo", filePath);
        
        if (Files.exists(fullPath)) {
            try {
                byte[] content = Files.readAllBytes(fullPath);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                        .body(content);
            } catch (IOException e) {
                log.error("读取头像失败: {}", fullPath, e);
            }
        }
        
        // 返回默认头像
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .body(getDefaultImage());
    }

    /**
     * 生成默认占位图
     */
    private byte[] getDefaultImage() {
        if (defaultImageBytes != null) {
            return defaultImageBytes;
        }
        
        try {
            // 创建200x200的灰色占位图
            BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // 设置背景色
            g2d.setColor(new Color(240, 240, 240));
            g2d.fillRect(0, 0, 200, 200);
            
            // 绘制边框
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawRect(0, 0, 199, 199);
            
            // 绘制图标(简单的图片图标)
            g2d.setColor(new Color(150, 150, 150));
            g2d.fillOval(70, 50, 60, 60); // 圆形
            g2d.fillRect(40, 120, 120, 40); // 底部矩形
            
            // 添加文字
            g2d.setColor(new Color(100, 100, 100));
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("暂无图片", 70, 145);
            
            g2d.dispose();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            defaultImageBytes = baos.toByteArray();
            return defaultImageBytes;
        } catch (IOException e) {
            log.error("创建默认占位图失败", e);
            return new byte[0];
        }
    }

    /**
     * 根据文件扩展名获取Content-Type
     */
    private String getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "mp4":
                return "video/mp4";
            case "webm":
                return "video/webm";
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default:
                return "application/octet-stream";
        }
    }
}
