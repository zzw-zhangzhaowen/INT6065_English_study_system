package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.entity.SysLog;
import com.english.learning.mapper.SysLogMapper;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SysLogService {
    private static final Logger log = LoggerFactory.getLogger(SysLogService.class);
    private final SysLogMapper sysLogMapper;

    @Async
    public void saveLogAsync(SysLog sysLog) {
        try {
            this.sysLogMapper.insert(sysLog);
        }
        catch (Exception e) {
            log.error("\u4fdd\u5b58\u64cd\u4f5c\u65e5\u5fd7\u5931\u8d25", (Throwable)e);
        }
    }

    public void saveLog(SysLog sysLog) {
        this.sysLogMapper.insert(sysLog);
    }

    public Page<SysLog> getLogPage(int pageNum, int pageSize, String username, String operation, Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        Page<SysLog> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null, SysLog::getUsername, username).like(operation != null, SysLog::getOperation,operation).eq(status != null, SysLog::getStatus, status).ge(startTime != null, SysLog::getCreateTime,startTime).le(endTime != null, SysLog::getCreateTime,endTime).orderByDesc(SysLog::getCreateTime);
        return (Page<SysLog>)this.sysLogMapper.selectPage(page, wrapper);
    }

    public SysLog getLogById(Long id) {
        return (SysLog)this.sysLogMapper.selectById(id);
    }

    public void deleteLog(Long id) {
        this.sysLogMapper.deleteById(id);
    }

    public void deleteLogs(List<Long> ids) {
        this.sysLogMapper.delete(new LambdaQueryWrapper<SysLog>().in(SysLog::getId, ids));
    }

    public void clearLogs() {
        this.sysLogMapper.delete(null);
    }

    public int cleanOldLogs(int days) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SysLog::getCreateTime,cutoffTime);
        return this.sysLogMapper.delete(wrapper);
    }

    public Map<String, Object> getLogStats() {
        HashMap<String, Object> stats = new HashMap<String, Object>();
        long totalCount = this.sysLogMapper.selectCount(null);
        stats.put("totalCount", totalCount);
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayCount = this.sysLogMapper.selectCount(new LambdaQueryWrapper<SysLog>().ge(SysLog::getCreateTime,todayStart));
        stats.put("todayCount", todayCount);
        long successCount = this.sysLogMapper.selectCount(new LambdaQueryWrapper<SysLog>().eq(SysLog::getStatus,1));
        stats.put("successCount", successCount);
        long failCount = this.sysLogMapper.selectCount(new LambdaQueryWrapper<SysLog>().eq(SysLog::getStatus,0));
        stats.put("failCount", failCount);
        if (totalCount > 0L) {
            stats.put("successRate", String.format("%.2f%%", (double)successCount * 100.0 / (double)totalCount));
        } else {
            stats.put("successRate", "0%");
        }
        return stats;
    }

    public List<SysLog> getUserLogs(Long userId, int limit) {
        return this.sysLogMapper.selectList(new LambdaQueryWrapper<SysLog>().eq(SysLog::getUserId, userId).orderByDesc(SysLog::getCreateTime).last("LIMIT " + limit));
    }

    public List<SysLog> getRecentLogs(int limit) {
        return this.sysLogMapper.selectList(new LambdaQueryWrapper<SysLog>().orderByDesc(SysLog::getCreateTime).last("LIMIT " + limit));
    }

    public SysLogService(SysLogMapper sysLogMapper) {
        this.sysLogMapper = sysLogMapper;
    }
}

