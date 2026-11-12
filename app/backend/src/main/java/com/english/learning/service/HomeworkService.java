package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.Homework;
import com.english.learning.entity.HomeworkSubmission;
import com.english.learning.entity.User;
import com.english.learning.mapper.HomeworkMapper;
import com.english.learning.mapper.HomeworkSubmissionMapper;
import com.english.learning.mapper.UserMapper;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HomeworkService {
    private static final Logger log = LoggerFactory.getLogger(HomeworkService.class);
    private final HomeworkMapper homeworkMapper;
    private final HomeworkSubmissionMapper submissionMapper;
    private final UserMapper userMapper;

    @Transactional(rollbackFor={Exception.class})
    public void createHomework(Homework homework, Long teacherId) {
        homework.setTeacherId(teacherId);
        homework.setStatus(1);
        if (homework.getTotalScore() == null) {
            homework.setTotalScore(BigDecimal.valueOf(100L));
        }
        if (homework.getHomeworkType() == null) {
            homework.setHomeworkType("NORMAL");
        }
        this.homeworkMapper.insert(homework);
        log.info("\u6559\u5e08 {} \u521b\u5efa\u4f5c\u4e1a: {}",teacherId,homework.getTitle());
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateHomework(Homework homework, Long teacherId) {
        Homework existing = (Homework)this.homeworkMapper.selectById(homework.getId());
        if (existing == null) {
            throw new BusinessException("\u4f5c\u4e1a\u4e0d\u5b58\u5728");
        }
        if (!existing.getTeacherId().equals(teacherId)) {
            throw new BusinessException("\u65e0\u6743\u4fee\u6539\u6b64\u4f5c\u4e1a");
        }
        this.homeworkMapper.updateById(homework);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteHomework(Long id, Long teacherId) {
        Homework existing = (Homework)this.homeworkMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("\u4f5c\u4e1a\u4e0d\u5b58\u5728");
        }
        if (!existing.getTeacherId().equals(teacherId)) {
            throw new BusinessException("\u65e0\u6743\u5220\u9664\u6b64\u4f5c\u4e1a");
        }
        this.homeworkMapper.deleteById(id);
    }

    public Page<Homework> getTeacherHomeworks(Long teacherId, int pageNum, int pageSize, String keyword) {
        Page<Homework> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<Homework> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Homework::getTeacherId,teacherId).like(keyword != null, Homework::getTitle, keyword).orderByDesc(Homework::getCreateTime);
        return (Page<Homework>)this.homeworkMapper.selectPage(page, wrapper);
    }

    public Page<HomeworkSubmission> getHomeworkSubmissions(Long homeworkId, Long teacherId, int pageNum, int pageSize, String status) {
        Homework homework = (Homework)this.homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new BusinessException("\u4f5c\u4e1a\u4e0d\u5b58\u5728");
        }
        if (!homework.getTeacherId().equals(teacherId)) {
            throw new BusinessException("\u65e0\u6743\u67e5\u770b\u6b64\u4f5c\u4e1a\u63d0\u4ea4\u5217\u8868");
        }
        Page<HomeworkSubmission> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<HomeworkSubmission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkSubmission::getHomeworkId,homeworkId).eq(status != null && !status.isEmpty(), HomeworkSubmission::getStatus, status).orderByDesc(HomeworkSubmission::getSubmitTime);
        return (Page<HomeworkSubmission>)this.submissionMapper.selectPage(page, wrapper);
    }

    @Transactional(rollbackFor={Exception.class})
    public void gradeSubmission(Long submissionId, BigDecimal score, String feedback, Long teacherId) {
        HomeworkSubmission submission = (HomeworkSubmission)this.submissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new BusinessException("\u63d0\u4ea4\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        Homework homework = (Homework)this.homeworkMapper.selectById(submission.getHomeworkId());
        if (homework == null || !homework.getTeacherId().equals(teacherId)) {
            throw new BusinessException("\u65e0\u6743\u6279\u6539\u6b64\u4f5c\u4e1a");
        }
        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setStatus("GRADED");
        this.submissionMapper.updateById(submission);
        log.info("\u6559\u5e08 {} \u6279\u6539\u4f5c\u4e1a\u63d0\u4ea4 {}, \u5206\u6570: {}", new Object[]{teacherId, submissionId, score});
    }

    @Transactional(rollbackFor={Exception.class})
    public void returnSubmission(Long submissionId, String feedback, Long teacherId) {
        HomeworkSubmission submission = (HomeworkSubmission)this.submissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new BusinessException("\u63d0\u4ea4\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        Homework homework = (Homework)this.homeworkMapper.selectById(submission.getHomeworkId());
        if (homework == null || !homework.getTeacherId().equals(teacherId)) {
            throw new BusinessException("\u65e0\u6743\u64cd\u4f5c\u6b64\u4f5c\u4e1a");
        }
        submission.setFeedback(feedback);
        submission.setStatus("RETURNED");
        this.submissionMapper.updateById(submission);
    }

    public long getPendingGradeCount(Long teacherId) {
        List<Homework> homeworks = this.homeworkMapper.selectList(new LambdaQueryWrapper<Homework>().eq(Homework::getTeacherId,teacherId));
        if (homeworks.isEmpty()) {
            return 0L;
        }
        List<Long> homeworkIds = homeworks.stream().map(Homework::getId).toList();
        return this.submissionMapper.selectCount(new LambdaQueryWrapper<HomeworkSubmission>().in(HomeworkSubmission::getHomeworkId, homeworkIds).eq(HomeworkSubmission::getStatus, "SUBMITTED"));
    }

    public Page<Homework> getStudentHomeworks(Long studentId, int pageNum, int pageSize, String level, String status, String keyword) {
        LambdaQueryWrapper<Homework> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Homework::getStatus,1).eq(level != null, Homework::getLevel,level).like(keyword != null && !keyword.isEmpty(), Homework::getTitle, keyword).orderByDesc(Homework::getCreateTime);
        List<Homework> homeworkList = this.homeworkMapper.selectList(wrapper);
        for (Homework homework2 : homeworkList) {
            User teacher;
            HomeworkSubmission submission = (HomeworkSubmission)this.submissionMapper.selectOne(new LambdaQueryWrapper<HomeworkSubmission>().eq(HomeworkSubmission::getHomeworkId,homework2.getId()).eq(HomeworkSubmission::getStudentId,studentId));
            if (submission != null) {
                homework2.setSubmitted(Boolean.valueOf(true));
                homework2.setSubmissionStatus(submission.getStatus());
                homework2.setScore(submission.getScore());
            } else {
                homework2.setSubmitted(Boolean.valueOf(false));
                homework2.setSubmissionStatus(null);
                homework2.setScore(null);
            }
            if ((teacher = (User)this.userMapper.selectById(homework2.getTeacherId())) == null) continue;
            homework2.setTeacherName(teacher.getRealName());
        }
        List<Homework> filteredList = homeworkList;
        if (status != null && !status.isEmpty()) {
            filteredList = homeworkList.stream().filter(homework -> {
                switch (status) {
                    case "pending": {
                        return homework.getSubmitted() == false || "PENDING".equals(homework.getSubmissionStatus());
                    }
                    case "submitted": {
                        return homework.getSubmitted() != false && "SUBMITTED".equals(homework.getSubmissionStatus());
                    }
                    case "graded": {
                        return homework.getSubmitted() != false && "GRADED".equals(homework.getSubmissionStatus());
                    }
                }
                return true;
            }).collect(Collectors.toList());
        }
        Page<Homework> resultPage = new Page<>((long)pageNum, (long)pageSize);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, filteredList.size());
        if (start < filteredList.size()) {
            resultPage.setRecords(filteredList.subList(start, end));
        } else {
            resultPage.setRecords(List.of());
        }
        resultPage.setTotal((long)filteredList.size());
        return resultPage;
    }

    @Transactional(rollbackFor={Exception.class})
    public void submitHomework(Long homeworkId, String content, String attachmentUrl, Long studentId) {
        Homework homework = (Homework)this.homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new BusinessException("\u4f5c\u4e1a\u4e0d\u5b58\u5728");
        }
        if (homework.getDeadline() != null && LocalDateTime.now().isAfter(homework.getDeadline())) {
            throw new BusinessException("\u4f5c\u4e1a\u5df2\u8fc7\u622a\u6b62\u65f6\u95f4");
        }
        HomeworkSubmission existing = (HomeworkSubmission)this.submissionMapper.selectOne(new LambdaQueryWrapper<HomeworkSubmission>().eq(HomeworkSubmission::getHomeworkId,homeworkId).eq(HomeworkSubmission::getStudentId,studentId));
        if (existing != null) {
            existing.setContent(content);
            existing.setAttachmentUrl(attachmentUrl);
            existing.setSubmitTime(LocalDateTime.now());
            existing.setStatus("SUBMITTED");
            this.submissionMapper.updateById(existing);
        } else {
            HomeworkSubmission submission = new HomeworkSubmission();
            submission.setHomeworkId(homeworkId);
            submission.setStudentId(studentId);
            submission.setContent(content);
            submission.setAttachmentUrl(attachmentUrl);
            submission.setSubmitTime(LocalDateTime.now());
            submission.setStatus("SUBMITTED");
            this.submissionMapper.insert(submission);
        }
        log.info("\u5b66\u751f {} \u63d0\u4ea4\u4f5c\u4e1a {}",studentId,homeworkId);
    }

    public List<HomeworkSubmission> getStudentSubmissions(Long studentId) {
        return this.submissionMapper.selectList(new LambdaQueryWrapper<HomeworkSubmission>().eq(HomeworkSubmission::getStudentId,studentId).orderByDesc(HomeworkSubmission::getSubmitTime));
    }

    public HomeworkSubmission getStudentSubmission(Long homeworkId, Long studentId) {
        return (HomeworkSubmission)this.submissionMapper.selectOne(new LambdaQueryWrapper<HomeworkSubmission>().eq(HomeworkSubmission::getHomeworkId,homeworkId).eq(HomeworkSubmission::getStudentId,studentId));
    }

    public Homework getHomeworkById(Long id) {
        Homework homework = (Homework)this.homeworkMapper.selectById(id);
        if (homework == null) {
            throw new BusinessException("\u4f5c\u4e1a\u4e0d\u5b58\u5728");
        }
        return homework;
    }

    public Map<String, Object> getHomeworkStats(Long homeworkId) {
        HashMap<String, Object> stats = new HashMap<String, Object>();
        long totalSubmissions = this.submissionMapper.selectCount(new LambdaQueryWrapper<HomeworkSubmission>().eq(HomeworkSubmission::getHomeworkId,homeworkId));
        stats.put("totalSubmissions", totalSubmissions);
        long gradedCount = this.submissionMapper.selectCount(new LambdaQueryWrapper<HomeworkSubmission>().eq(HomeworkSubmission::getHomeworkId,homeworkId).eq(HomeworkSubmission::getStatus, "GRADED"));
        stats.put("gradedCount", gradedCount);
        long pendingCount = this.submissionMapper.selectCount(new LambdaQueryWrapper<HomeworkSubmission>().eq(HomeworkSubmission::getHomeworkId,homeworkId).eq(HomeworkSubmission::getStatus, "SUBMITTED"));
        stats.put("pendingCount", pendingCount);
        List<HomeworkSubmission> gradedSubmissions = this.submissionMapper.selectList(new LambdaQueryWrapper<HomeworkSubmission>().eq(HomeworkSubmission::getHomeworkId,homeworkId).eq(HomeworkSubmission::getStatus, "GRADED").isNotNull(HomeworkSubmission::getScore));
        if (!gradedSubmissions.isEmpty()) {
            double avgScore = gradedSubmissions.stream().mapToDouble(s -> s.getScore().doubleValue()).average().orElse(0.0);
            stats.put("averageScore", BigDecimal.valueOf(avgScore).setScale(2, RoundingMode.HALF_UP));
        } else {
            stats.put("averageScore", 0);
        }
        return stats;
    }

    public Page<Map<String, Object>> getPendingHomeworkList(Long teacherId, int pageNum, int pageSize) {
        List<Homework> homeworks = this.homeworkMapper.selectList(new LambdaQueryWrapper<Homework>().eq(Homework::getTeacherId,teacherId).orderByDesc(Homework::getCreateTime));
        List<Map<String, Object>> result = homeworks.stream().map(homework -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", homework.getId());
            item.put("title", homework.getTitle());
            item.put("className", homework.getLevel());
            item.put("deadline", homework.getDeadline());
            long totalCount = this.submissionMapper.selectCount(new LambdaQueryWrapper<HomeworkSubmission>().eq(HomeworkSubmission::getHomeworkId,homework.getId()));
            long submittedCount = this.submissionMapper.selectCount(new LambdaQueryWrapper<HomeworkSubmission>().eq(HomeworkSubmission::getHomeworkId,homework.getId()).eq(HomeworkSubmission::getStatus, "SUBMITTED"));
            item.put("totalCount", totalCount);
            item.put("submittedCount", submittedCount);
            return item;
        }).filter(item -> (Long)item.get("submittedCount") > 0L).collect(Collectors.toList());
        Page<Map<String, Object>> page = new Page<>((long)pageNum, (long)pageSize);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, result.size());
        if (start < result.size()) {
            page.setRecords(result.subList(start, end));
        }
        page.setTotal((long)result.size());
        return page;
    }

    public Page<Map<String, Object>> getPendingHomeworks(Long teacherId, int pageNum, int pageSize) {
        return this.getPendingHomeworkList(teacherId, pageNum, pageSize);
    }

    public HomeworkService(HomeworkMapper homeworkMapper, HomeworkSubmissionMapper submissionMapper, UserMapper userMapper) {
        this.homeworkMapper = homeworkMapper;
        this.submissionMapper = submissionMapper;
        this.userMapper = userMapper;
    }
}

