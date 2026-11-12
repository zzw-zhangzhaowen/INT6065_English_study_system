package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.dto.StudyPlanRequest;
import com.english.learning.entity.StudyPlan;
import com.english.learning.mapper.StudyPlanMapper;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StudyPlanService {
    private static final Logger log = LoggerFactory.getLogger(StudyPlanService.class);
    private final StudyPlanMapper studyPlanMapper;

    public Page<StudyPlan> getPlanPage(int pageNum, int pageSize, Long studentId, String status) {
        Page<StudyPlan> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<StudyPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(studentId != null, StudyPlan::getStudentId,studentId).eq(status != null, StudyPlan::getStatus, status).orderByDesc(StudyPlan::getCreateTime);
        return (Page<StudyPlan>)this.studyPlanMapper.selectPage((IPage<StudyPlan>)page, wrapper);
    }

    public StudyPlan getPlanDetail(Long planId) {
        StudyPlan plan = (StudyPlan)this.studyPlanMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException("\u5b66\u4e60\u8ba1\u5212\u4e0d\u5b58\u5728");
        }
        return plan;
    }

    public void createPlan(Long studentId, StudyPlanRequest request) {
        StudyPlan plan = new StudyPlan();
        plan.setStudentId(studentId);
        plan.setPlanName(request.getPlanName());
        plan.setTargetLevel(request.getTargetLevel());
        plan.setTargetScore(request.getTargetScore());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setDailyDuration(request.getDailyDuration());
        plan.setDailyQuestions(request.getDailyQuestions());
        plan.setProgress(0);
        plan.setStatus("IN_PROGRESS");
        this.studyPlanMapper.insert(plan);
    }

    public void updatePlan(Long planId, Long studentId, StudyPlanRequest request) {
        StudyPlan plan = (StudyPlan)this.studyPlanMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException("\u5b66\u4e60\u8ba1\u5212\u4e0d\u5b58\u5728");
        }
        if (!plan.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u4fee\u6539\u6b64\u8ba1\u5212");
        }
        if (request.getPlanName() != null) {
            plan.setPlanName(request.getPlanName());
        }
        if (request.getTargetLevel() != null) {
            plan.setTargetLevel(request.getTargetLevel());
        }
        if (request.getTargetScore() != null) {
            plan.setTargetScore(request.getTargetScore());
        }
        if (request.getStartDate() != null) {
            plan.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            plan.setEndDate(request.getEndDate());
        }
        if (request.getDailyDuration() != null) {
            plan.setDailyDuration(request.getDailyDuration());
        }
        if (request.getDailyQuestions() != null) {
            plan.setDailyQuestions(request.getDailyQuestions());
        }
        this.studyPlanMapper.updateById(plan);
    }

    public void updateProgress(Long planId, Long studentId, Integer progress) {
        StudyPlan plan = (StudyPlan)this.studyPlanMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException("\u5b66\u4e60\u8ba1\u5212\u4e0d\u5b58\u5728");
        }
        if (!plan.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u4fee\u6539\u6b64\u8ba1\u5212");
        }
        plan.setProgress(progress);
        if (progress >= 100) {
            plan.setStatus("COMPLETED");
        }
        this.studyPlanMapper.updateById(plan);
    }

    public void togglePlanStatus(Long planId, Long studentId) {
        StudyPlan plan = (StudyPlan)this.studyPlanMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException("\u5b66\u4e60\u8ba1\u5212\u4e0d\u5b58\u5728");
        }
        if (!plan.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u4fee\u6539\u6b64\u8ba1\u5212");
        }
        if ("IN_PROGRESS".equals(plan.getStatus())) {
            plan.setStatus("SUSPENDED");
        } else if ("SUSPENDED".equals(plan.getStatus())) {
            plan.setStatus("IN_PROGRESS");
        }
        this.studyPlanMapper.updateById(plan);
    }

    public void deletePlan(Long planId, Long studentId) {
        StudyPlan plan = (StudyPlan)this.studyPlanMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException("\u5b66\u4e60\u8ba1\u5212\u4e0d\u5b58\u5728");
        }
        if (!plan.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u5220\u9664\u6b64\u8ba1\u5212");
        }
        this.studyPlanMapper.deleteById(planId);
    }

    public List<StudyPlan> getTodayTasks(Long studentId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<StudyPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyPlan::getStudentId,studentId).eq(StudyPlan::getStatus, "IN_PROGRESS").le(StudyPlan::getStartDate,today).ge(StudyPlan::getEndDate,today).orderByAsc(StudyPlan::getCreateTime);
        return this.studyPlanMapper.selectList(wrapper);
    }

    public StudyPlanService(StudyPlanMapper studyPlanMapper) {
        this.studyPlanMapper = studyPlanMapper;
    }
}

