package com.english.learning.service;

import com.english.learning.common.exception.BusinessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BackupService {
    private static final Logger log = LoggerFactory.getLogger(BackupService.class);
    @Value(value="${spring.datasource.url}")
    private String datasourceUrl;
    @Value(value="${spring.datasource.username}")
    private String dbUsername;
    @Value(value="${spring.datasource.password}")
    private String dbPassword;
    @Value(value="${backup.path:./backups}")
    private String backupPath;
    @Value(value="${backup.max-count:10}")
    private int maxBackupCount;

    public Map<String, Object> createBackup(String description) {
        log.info("\u5f00\u59cb\u521b\u5efa\u6570\u636e\u5e93\u5907\u4efd...");
        Path backupDir = Paths.get(this.backupPath, new String[0]);
        try {
            Files.createDirectories(backupDir, new FileAttribute[0]);
        }
        catch (IOException e) {
            throw new BusinessException("\u521b\u5efa\u5907\u4efd\u76ee\u5f55\u5931\u8d25: " + e.getMessage());
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "backup_" + timestamp + ".sql";
        Path backupFile = backupDir.resolve(filename);
        String dbName = this.extractDatabaseName(this.datasourceUrl);
        String host = this.extractHost(this.datasourceUrl);
        String port = this.extractPort(this.datasourceUrl);
        try {
            ArrayList<String> command = new ArrayList<String>();
            command.add("mysqldump");
            command.add("-h" + host);
            command.add("-P" + port);
            command.add("-u" + this.dbUsername);
            command.add("-p" + this.dbPassword);
            command.add("--single-transaction");
            command.add("--routines");
            command.add("--triggers");
            command.add("--events");
            command.add(dbName);
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(backupFile.toFile());
            pb.redirectErrorStream(false);
            Process process = pb.start();
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));){
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Warning")) continue;
                    errorOutput.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                Files.deleteIfExists(backupFile);
                throw new BusinessException("\u5907\u4efd\u5931\u8d25: " + errorOutput);
            }
            long fileSize = Files.size(backupFile);
            this.saveBackupMetadata(filename, description, fileSize);
            this.cleanOldBackups();
            log.info("\u6570\u636e\u5e93\u5907\u4efd\u5b8c\u6210: {}",filename);
            HashMap<String, Object> result = new HashMap<String, Object>();
            result.put("filename", filename);
            result.put("path", backupFile.toString());
            result.put("size", this.formatFileSize(fileSize));
            result.put("createTime", LocalDateTime.now());
            result.put("description", description);
            return result;
        }
        catch (IOException | InterruptedException e) {
            log.error("\u5907\u4efd\u5931\u8d25", (Throwable)e);
            throw new BusinessException("\u5907\u4efd\u5931\u8d25: " + e.getMessage());
        }
    }

    public void restoreBackup(String filename) {
        log.info("\u5f00\u59cb\u6062\u590d\u6570\u636e\u5e93: {}",filename);
        Path backupFile = Paths.get(this.backupPath, filename);
        if (!Files.exists(backupFile, new LinkOption[0])) {
            throw new BusinessException("\u5907\u4efd\u6587\u4ef6\u4e0d\u5b58\u5728: " + filename);
        }
        String dbName = this.extractDatabaseName(this.datasourceUrl);
        String host = this.extractHost(this.datasourceUrl);
        String port = this.extractPort(this.datasourceUrl);
        try {
            ArrayList<String> command = new ArrayList<String>();
            command.add("mysql");
            command.add("-h" + host);
            command.add("-P" + port);
            command.add("-u" + this.dbUsername);
            command.add("-p" + this.dbPassword);
            command.add(dbName);
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectInput(backupFile.toFile());
            Process process = pb.start();
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));){
                String line;
                while ((line = reader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new BusinessException("\u6062\u590d\u5931\u8d25: " + errorOutput);
            }
            log.info("\u6570\u636e\u5e93\u6062\u590d\u5b8c\u6210: {}",filename);
        }
        catch (IOException | InterruptedException e) {
            log.error("\u6062\u590d\u5931\u8d25", (Throwable)e);
            throw new BusinessException("\u6062\u590d\u5931\u8d25: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getBackupList() {
        Path backupDir = Paths.get(this.backupPath, new String[0]);
        if (!Files.exists(backupDir, new LinkOption[0])) {
            return new ArrayList<Map<String, Object>>();
        }
        try (Stream<Path> paths = Files.list(backupDir)) {
            return paths.filter(p -> p.toString().endsWith(".sql")).sorted(Comparator.comparing(p -> {
                try {
                    return Files.getLastModifiedTime(p, new LinkOption[0]);
                }
                catch (IOException e) {
                    return null;
                }
            }, Comparator.reverseOrder())).map(arg_0 -> this.buildBackupInfo(arg_0)).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("\u83b7\u53d6\u5907\u4efd\u5217\u8868\u5931\u8d25", (Throwable)e);
            return new ArrayList<Map<String, Object>>();
        }
    }

    public void deleteBackup(String filename) {
        Path backupFile = Paths.get(this.backupPath, filename);
        Path metaFile = Paths.get(this.backupPath, filename + ".meta");
        try {
            Files.deleteIfExists(backupFile);
            Files.deleteIfExists(metaFile);
            log.info("\u5220\u9664\u5907\u4efd: {}",filename);
        }
        catch (IOException e) {
            throw new BusinessException("\u5220\u9664\u5907\u4efd\u5931\u8d25: " + e.getMessage());
        }
    }

    public byte[] downloadBackup(String filename) {
        Path backupFile = Paths.get(this.backupPath, filename);
        if (!Files.exists(backupFile, new LinkOption[0])) {
            throw new BusinessException("\u5907\u4efd\u6587\u4ef6\u4e0d\u5b58\u5728");
        }
        try {
            return Files.readAllBytes(backupFile);
        }
        catch (IOException e) {
            throw new BusinessException("\u8bfb\u53d6\u5907\u4efd\u6587\u4ef6\u5931\u8d25: " + e.getMessage());
        }
    }

    public void uploadAndRestore(byte[] data, String filename) {
        Path backupFile = Paths.get(this.backupPath, filename);
        try {
            Files.createDirectories(backupFile.getParent(), new FileAttribute[0]);
            Files.write(backupFile, data, new OpenOption[0]);
            this.restoreBackup(filename);
        }
        catch (IOException e) {
            throw new BusinessException("\u4e0a\u4f20\u5907\u4efd\u6587\u4ef6\u5931\u8d25: " + e.getMessage());
        }
    }

    private String extractDatabaseName(String url) {
        String[] parts = url.split("/");
        String dbPart = parts[parts.length - 1];
        int questionMark = dbPart.indexOf(63);
        return questionMark > 0 ? dbPart.substring(0, questionMark) : dbPart;
    }

    private String extractHost(String url) {
        String[] parts = url.split("//")[1].split("/")[0].split(":");
        return parts[0];
    }

    private String extractPort(String url) {
        String[] parts = url.split("//")[1].split("/")[0].split(":");
        return parts.length > 1 ? parts[1] : "3306";
    }

    private void saveBackupMetadata(String filename, String description, long fileSize) {
        Path metaFile = Paths.get(this.backupPath, filename + ".meta");
        Properties props = new Properties();
        props.setProperty("filename", filename);
        props.setProperty("description", description != null ? description : "");
        props.setProperty("size", String.valueOf(fileSize));
        props.setProperty("createTime", LocalDateTime.now().toString());
        try (OutputStream out = Files.newOutputStream(metaFile, new OpenOption[0]);){
            props.store(out, "Backup Metadata");
        }
        catch (IOException e) {
            log.warn("\u4fdd\u5b58\u5907\u4efd\u5143\u6570\u636e\u5931\u8d25: {}",e.getMessage());
        }
    }

    private Map<String, Object> buildBackupInfo(Path path) {
        HashMap<String, Object> info = new HashMap<String, Object>();
        String filename = path.getFileName().toString();
        info.put("filename", filename);
        try {
            info.put("size", this.formatFileSize(Files.size(path)));
            info.put("createTime", Files.getLastModifiedTime(path, new LinkOption[0]).toInstant());
        }
        catch (IOException e) {
            info.put("size", "N/A");
            info.put("createTime", null);
        }
        Path metaFile = Paths.get(this.backupPath, filename + ".meta");
        if (Files.exists(metaFile, new LinkOption[0])) {
            try (InputStream in = Files.newInputStream(metaFile, new OpenOption[0]);){
                Properties props = new Properties();
                props.load(in);
                info.put("description", props.getProperty("description", ""));
            }
            catch (IOException e) {
                info.put("description", "");
            }
        } else {
            info.put("description", "");
        }
        return info;
    }

    private void cleanOldBackups() {
        try {
            List backups = this.getBackupList();
            if (backups.size() > this.maxBackupCount) {
                for (int i = this.maxBackupCount; i < backups.size(); ++i) {
                    String filename = (String)((Map)backups.get(i)).get("filename");
                    this.deleteBackup(filename);
                    log.info("\u6e05\u7406\u65e7\u5907\u4efd: {}",filename);
                }
            }
        }
        catch (Exception e) {
            log.warn("\u6e05\u7406\u65e7\u5907\u4efd\u5931\u8d25: {}",e.getMessage());
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024L) {
            return bytes + " B";
        }
        if (bytes < 0x100000L) {
            return String.format("%.2f KB", (double)bytes / 1024.0);
        }
        if (bytes < 0x40000000L) {
            return String.format("%.2f MB", (double)bytes / 1048576.0);
        }
        return String.format("%.2f GB", (double)bytes / 1.073741824E9);
    }
}

