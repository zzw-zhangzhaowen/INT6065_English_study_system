package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.english.learning.entity.ExamRecord;
import com.english.learning.entity.StudyPlan;
import com.english.learning.entity.WrongQuestion;
import com.english.learning.mapper.ExamRecordMapper;
import com.english.learning.mapper.StudyPlanMapper;
import com.english.learning.mapper.WrongQuestionMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LearningReportService {
    private static final Logger log = LoggerFactory.getLogger(LearningReportService.class);
    private final ExamRecordMapper examRecordMapper;
    private final WrongQuestionMapper wrongQuestionMapper;
    private final StudyPlanMapper studyPlanMapper;

    public Map<String, Object> generateLearningReport(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        HashMap<String, Object> report = new HashMap<String, Object>();
        report.put("userId", userId);
        report.put("reportPeriod", Map.of("start", startTime, "end", endTime, "days", ChronoUnit.DAYS.between(startTime, endTime)));
        report.put("examStats", this.getExamStats(userId, startTime, endTime));
        report.put("wrongQuestionStats", this.getWrongQuestionStats(userId, startTime, endTime));
        report.put("studyPlanStats", this.getStudyPlanStats(userId, startTime, endTime));
        report.put("suggestions", this.generateSuggestions(report));
        log.info("\u751f\u6210\u7528\u6237{}\u7684\u5b66\u4e60\u62a5\u544a\uff0c\u65f6\u95f4\u8303\u56f4: {} - {}", new Object[]{userId, startTime, endTime});
        return report;
    }

    private Map<String, Object> getExamStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getStudentId, userId).between(ExamRecord::getStartTime,startTime,endTime).eq(ExamRecord::getStatus, "completed");
        List<ExamRecord> records = this.examRecordMapper.selectList(wrapper);
        if (records.isEmpty()) {
            return Map.of("totalExams", 0, "averageScore", BigDecimal.ZERO, "highestScore", BigDecimal.ZERO, "lowestScore", BigDecimal.ZERO);
        }
        int totalExams = records.size();
        BigDecimal totalScore = records.stream().map(ExamRecord::getObtainedScore).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageScore = totalScore.divide(BigDecimal.valueOf(totalExams), 2, RoundingMode.HALF_UP);
        BigDecimal highestScore = records.stream().map(ExamRecord::getObtainedScore).filter(Objects::nonNull).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal lowestScore = records.stream().map(ExamRecord::getObtainedScore).filter(Objects::nonNull).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        return Map.of("totalExams", totalExams, "averageScore", averageScore, "highestScore", highestScore, "lowestScore", lowestScore);
    }

    private Map<String, Object> getWrongQuestionStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getStudentId, userId).between(WrongQuestion::getCreateTime,startTime,endTime);
        List<WrongQuestion> wrongQuestions = this.wrongQuestionMapper.selectList(wrapper);
        int totalWrong = wrongQuestions.size();
        long masteredCount = wrongQuestions.stream().filter(wq -> wq.getMasteryLevel() != null && wq.getMasteryLevel() >= 3).count();
        return Map.of("totalWrongQuestions", totalWrong, "masteredCount", masteredCount, "unmasteredCount", (long)totalWrong - masteredCount);
    }

    private Map<String, Object> getStudyPlanStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<StudyPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyPlan::getStudentId, userId).between(StudyPlan::getCreateTime,startTime,endTime);
        List<StudyPlan> plans = this.studyPlanMapper.selectList(wrapper);
        int totalPlans = plans.size();
        long completedPlans = plans.stream().filter(plan -> plan.getProgress() != null && plan.getProgress() >= 100).count();
        return Map.of("totalPlans", totalPlans, "completedPlans", completedPlans, "inProgressPlans", (long)totalPlans - completedPlans);
    }

    private List<String> generateSuggestions(Map<String, Object> report) {
        int totalWrong;
        Map<String, Object> wrongStats;
        ArrayList<String> suggestions = new ArrayList<String>();
        Map<String, Object> examStats = (Map<String, Object>)report.get("examStats");
        if (examStats != null && examStats.get("totalExams") != null) {
            int totalExams = (Integer)examStats.get("totalExams");
            if (totalExams == 0) {
                suggestions.add("\u5efa\u8bae\u5f00\u59cb\u8fdb\u884c\u8003\u8bd5\u7ec3\u4e60\uff0c\u68c0\u9a8c\u5b66\u4e60\u6210\u679c");
            } else {
                BigDecimal avgScore = (BigDecimal)examStats.get("averageScore");
                if (avgScore != null) {
                    if (avgScore.compareTo(BigDecimal.valueOf(60L)) < 0) {
                        suggestions.add("\u5e73\u5747\u5206\u8f83\u4f4e\uff0c\u5efa\u8bae\u52a0\u5f3a\u57fa\u7840\u77e5\u8bc6\u5b66\u4e60");
                    } else if (avgScore.compareTo(BigDecimal.valueOf(80L)) < 0) {
                        suggestions.add("\u6210\u7ee9\u826f\u597d\uff0c\u7ee7\u7eed\u52aa\u529b\u53ef\u4ee5\u8fbe\u5230\u4f18\u79c0\u6c34\u5e73");
                    } else {
                        suggestions.add("\u6210\u7ee9\u4f18\u79c0\uff0c\u7ee7\u7eed\u4fdd\u6301\uff01");
                    }
                }
            }
        }
        if ((wrongStats = (Map<String, Object>)report.get("wrongQuestionStats")) != null && (totalWrong = ((Integer)wrongStats.get("totalWrongQuestions")).intValue()) > 10) {
            suggestions.add("\u9519\u9898\u8f83\u591a\uff0c\u5efa\u8bae\u6bcf\u5929\u590d\u4e60\u9519\u9898\u672c");
        }
        if (suggestions.isEmpty()) {
            suggestions.add("\u7ee7\u7eed\u4fdd\u6301\u826f\u597d\u7684\u5b66\u4e60\u4e60\u60ef\uff01");
        }
        return suggestions;
    }

    public Map<String, Object> generateWeeklyReport(Long userId) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusWeeks(1L);
        return this.generateLearningReport(userId, startTime, endTime);
    }

    public Map<String, Object> generateMonthlyReport(Long userId) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMonths(1L);
        return this.generateLearningReport(userId, startTime, endTime);
    }

    public LearningReportService(ExamRecordMapper examRecordMapper, WrongQuestionMapper wrongQuestionMapper, StudyPlanMapper studyPlanMapper) {
        this.examRecordMapper = examRecordMapper;
        this.wrongQuestionMapper = wrongQuestionMapper;
        this.studyPlanMapper = studyPlanMapper;
    }
}

