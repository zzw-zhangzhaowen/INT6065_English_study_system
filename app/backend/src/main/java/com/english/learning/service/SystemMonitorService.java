package com.english.learning.service;

import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SystemMonitorService {
    private static final Logger log = LoggerFactory.getLogger(SystemMonitorService.class);
    private final RedisTemplate<String, Object> redisTemplate;

    public Map<String, Object> getSystemOverview() {
        HashMap<String, Object> overview = new HashMap<String, Object>();
        overview.put("jvm", this.getJvmInfo());
        overview.put("memory", this.getMemoryInfo());
        overview.put("cpu", this.getCpuInfo());
        overview.put("disk", this.getDiskInfo());
        overview.put("system", this.getSystemInfo());
        overview.put("serverTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return overview;
    }

    public Map<String, Object> getJvmInfo() {
        HashMap<String, Object> jvmInfo = new HashMap<String, Object>();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        jvmInfo.put("name", runtimeMXBean.getVmName());
        jvmInfo.put("version", runtimeMXBean.getVmVersion());
        jvmInfo.put("vendor", runtimeMXBean.getVmVendor());
        long startTime = runtimeMXBean.getStartTime();
        jvmInfo.put("startTime", new Date(startTime));
        long uptime = runtimeMXBean.getUptime();
        jvmInfo.put("uptime", this.formatUptime(uptime));
        jvmInfo.put("uptimeMillis", uptime);
        MemoryUsage heapMemory = memoryMXBean.getHeapMemoryUsage();
        HashMap<String, Object> heap = new HashMap<String, Object>();
        heap.put("init", this.formatBytes(heapMemory.getInit()));
        heap.put("used", this.formatBytes(heapMemory.getUsed()));
        heap.put("committed", this.formatBytes(heapMemory.getCommitted()));
        heap.put("max", this.formatBytes(heapMemory.getMax()));
        heap.put("usagePercent", this.calculatePercent(heapMemory.getUsed(), heapMemory.getMax()));
        jvmInfo.put("heapMemory", heap);
        MemoryUsage nonHeapMemory = memoryMXBean.getNonHeapMemoryUsage();
        HashMap<String, String> nonHeap = new HashMap<String, String>();
        nonHeap.put("init", this.formatBytes(nonHeapMemory.getInit()));
        nonHeap.put("used", this.formatBytes(nonHeapMemory.getUsed()));
        nonHeap.put("committed", this.formatBytes(nonHeapMemory.getCommitted()));
        jvmInfo.put("nonHeapMemory", nonHeap);
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        HashMap<String, Number> thread = new HashMap<String, Number>();
        thread.put("totalStarted", threadMXBean.getTotalStartedThreadCount());
        thread.put("active", threadMXBean.getThreadCount());
        thread.put("daemon", threadMXBean.getDaemonThreadCount());
        thread.put("peak", threadMXBean.getPeakThreadCount());
        jvmInfo.put("thread", thread);
        ArrayList gcList = new ArrayList();
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            HashMap<String, Object> gcInfo = new HashMap<String, Object>();
            gcInfo.put("name", gc.getName());
            gcInfo.put("collectionCount", gc.getCollectionCount());
            gcInfo.put("collectionTime", gc.getCollectionTime() + "ms");
            gcList.add(gcInfo);
        }
        jvmInfo.put("gc", gcList);
        return jvmInfo;
    }

    public Map<String, Object> getMemoryInfo() {
        HashMap<String, Object> memoryInfo = new HashMap<String, Object>();
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        long usedMemory = totalMemory - freeMemory;
        memoryInfo.put("total", this.formatBytes(totalMemory));
        memoryInfo.put("free", this.formatBytes(freeMemory));
        memoryInfo.put("max", this.formatBytes(maxMemory));
        memoryInfo.put("used", this.formatBytes(usedMemory));
        memoryInfo.put("usagePercent", this.calculatePercent(usedMemory, maxMemory));
        return memoryInfo;
    }

    public Map<String, Object> getCpuInfo() {
        HashMap<String, Object> cpuInfo = new HashMap<String, Object>();
        java.lang.management.OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        cpuInfo.put("availableProcessors", osMXBean.getAvailableProcessors());
        cpuInfo.put("systemLoadAverage", osMXBean.getSystemLoadAverage());
        if (osMXBean instanceof OperatingSystemMXBean) {
            OperatingSystemMXBean sunOsMXBean = (OperatingSystemMXBean)osMXBean;
            cpuInfo.put("processCpuLoad", String.format("%.2f%%", sunOsMXBean.getProcessCpuLoad() * 100.0));
            cpuInfo.put("systemCpuLoad", String.format("%.2f%%", sunOsMXBean.getCpuLoad() * 100.0));
            cpuInfo.put("processCpuTime", sunOsMXBean.getProcessCpuTime() / 1000000L + "ms");
        }
        return cpuInfo;
    }

    public Map<String, Object> getDiskInfo() {
        HashMap<String, Object> diskInfo = new HashMap<String, Object>();
        ArrayList disks = new ArrayList();
        File[] roots = File.listRoots();
        long totalSpace = 0L;
        long usedSpace = 0L;
        long freeSpace = 0L;
        for (File root : roots) {
            HashMap<String, Object> disk = new HashMap<String, Object>();
            disk.put("path", root.getAbsolutePath());
            disk.put("total", this.formatBytes(root.getTotalSpace()));
            disk.put("free", this.formatBytes(root.getFreeSpace()));
            disk.put("usable", this.formatBytes(root.getUsableSpace()));
            disk.put("used", this.formatBytes(root.getTotalSpace() - root.getFreeSpace()));
            disk.put("usagePercent", this.calculatePercent(root.getTotalSpace() - root.getFreeSpace(), root.getTotalSpace()));
            disks.add(disk);
            totalSpace += root.getTotalSpace();
            freeSpace += root.getFreeSpace();
            usedSpace += root.getTotalSpace() - root.getFreeSpace();
        }
        diskInfo.put("disks", disks);
        diskInfo.put("totalSpace", this.formatBytes(totalSpace));
        diskInfo.put("usedSpace", this.formatBytes(usedSpace));
        diskInfo.put("freeSpace", this.formatBytes(freeSpace));
        diskInfo.put("usagePercent", this.calculatePercent(usedSpace, totalSpace));
        return diskInfo;
    }

    public Map<String, Object> getSystemInfo() {
        HashMap<String, Object> systemInfo = new HashMap<String, Object>();
        java.lang.management.OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        systemInfo.put("osName", osMXBean.getName());
        systemInfo.put("osVersion", osMXBean.getVersion());
        systemInfo.put("osArch", osMXBean.getArch());
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("javaVendor", System.getProperty("java.vendor"));
        systemInfo.put("javaHome", System.getProperty("java.home"));
        systemInfo.put("userDir", System.getProperty("user.dir"));
        systemInfo.put("userName", System.getProperty("user.name"));
        return systemInfo;
    }

    public Map<String, Object> getRedisInfo() {
        HashMap<String, Object> redisInfo = new HashMap<String, Object>();
        try {
            Properties info = this.redisTemplate.getConnectionFactory().getConnection().serverCommands().info();
            if (info != null) {
                redisInfo.put("version", info.getProperty("redis_version"));
                redisInfo.put("mode", info.getProperty("redis_mode"));
                redisInfo.put("connectedClients", info.getProperty("connected_clients"));
                redisInfo.put("usedMemory", info.getProperty("used_memory_human"));
                redisInfo.put("usedMemoryPeak", info.getProperty("used_memory_peak_human"));
                redisInfo.put("uptimeInDays", info.getProperty("uptime_in_days"));
                redisInfo.put("totalConnectionsReceived", info.getProperty("total_connections_received"));
                redisInfo.put("totalCommandsProcessed", info.getProperty("total_commands_processed"));
            }
            redisInfo.put("status", "connected");
        }
        catch (Exception e) {
            log.warn("Failed to get Redis info: {}",e.getMessage());
            redisInfo.put("status", "disconnected");
            redisInfo.put("error", e.getMessage());
        }
        return redisInfo;
    }

    public Map<String, Object> getRealTimeMetrics() {
        HashMap<String, Object> metrics = new HashMap<String, Object>();
        Runtime runtime = Runtime.getRuntime();
        java.lang.management.OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        metrics.put("memoryUsage", this.calculatePercent(usedMemory, maxMemory));
        if (osMXBean instanceof OperatingSystemMXBean) {
            OperatingSystemMXBean sunOsMXBean = (OperatingSystemMXBean)osMXBean;
            metrics.put("cpuUsage", Math.round(sunOsMXBean.getCpuLoad() * 100.0));
        } else {
            metrics.put("cpuUsage", 0);
        }
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        metrics.put("threadCount", threadMXBean.getThreadCount());
        metrics.put("timestamp", System.currentTimeMillis());
        return metrics;
    }

    private String formatBytes(long bytes) {
        if (bytes < 0L) {
            return "N/A";
        }
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

    private double calculatePercent(long used, long total) {
        if (total <= 0L) {
            return 0.0;
        }
        return (double)Math.round((double)used * 10000.0 / (double)total) / 100.0;
    }

    private String formatUptime(long millis) {
        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        if (days > 0L) {
            return String.format("%d\u5929%d\u5c0f\u65f6%d\u5206\u949f", days, hours % 24L, minutes % 60L);
        }
        if (hours > 0L) {
            return String.format("%d\u5c0f\u65f6%d\u5206\u949f", hours, minutes % 60L);
        }
        return String.format("%d\u5206\u949f", minutes);
    }

    public SystemMonitorService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}

