package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.dto.StudyTaskRequest;
import com.english.learning.entity.StudyTask;
import com.english.learning.mapper.StudyTaskMapper;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyTaskService {
    private static final Logger log = LoggerFactory.getLogger(StudyTaskService.class);
    private final StudyTaskMapper studyTaskMapper;

    public Page<StudyTask> getTaskPage(int pageNum, int pageSize, Long studentId, String status) {
        Page<StudyTask> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<StudyTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyTask::getStudentId, studentId)
               .eq(status != null, StudyTask::getStatus, status)
               .orderByDesc(StudyTask::getCreateTime);
        return this.studyTaskMapper.selectPage(page, wrapper);
    }

    public List<StudyTask> getTasksByPlanId(Long planId, Long studentId) {
        LambdaQueryWrapper<StudyTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyTask::getPlanId, planId)
               .eq(StudyTask::getStudentId, studentId)
               .orderByAsc(StudyTask::getDeadline);
        return this.studyTaskMapper.selectList(wrapper);
    }

    public StudyTask getTaskDetail(Long id) {
        StudyTask task = (StudyTask)this.studyTaskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("\u4efb\u52a1\u4e0d\u5b58\u5728");
        }
        return task;
    }

    @Transactional(rollbackFor={Exception.class})
    public void createTask(Long studentId, StudyTaskRequest request) {
        StudyTask task = new StudyTask();
        task.setStudentId(studentId);
        task.setPlanId(request.getPlanId());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        task.setStatus("PENDING");
        task.setProgress(0);
        task.setDeadline(request.getDeadline());
        int result = this.studyTaskMapper.insert(task);
        if (result <= 0) {
            throw new BusinessException("\u521b\u5efa\u4efb\u52a1\u5931\u8d25");
        }
        log.info("\u5b66\u751f{}\u521b\u5efa\u4e86\u5b66\u4e60\u4efb\u52a1: {}",studentId,request.getTitle());
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateTask(Long id, Long studentId, StudyTaskRequest request) {
        int result;
        StudyTask existingTask = (StudyTask)this.studyTaskMapper.selectById(id);
        if (existingTask == null) {
            throw new BusinessException("\u4efb\u52a1\u4e0d\u5b58\u5728");
        }
        if (!existingTask.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u9650\u4fee\u6539\u6b64\u4efb\u52a1");
        }
        if (request.getTitle() != null) {
            existingTask.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            existingTask.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            existingTask.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            existingTask.setStatus(request.getStatus());
            if ("COMPLETED".equals(request.getStatus())) {
                existingTask.setProgress(100);
                existingTask.setCompletedTime(LocalDateTime.now());
            }
        }
        if (request.getProgress() != null) {
            existingTask.setProgress(request.getProgress());
        }
        if (request.getDeadline() != null) {
            existingTask.setDeadline(request.getDeadline());
        }
        if ((result = this.studyTaskMapper.updateById(existingTask)) <= 0) {
            throw new BusinessException("\u66f4\u65b0\u4efb\u52a1\u5931\u8d25");
        }
        log.info("\u5b66\u751f{}\u66f4\u65b0\u4e86\u5b66\u4e60\u4efb\u52a1: {}",studentId,id);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteTask(Long id, Long studentId) {
        StudyTask existingTask = (StudyTask)this.studyTaskMapper.selectById(id);
        if (existingTask == null) {
            throw new BusinessException("\u4efb\u52a1\u4e0d\u5b58\u5728");
        }
        if (!existingTask.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u9650\u5220\u9664\u6b64\u4efb\u52a1");
        }
        int result = this.studyTaskMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("\u5220\u9664\u4efb\u52a1\u5931\u8d25");
        }
        log.info("\u5b66\u751f{}\u5220\u9664\u4e86\u5b66\u4e60\u4efb\u52a1: {}",studentId,id);
    }

    public Map<String, Object> getTaskStatistics(Long studentId) {
        LambdaQueryWrapper<StudyTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyTask::getStudentId, studentId);
        List<StudyTask> allTasks = this.studyTaskMapper.selectList(wrapper);
        long total = allTasks.size();
        long completed = allTasks.stream().filter(t -> "COMPLETED".equals(t.getStatus())).count();
        long pending = allTasks.stream().filter(t -> "PENDING".equals(t.getStatus()) || "IN_PROGRESS".equals(t.getStatus())).count();
        HashMap<String, Object> stats = new HashMap<String, Object>();
        stats.put("total", total);
        stats.put("completed", completed);
        stats.put("pending", pending);
        return stats;
    }

    @Transactional(rollbackFor={Exception.class})
    public void toggleTaskStatus(Long id, Long studentId) {
        StudyTask task = (StudyTask)this.studyTaskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("\u4efb\u52a1\u4e0d\u5b58\u5728");
        }
        if (!task.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u9650\u64cd\u4f5c\u6b64\u4efb\u52a1");
        }
        if ("COMPLETED".equals(task.getStatus())) {
            task.setStatus("IN_PROGRESS");
            task.setProgress(0);
            task.setCompletedTime(null);
        } else {
            task.setStatus("COMPLETED");
            task.setProgress(100);
            task.setCompletedTime(LocalDateTime.now());
        }
        this.studyTaskMapper.updateById(task);
        log.info("\u5b66\u751f{}\u5207\u6362\u4e86\u4efb\u52a1\u72b6\u6001: {} -> {}", new Object[]{studentId, id, task.getStatus()});
    }

    public StudyTaskService(StudyTaskMapper studyTaskMapper) {
        this.studyTaskMapper = studyTaskMapper;
    }
}

