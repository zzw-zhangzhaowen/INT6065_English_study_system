package com.english.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.ClassInfo;
import com.english.learning.entity.ClassStudent;
import com.english.learning.entity.ExamRecord;
import com.english.learning.entity.StudentInfo;
import com.english.learning.entity.StudyNote;
import com.english.learning.entity.TeacherClass;
import com.english.learning.entity.User;
import com.english.learning.entity.WrongQuestion;
import com.english.learning.mapper.ClassInfoMapper;
import com.english.learning.mapper.ClassStudentMapper;
import com.english.learning.mapper.ExamRecordMapper;
import com.english.learning.mapper.RoleMapper;
import com.english.learning.mapper.StudentInfoMapper;
import com.english.learning.mapper.StudyNoteMapper;
import com.english.learning.mapper.TeacherClassMapper;
import com.english.learning.mapper.UserMapper;
import com.english.learning.mapper.UserRoleMapper;
import com.english.learning.mapper.WrongQuestionMapper;
import com.english.learning.service.TeacherService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final UserMapper userMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final ExamRecordMapper examRecordMapper;
    private final ClassInfoMapper classInfoMapper;
    private final TeacherClassMapper teacherClassMapper;
    private final ClassStudentMapper classStudentMapper;
    private final WrongQuestionMapper wrongQuestionMapper;
    private final StudyNoteMapper studyNoteMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public Page<Map<String, Object>> getStudentPage(Long teacherId, int pageNum, int pageSize, String keyword, Long classId) {
        List<Long> studentIds = this.getTeacherStudentIds(teacherId, classId);
        Page<Map<String, Object>> page = new Page<>((long)pageNum, (long)pageSize);
        if (studentIds.isEmpty()) {
            page.setRecords(new ArrayList<>());
            page.setTotal(0L);
            return page;
        }
        Page<User> userPage = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getId, studentIds).eq(User::getStatus, 1);
        if (keyword != null && !keyword.trim().isEmpty()) {
            userWrapper.and(w -> w.like(User::getRealName, keyword).or().like(User::getUsername, keyword));
        }
        userWrapper.orderByDesc(User::getCreateTime);
        userPage = (Page<User>) this.userMapper.selectPage((IPage<User>) userPage, userWrapper);
        List<Map<String, Object>> records = new ArrayList<>();
        for (User user : userPage.getRecords()) {
            HashMap<String, Object> record = new HashMap<>();
            record.put("id", user.getId());
            record.put("username", user.getUsername());
            record.put("realName", user.getRealName());
            record.put("email", user.getEmail());
            record.put("avatar", user.getAvatar());
            record.put("lastLoginTime", user.getLastLoginTime());
            StudentInfo studentInfo = this.studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>().eq(StudentInfo::getUserId, user.getId()));
            if (studentInfo != null) {
                record.put("studentNo", studentInfo.getStudentNo());
                record.put("studyMinutes", studentInfo.getTotalStudyTime());
                record.put("grade", studentInfo.getGrade());
            } else {
                record.put("studentNo", "");
                record.put("studyMinutes", 0);
                record.put("grade", "");
            }
            ClassStudent cs = this.classStudentMapper.selectOne(new LambdaQueryWrapper<ClassStudent>().eq(ClassStudent::getStudentId, user.getId()).last("LIMIT 1"));
            if (cs != null) {
                ClassInfo ci = this.classInfoMapper.selectById(cs.getClassId());
                record.put("className", ci != null ? ci.getClassName() : "-");
            } else {
                record.put("className", "-");
            }
            List<ExamRecord> examRecords = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId, user.getId()).eq(ExamRecord::getStatus, "COMPLETED"));
            record.put("examCount", examRecords.size());
            if (!examRecords.isEmpty()) {
                double avgScore = examRecords.stream().filter(e -> e.getObtainedScore() != null).mapToDouble(e -> e.getObtainedScore().doubleValue()).average().orElse(0.0);
                record.put("avgScore", BigDecimal.valueOf(avgScore).setScale(1, RoundingMode.HALF_UP));
            } else {
                record.put("avgScore", 0);
            }
            records.add(record);
        }
        page.setRecords(records);
        page.setTotal(userPage.getTotal());
        return page;
    }

    public Map<String, Object> getStudentDetail(Long teacherId, Long studentId) {
        this.validateTeacherStudentAccess(teacherId, studentId);
        HashMap<String, Object> detail = new HashMap<>();
        User user = this.userMapper.selectById(studentId);
        if (user == null) {
            return detail;
        }
        detail.put("id", user.getId());
        detail.put("username", user.getUsername());
        detail.put("realName", user.getRealName());
        detail.put("email", user.getEmail());
        detail.put("avatar", user.getAvatar());
        detail.put("lastLoginTime", user.getLastLoginTime());
        StudentInfo studentInfo = this.studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>().eq(StudentInfo::getUserId, studentId));
        if (studentInfo != null) {
            detail.put("studentNo", studentInfo.getStudentNo());
            detail.put("school", studentInfo.getSchool());
            detail.put("major", studentInfo.getMajor());
            detail.put("grade", studentInfo.getGrade());
            detail.put("currentLevel", studentInfo.getCurrentLevel());
            detail.put("targetLevel", studentInfo.getTargetLevel());
            detail.put("studyDays", studentInfo.getStudyDays());
            detail.put("continuousDays", studentInfo.getContinuousDays());
            detail.put("totalStudyTime", studentInfo.getTotalStudyTime());
        }
        HashMap<String, Number> stats = new HashMap<>();
        List<ExamRecord> examRecords = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId, studentId).eq(ExamRecord::getStatus, "COMPLETED"));
        stats.put("totalExams", examRecords.size());
        if (!examRecords.isEmpty()) {
            double avgScore = examRecords.stream().filter(e -> e.getObtainedScore() != null).mapToDouble(e -> e.getObtainedScore().doubleValue()).average().orElse(0.0);
            stats.put("avgScore", BigDecimal.valueOf(avgScore).setScale(1, RoundingMode.HALF_UP));
        } else {
            stats.put("avgScore", 0);
        }
        stats.put("totalStudyTime", studentInfo != null && studentInfo.getTotalStudyTime() != null ? studentInfo.getTotalStudyTime() : 0);
        Long wrongCount = this.wrongQuestionMapper.selectCount(new LambdaQueryWrapper<WrongQuestion>().eq(WrongQuestion::getStudentId, studentId));
        stats.put("wrongQuestionCount", wrongCount);
        Long noteCount = this.studyNoteMapper.selectCount(new LambdaQueryWrapper<StudyNote>().eq(StudyNote::getStudentId, studentId));
        stats.put("noteCount", noteCount);
        detail.put("stats", stats);
        List<ExamRecord> recentExams = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId, studentId).orderByDesc(ExamRecord::getCreateTime).last("LIMIT 10"));
        ArrayList<Map<String, Object>> recentExamList = new ArrayList<>();
        for (ExamRecord er : recentExams) {
            HashMap<String, Object> examMap = new HashMap<>();
            examMap.put("id", er.getId());
            examMap.put("score", er.getObtainedScore());
            examMap.put("totalScore", er.getTotalScore());
            examMap.put("status", er.getStatus());
            examMap.put("submitTime", er.getSubmitTime());
            examMap.put("duration", er.getDuration());
            recentExamList.add(examMap);
        }
        detail.put("recentExams", recentExamList);
        return detail;
    }

    public List<Map<String, Object>> getLearningAlerts(Long teacherId, int days) {
        ArrayList<Map<String, Object>> alerts = new ArrayList<>();
        List<Long> studentIds = this.getTeacherStudentIds(teacherId, null);
        if (studentIds.isEmpty()) {
            return alerts;
        }
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        List<User> inactiveStudents = this.userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, studentIds).eq(User::getStatus, 1).and(w -> w.isNull(User::getLastLoginTime).or().lt(User::getLastLoginTime, threshold)));
        for (User user : inactiveStudents) {
            HashMap<String, Object> alert = new HashMap<>();
            alert.put("id", user.getId());
            alert.put("realName", user.getRealName());
            alert.put("username", user.getUsername());
            alert.put("alertType", "INACTIVE");
            alert.put("message", "\u8fde\u7eed" + days + "\u5929\u672a\u767b\u5f55");
            alert.put("lastLoginTime", user.getLastLoginTime());
            alerts.add(alert);
        }
        for (Long sid : studentIds) {
            List<ExamRecord> recentRecords = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId, sid).eq(ExamRecord::getStatus, "COMPLETED").ge(ExamRecord::getSubmitTime, threshold).orderByDesc(ExamRecord::getSubmitTime).last("LIMIT 5"));
            if (recentRecords.size() < 2) continue;
            double avg = recentRecords.stream().filter(r -> r.getObtainedScore() != null && r.getTotalScore() != null && r.getTotalScore().doubleValue() > 0.0).mapToDouble(r -> r.getObtainedScore().doubleValue() / r.getTotalScore().doubleValue() * 100.0).average().orElse(100.0);
            if (avg >= 60.0) continue;
            boolean alreadyAlerted = alerts.stream().anyMatch(a -> sid.equals(a.get("id")));
            if (alreadyAlerted) continue;
            User user = this.userMapper.selectById(sid);
            if (user == null) continue;
            HashMap<String, Object> alert = new HashMap<>();
            alert.put("id", user.getId());
            alert.put("realName", user.getRealName());
            alert.put("username", user.getUsername());
            alert.put("alertType", "LOW_SCORE");
            alert.put("message", "\u8fd1\u671f\u8003\u8bd5\u5e73\u5747\u5206\u4f4e\u4e8e60\u5206");
            alerts.add(alert);
        }
        return alerts;
    }

    public Map<String, Object> getScoreTrend(Long teacherId, Long classId, int days) {
        HashMap<String, Object> trend = new HashMap<>();
        List<Long> studentIds = this.getTeacherStudentIds(teacherId, classId);
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Double> avgScores = new ArrayList<>();
        ArrayList<Integer> examCounts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = days - 1; i >= 0; --i) {
            LocalDateTime dayStart = now.minusDays(i).toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1L);
            dates.add(dayStart.toLocalDate().toString());
            if (studentIds.isEmpty()) {
                avgScores.add(0.0);
                examCounts.add(0);
                continue;
            }
            List<ExamRecord> dayRecords = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().in(ExamRecord::getStudentId, studentIds).eq(ExamRecord::getStatus, "COMPLETED").ge(ExamRecord::getSubmitTime, dayStart).lt(ExamRecord::getSubmitTime, dayEnd));
            examCounts.add(dayRecords.size());
            if (!dayRecords.isEmpty()) {
                double avg = dayRecords.stream().filter(e -> e.getObtainedScore() != null).mapToDouble(e -> e.getObtainedScore().doubleValue()).average().orElse(0.0);
                avgScores.add(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP).doubleValue());
            } else {
                avgScores.add(0.0);
            }
        }
        trend.put("dates", dates);
        trend.put("avgScores", avgScores);
        trend.put("examCounts", examCounts);
        return trend;
    }

    public List<Map<String, Object>> getClassList(Long teacherId) {
        ArrayList<Map<String, Object>> classes = new ArrayList<>();
        List<TeacherClass> teacherClasses = this.teacherClassMapper.selectList(new LambdaQueryWrapper<TeacherClass>().eq(TeacherClass::getTeacherId, teacherId));
        if (teacherClasses.isEmpty()) {
            return classes;
        }
        List<Long> classIds = teacherClasses.stream().map(TeacherClass::getClassId).collect(Collectors.toList());
        List<ClassInfo> classInfos = this.classInfoMapper.selectList(new LambdaQueryWrapper<ClassInfo>().in(ClassInfo::getId, classIds).eq(ClassInfo::getStatus, 1));
        for (ClassInfo ci : classInfos) {
            HashMap<String, Object> classMap = new HashMap<>();
            classMap.put("id", ci.getId());
            classMap.put("name", ci.getClassName());
            Long studentCount = this.classStudentMapper.selectCount(new LambdaQueryWrapper<ClassStudent>().eq(ClassStudent::getClassId, ci.getId()));
            classMap.put("studentCount", studentCount);
            classes.add(classMap);
        }
        return classes;
    }

    public Map<String, Object> getStudentReport(Long teacherId, Long studentId) {
        this.validateTeacherStudentAccess(teacherId, studentId);
        HashMap<String, Object> report = new HashMap<>();
        StudentInfo studentInfo = this.studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>().eq(StudentInfo::getUserId, studentId));
        report.put("totalStudyDays", studentInfo != null && studentInfo.getStudyDays() != null ? studentInfo.getStudyDays() : 0);
        int totalStudyTime = studentInfo != null && studentInfo.getTotalStudyTime() != null ? studentInfo.getTotalStudyTime() : 0;
        report.put("totalStudyTime", totalStudyTime);
        int studyDays = studentInfo != null && studentInfo.getStudyDays() != null && studentInfo.getStudyDays() > 0 ? studentInfo.getStudyDays() : 1;
        report.put("avgDailyStudyTime", totalStudyTime / studyDays);
        List<ExamRecord> examRecords = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId, studentId).eq(ExamRecord::getStatus, "COMPLETED"));
        report.put("totalExams", examRecords.size());
        if (!examRecords.isEmpty()) {
            List<Double> scores = examRecords.stream().filter(e -> e.getObtainedScore() != null).map(e -> e.getObtainedScore().doubleValue()).collect(Collectors.toList());
            long passedCount = scores.stream().filter(s -> s >= 60.0).count();
            report.put("passedExams", passedCount);
            report.put("avgScore", BigDecimal.valueOf(scores.stream().mapToDouble(d -> d).average().orElse(0.0)).setScale(1, RoundingMode.HALF_UP));
            report.put("highestScore", scores.stream().mapToDouble(d -> d).max().orElse(0.0));
            report.put("lowestScore", scores.stream().mapToDouble(d -> d).min().orElse(0.0));
        } else {
            report.put("passedExams", 0);
            report.put("avgScore", 0);
            report.put("highestScore", 0);
            report.put("lowestScore", 0);
        }
        return report;
    }

    public Map<String, Object> getClassStats(Long teacherId, Long classId) {
        Long relationCount = this.teacherClassMapper.selectCount(new LambdaQueryWrapper<TeacherClass>().eq(TeacherClass::getTeacherId, teacherId).eq(TeacherClass::getClassId, classId));
        if (relationCount == null || relationCount == 0L) {
            throw new BusinessException("\u65e0\u6743\u67e5\u770b\u8be5\u73ed\u7ea7\u7edf\u8ba1");
        }
        HashMap<String, Object> stats = new HashMap<>();
        List<ClassStudent> classStudents = this.classStudentMapper.selectList(new LambdaQueryWrapper<ClassStudent>().eq(ClassStudent::getClassId, classId));
        List<Long> studentIds = classStudents.stream().map(ClassStudent::getStudentId).collect(Collectors.toList());
        stats.put("studentCount", studentIds.size());
        if (studentIds.isEmpty()) {
            stats.put("avgScore", 0);
            stats.put("passRate", 0);
            stats.put("excellentRate", 0);
            stats.put("totalExams", 0);
            stats.put("scoreDistribution", new HashMap<>());
            return stats;
        }
        List<ExamRecord> allRecords = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().in(ExamRecord::getStudentId, studentIds).eq(ExamRecord::getStatus, "COMPLETED"));
        stats.put("totalExams", allRecords.size());
        if (!allRecords.isEmpty()) {
            List<Double> scores = allRecords.stream().filter(e -> e.getObtainedScore() != null).map(e -> e.getObtainedScore().doubleValue()).collect(Collectors.toList());
            double avgScore = scores.stream().mapToDouble(d -> d).average().orElse(0.0);
            stats.put("avgScore", BigDecimal.valueOf(avgScore).setScale(1, RoundingMode.HALF_UP));
            long passCount = scores.stream().filter(s -> s >= 60.0).count();
            long excellentCount = scores.stream().filter(s -> s >= 85.0).count();
            stats.put("passRate", scores.size() > 0 ? BigDecimal.valueOf((double) passCount * 100.0 / (double) scores.size()).setScale(1, RoundingMode.HALF_UP) : 0);
            stats.put("excellentRate", scores.size() > 0 ? BigDecimal.valueOf((double) excellentCount * 100.0 / (double) scores.size()).setScale(1, RoundingMode.HALF_UP) : 0);
            LinkedHashMap<String, Integer> scoreDistribution = new LinkedHashMap<>();
            scoreDistribution.put("0-59", (int) scores.stream().filter(s -> s < 60.0).count());
            scoreDistribution.put("60-69", (int) scores.stream().filter(s -> s >= 60.0 && s < 70.0).count());
            scoreDistribution.put("70-79", (int) scores.stream().filter(s -> s >= 70.0 && s < 80.0).count());
            scoreDistribution.put("80-89", (int) scores.stream().filter(s -> s >= 80.0 && s < 90.0).count());
            scoreDistribution.put("90-100", (int) scores.stream().filter(s -> s >= 90.0).count());
            stats.put("scoreDistribution", scoreDistribution);
        } else {
            stats.put("avgScore", 0);
            stats.put("passRate", 0);
            stats.put("excellentRate", 0);
            stats.put("scoreDistribution", new HashMap<>());
        }
        return stats;
    }

    private void validateTeacherStudentAccess(Long teacherId, Long studentId) {
        if (teacherId == null || studentId == null) {
            throw new BusinessException("\u53c2\u6570\u9519\u8bef");
        }
        List<Long> teacherStudentIds = this.getTeacherStudentIds(teacherId, null);
        if (!teacherStudentIds.contains(studentId)) {
            throw new BusinessException("\u65e0\u6743\u67e5\u770b\u8be5\u5b66\u751f\u4fe1\u606f");
        }
    }

    private List<Long> getTeacherStudentIds(Long teacherId, Long classId) {
        LambdaQueryWrapper<TeacherClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeacherClass::getTeacherId, teacherId);
        if (classId != null) {
            tcWrapper.eq(TeacherClass::getClassId, classId);
        }
        List<TeacherClass> teacherClasses = this.teacherClassMapper.selectList(tcWrapper);
        if (teacherClasses.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> classIds = teacherClasses.stream().map(TeacherClass::getClassId).collect(Collectors.toList());
        List<ClassStudent> classStudents = this.classStudentMapper.selectList(new LambdaQueryWrapper<ClassStudent>().in(ClassStudent::getClassId, classIds));
        return classStudents.stream().map(ClassStudent::getStudentId).distinct().collect(Collectors.toList());
    }

    public TeacherServiceImpl(UserMapper userMapper, StudentInfoMapper studentInfoMapper, ExamRecordMapper examRecordMapper, ClassInfoMapper classInfoMapper, TeacherClassMapper teacherClassMapper, ClassStudentMapper classStudentMapper, WrongQuestionMapper wrongQuestionMapper, StudyNoteMapper studyNoteMapper, RoleMapper roleMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.studentInfoMapper = studentInfoMapper;
        this.examRecordMapper = examRecordMapper;
        this.classInfoMapper = classInfoMapper;
        this.teacherClassMapper = teacherClassMapper;
        this.classStudentMapper = classStudentMapper;
        this.wrongQuestionMapper = wrongQuestionMapper;
        this.studyNoteMapper = studyNoteMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }
}
