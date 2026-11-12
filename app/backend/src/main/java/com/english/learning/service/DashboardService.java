package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.english.learning.entity.CommunityPost;
import com.english.learning.entity.Exam;
import com.english.learning.entity.ExamRecord;
import com.english.learning.entity.Question;
import com.english.learning.entity.Role;
import com.english.learning.entity.StudentInfo;
import com.english.learning.entity.StudyPlan;
import com.english.learning.entity.User;
import com.english.learning.entity.UserRole;
import com.english.learning.entity.WrongQuestion;
import com.english.learning.mapper.CommunityPostMapper;
import com.english.learning.mapper.ExamMapper;
import com.english.learning.mapper.ExamRecordMapper;
import com.english.learning.mapper.QuestionMapper;
import com.english.learning.mapper.RoleMapper;
import com.english.learning.mapper.StudentInfoMapper;
import com.english.learning.mapper.StudyPlanMapper;
import com.english.learning.mapper.UserMapper;
import com.english.learning.mapper.UserRoleMapper;
import com.english.learning.mapper.WrongQuestionMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);
    private final UserMapper userMapper;
    private final ExamRecordMapper examRecordMapper;
    private final ExamMapper examMapper;
    private final QuestionMapper questionMapper;
    private final WrongQuestionMapper wrongQuestionMapper;
    private final StudyPlanMapper studyPlanMapper;
    private final CommunityPostMapper communityPostMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public Map<String, Object> getStudentDashboard(Long userId) {
        log.info("[Dashboard] \u5f00\u59cb\u67e5\u8be2\u5b66\u751f\u4eea\u8868\u76d8\u6570\u636e, userId={}",userId);
        HashMap<String, Object> result = new HashMap<String, Object>();
        StudentInfo studentInfo = (StudentInfo)this.studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>().eq(StudentInfo::getUserId, userId));
        log.info("[Dashboard] \u67e5\u8be2student_info\u7ed3\u679c: {}",studentInfo);
        if (studentInfo != null) {
            result.put("studyDays", studentInfo.getStudyDays());
            result.put("continuousDays", studentInfo.getContinuousDays());
            result.put("totalStudyTime", studentInfo.getTotalStudyTime());
            result.put("achievementScore", studentInfo.getAchievementScore());
            result.put("currentLevel", studentInfo.getCurrentLevel());
            result.put("targetLevel", studentInfo.getTargetLevel());
            result.put("studyTime", studentInfo.getTotalStudyTime() / 60);
            result.put("streak", studentInfo.getContinuousDays());
        } else {
            result.put("studyDays", 0);
            result.put("continuousDays", 0);
            result.put("totalStudyTime", 0);
            result.put("achievementScore", 0);
            result.put("studyTime", 0);
            result.put("streak", 0);
        }
        long examCount = this.examRecordMapper.selectCount(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId, userId).eq(ExamRecord::getStatus, "COMPLETED"));
        log.info("[Dashboard] \u5b8c\u6210\u8003\u8bd5\u6570\u91cf: {}",examCount);
        result.put("examCount", examCount);
        List<ExamRecord> examRecords = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId, userId).eq(ExamRecord::getStatus, "COMPLETED"));
        if (!examRecords.isEmpty()) {
            BigDecimal totalScore = examRecords.stream().map(ExamRecord::getObtainedScore).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avgScore = totalScore.divide(BigDecimal.valueOf(examRecords.size()), 2, RoundingMode.HALF_UP);
            result.put("avgScore", avgScore);
            int totalCorrect = examRecords.stream().mapToInt(r -> r.getCorrectCount() != null ? r.getCorrectCount() : 0).sum();
            int totalQuestions = examRecords.stream().mapToInt(r -> {
                int correct = r.getCorrectCount() != null ? r.getCorrectCount() : 0;
                int wrong = r.getWrongCount() != null ? r.getWrongCount() : 0;
                return correct + wrong;
            }).sum();
            int correctRate = totalQuestions > 0 ? totalCorrect * 100 / totalQuestions : 0;
            log.info("[Dashboard] \u5e73\u5747\u6b63\u786e\u7387: {}%, \u603b\u6b63\u786e\u6570: {}/{}", new Object[]{correctRate, totalCorrect, totalQuestions});
            result.put("correctRate", correctRate);
        } else {
            log.info("[Dashboard] \u65e0\u8003\u8bd5\u8bb0\u5f55\uff0c\u8bbe\u7f6e\u9ed8\u8ba4\u503c");
            result.put("avgScore", 0);
            result.put("correctRate", 0);
        }
        long wrongQuestionCount = this.wrongQuestionMapper.selectCount(new LambdaQueryWrapper<WrongQuestion>().eq(WrongQuestion::getStudentId, userId));
        result.put("wrongQuestionCount", wrongQuestionCount);
        long notMasteredCount = this.wrongQuestionMapper.selectCount(new LambdaQueryWrapper<WrongQuestion>().eq(WrongQuestion::getStudentId, userId).eq(WrongQuestion::getMasteryLevel,0));
        result.put("notMasteredCount", notMasteredCount);
        long activePlanCount = this.studyPlanMapper.selectCount(new LambdaQueryWrapper<StudyPlan>().eq(StudyPlan::getStudentId, userId).eq(StudyPlan::getStatus, "IN_PROGRESS"));
        result.put("activePlanCount", activePlanCount);
        log.info("[Dashboard] \u6700\u7ec8\u8fd4\u56de\u6570\u636e: studyTime={}, examCount={}, correctRate={}, streak={}", new Object[]{result.get("studyTime"), result.get("examCount"), result.get("correctRate"), result.get("streak")});
        return result;
    }

    public Map<String, Object> getAdminDashboard() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        long totalUsers = this.userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getDeleted,0));
        result.put("totalUsers", totalUsers);
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayNewUsers = this.userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getDeleted,0).ge(User::getCreateTime,todayStart));
        result.put("todayNewUsers", todayNewUsers);
        long todayActiveUsers = this.userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getDeleted,0).ge(User::getUpdateTime,todayStart));
        result.put("onlineUsers", todayActiveUsers);
        long totalQuestions = this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getDeleted,0));
        result.put("totalQuestions", totalQuestions);
        long totalExamRecords = this.examRecordMapper.selectCount(new LambdaQueryWrapper<ExamRecord>());
        result.put("totalExamRecords", totalExamRecords);
        long completedExams = this.examRecordMapper.selectCount(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStatus, "COMPLETED"));
        result.put("completedExams", completedExams);
        LocalDateTime now = LocalDateTime.now();
        long ongoingExams = this.examMapper.selectCount(new LambdaQueryWrapper<Exam>().eq(Exam::getDeleted,0).le(Exam::getStartTime,now).ge(Exam::getEndTime,now));
        result.put("ongoingExams", ongoingExams);
        long totalPosts = this.communityPostMapper.selectCount(new LambdaQueryWrapper<CommunityPost>().eq(CommunityPost::getDeleted,0));
        result.put("totalPosts", totalPosts);
        result.put("storageUsed", 2.5);
        result.put("storageTotal", 10.0);
        HashMap<String, Long> roleDistribution = new HashMap<String, Long>();
        List<Role> roles = this.roleMapper.selectList(new LambdaQueryWrapper<Role>());
        for (Role role : roles) {
            List<UserRole> userRoles = this.userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId,role.getId()));
            if (userRoles.isEmpty()) continue;
            List<Long> userIds = userRoles.stream().map(UserRole::getUserId).collect(Collectors.toList());
            long count = this.userMapper.selectCount(new LambdaQueryWrapper<User>().in(User::getId, userIds).eq(User::getDeleted,0));
            String roleName = switch (role.getRoleCode()) {
                case "ROLE_ADMIN" -> "\u7ba1\u7406\u5458";
                case "ROLE_TEACHER" -> "\u6559\u5e08";
                case "ROLE_STUDENT" -> "\u5b66\u751f";
                default -> role.getRoleName();
            };
            roleDistribution.put(roleName, count);
        }
        result.put("roleDistribution", roleDistribution);
        return result;
    }

    public Map<String, Object> getStudyTrend(Long userId, int days) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        int safeDays = Math.max(days, 1);
        LocalDate startDay = LocalDate.now().minusDays(safeDays - 1);
        LocalDateTime startDate = startDay.atStartOfDay();
        List<ExamRecord> examRecords = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId, userId).in(ExamRecord::getStatus, new Object[]{"COMPLETED", "completed"}).ge(ExamRecord::getCreateTime,startDate).orderByAsc(ExamRecord::getCreateTime));
        LinkedHashMap<String, Integer> dailyMinutes = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < safeDays; ++i) {
            LocalDate day = startDay.plusDays(i);
            dailyMinutes.put(day.toString(), 0);
        }
        for (ExamRecord record : (List<ExamRecord>)examRecords) {
            String key;
            LocalDate recordDay = null;
            if (record.getSubmitTime() != null) {
                recordDay = record.getSubmitTime().toLocalDate();
            } else if (record.getEndTime() != null) {
                recordDay = record.getEndTime().toLocalDate();
            } else if (record.getCreateTime() != null) {
                recordDay = record.getCreateTime().toLocalDate();
            }
            if (recordDay == null || recordDay.isBefore(startDay) || !dailyMinutes.containsKey(key = recordDay.toString())) continue;
            int durationMinutes = 0;
            if (record.getDuration() != null && record.getDuration() > 0) {
                durationMinutes = record.getDuration();
            } else if (record.getStartTime() != null && record.getEndTime() != null && (durationMinutes = (int)ChronoUnit.MINUTES.between(record.getStartTime(), record.getEndTime())) < 0) {
                durationMinutes = 0;
            }
            dailyMinutes.put(key, (Integer)dailyMinutes.get(key) + durationMinutes);
        }
        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Integer> values = new ArrayList<Integer>();
        ArrayList<Map<String, Object>> trend = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dailyMinutes.entrySet()) {
            String date = (String)entry.getKey();
            Integer minutes = (Integer)entry.getValue();
            labels.add(date);
            values.add(minutes);
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("date", date);
            item.put("minutes", minutes);
            trend.add(item);
        }
        result.put("labels", labels);
        result.put("values", values);
        result.put("trend", trend);
        result.put("examRecords", examRecords);
        return result;
    }

    public Map<String, Object> getTeacherDashboard(Long userId) {
        List<UserRole> studentUserRoles;
        HashMap<String, Object> result = new HashMap<String, Object>();
        long totalStudents = 0L;
        Role studentRole = (Role)this.roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, "ROLE_STUDENT"));
        if (studentRole != null && !(studentUserRoles = this.userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId,studentRole.getId()))).isEmpty()) {
            List<Long> studentUserIds = studentUserRoles.stream().map(UserRole::getUserId).collect(Collectors.toList());
            totalStudents = this.userMapper.selectCount(new LambdaQueryWrapper<User>().in(User::getId, studentUserIds).eq(User::getStatus,1));
        }
        result.put("totalStudents", totalStudents);
        long totalExams = this.examMapper.selectCount(new LambdaQueryWrapper<Exam>().eq(Exam::getDeleted,0));
        result.put("totalExams", totalExams);
        long totalQuestions2 = this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getDeleted,0));
        result.put("totalQuestions", totalQuestions2);
        List<ExamRecord> completedRecords = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStatus, "COMPLETED"));
        if (!completedRecords.isEmpty()) {
            long passedCount = completedRecords.stream().filter(r -> r.getObtainedScore() != null && r.getTotalScore() != null && r.getObtainedScore().compareTo(r.getTotalScore().multiply(BigDecimal.valueOf(0.6))) >= 0).count();
            int avgPassRate = (int)(passedCount * 100L / (long)completedRecords.size());
            result.put("avgPassRate", avgPassRate);
        } else {
            result.put("avgPassRate", 0);
        }
        return result;
    }

    public DashboardService(UserMapper userMapper, ExamRecordMapper examRecordMapper, ExamMapper examMapper, QuestionMapper questionMapper, WrongQuestionMapper wrongQuestionMapper, StudyPlanMapper studyPlanMapper, CommunityPostMapper communityPostMapper, StudentInfoMapper studentInfoMapper, RoleMapper roleMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.examRecordMapper = examRecordMapper;
        this.examMapper = examMapper;
        this.questionMapper = questionMapper;
        this.wrongQuestionMapper = wrongQuestionMapper;
        this.studyPlanMapper = studyPlanMapper;
        this.communityPostMapper = communityPostMapper;
        this.studentInfoMapper = studentInfoMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }
}

