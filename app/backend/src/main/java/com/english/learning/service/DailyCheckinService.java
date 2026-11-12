package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.DailyCheckin;
import com.english.learning.entity.StudentInfo;
import com.english.learning.entity.User;
import com.english.learning.mapper.DailyCheckinMapper;
import com.english.learning.mapper.StudentInfoMapper;
import com.english.learning.mapper.UserMapper;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyCheckinService {
    private static final Logger log = LoggerFactory.getLogger(DailyCheckinService.class);
    private final DailyCheckinMapper dailyCheckinMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final UserMapper userMapper;

    @Transactional(rollbackFor={Exception.class})
    public Map<String, Object> checkin(Long userId, String notes, String mood) {
        LocalDate today = LocalDate.now();
        DailyCheckin existing = this.dailyCheckinMapper.selectOne(new LambdaQueryWrapper<DailyCheckin>().eq(DailyCheckin::getUserId, userId).eq(DailyCheckin::getCheckinDate,today));
        if (existing != null) {
            throw new BusinessException("\u4eca\u65e5\u5df2\u6253\u5361\uff0c\u8bf7\u52ff\u91cd\u590d\u6253\u5361");
        }
        DailyCheckin checkin = new DailyCheckin();
        checkin.setUserId(userId);
        checkin.setCheckinDate(today);
        checkin.setCheckinTime(LocalDateTime.now());
        checkin.setNotes(notes);
        checkin.setMood(mood != null ? mood : "NORMAL");
        checkin.setStudyDuration(0);
        checkin.setWordCount(0);
        checkin.setQuestionCount(0);
        this.dailyCheckinMapper.insert(checkin);
        this.updateStudentStudyDays(userId);
        int continuousDays = this.calculateContinuousDays(userId);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("checkinId", checkin.getId());
        result.put("checkinDate", today);
        result.put("continuousDays", continuousDays);
        result.put("message", "\u6253\u5361\u6210\u529f\uff01\u8fde\u7eed\u6253\u5361 " + continuousDays + " \u5929");
        return result;
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateCheckinData(Long userId, Integer studyDuration, Integer wordCount, Integer questionCount) {
        StudentInfo studentInfo;
        LocalDate today = LocalDate.now();
        DailyCheckin checkin = this.dailyCheckinMapper.selectOne(new LambdaQueryWrapper<DailyCheckin>().eq(DailyCheckin::getUserId, userId).eq(DailyCheckin::getCheckinDate,today));
        if (checkin == null) {
            checkin = new DailyCheckin();
            checkin.setUserId(userId);
            checkin.setCheckinDate(today);
            checkin.setCheckinTime(LocalDateTime.now());
            checkin.setMood("NORMAL");
            checkin.setStudyDuration(Integer.valueOf(studyDuration != null ? studyDuration : 0));
            checkin.setWordCount(Integer.valueOf(wordCount != null ? wordCount : 0));
            checkin.setQuestionCount(Integer.valueOf(questionCount != null ? questionCount : 0));
            this.dailyCheckinMapper.insert(checkin);
        } else {
            if (studyDuration != null) {
                checkin.setStudyDuration(Integer.valueOf(checkin.getStudyDuration() + studyDuration));
            }
            if (wordCount != null) {
                checkin.setWordCount(Integer.valueOf(checkin.getWordCount() + wordCount));
            }
            if (questionCount != null) {
                checkin.setQuestionCount(Integer.valueOf(checkin.getQuestionCount() + questionCount));
            }
            this.dailyCheckinMapper.updateById(checkin);
        }
        if (studyDuration != null && studyDuration > 0 && (studentInfo = this.studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>().eq(StudentInfo::getUserId, userId))) != null) {
            studentInfo.setTotalStudyTime(Integer.valueOf(studentInfo.getTotalStudyTime() + studyDuration));
            this.studentInfoMapper.updateById(studentInfo);
        }
    }

    public boolean hasCheckedInToday(Long userId) {
        LocalDate today = LocalDate.now();
        Long count = this.dailyCheckinMapper.selectCount(new LambdaQueryWrapper<DailyCheckin>().eq(DailyCheckin::getUserId, userId).eq(DailyCheckin::getCheckinDate,today));
        return count > 0L;
    }

    public DailyCheckin getTodayCheckin(Long userId) {
        LocalDate today = LocalDate.now();
        return this.dailyCheckinMapper.selectOne(new LambdaQueryWrapper<DailyCheckin>().eq(DailyCheckin::getUserId, userId).eq(DailyCheckin::getCheckinDate,today));
    }

    public List<DailyCheckin> getCheckinCalendar(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1L).minusDays(1L);
        return this.dailyCheckinMapper.selectList(new LambdaQueryWrapper<DailyCheckin>().eq(DailyCheckin::getUserId, userId).between(DailyCheckin::getCheckinDate,startDate,endDate).orderByAsc(DailyCheckin::getCheckinDate));
    }

    public Map<String, Object> getCheckinStats(Long userId) {
        HashMap<String, Object> stats = new HashMap<String, Object>();
        Long totalDays = this.dailyCheckinMapper.selectCount(new LambdaQueryWrapper<DailyCheckin>().eq(DailyCheckin::getUserId, userId));
        stats.put("totalDays", totalDays);
        int continuousDays = this.calculateContinuousDays(userId);
        stats.put("continuousDays", continuousDays);
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        Long monthDays = this.dailyCheckinMapper.selectCount(new LambdaQueryWrapper<DailyCheckin>().eq(DailyCheckin::getUserId, userId).ge(DailyCheckin::getCheckinDate,monthStart));
        stats.put("monthDays", monthDays);
        stats.put("checkedInToday", this.hasCheckedInToday(userId));
        int maxContinuousDays = this.calculateMaxContinuousDays(userId);
        stats.put("maxContinuousDays", maxContinuousDays);
        return stats;
    }

    public List<Map<String, Object>> getCheckinRanking(int limit) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        List<DailyCheckin> allCheckins = this.dailyCheckinMapper.selectList(new LambdaQueryWrapper<DailyCheckin>().ge(DailyCheckin::getCheckinDate,monthStart));
        HashMap<Long, Integer> userCheckinCount = new HashMap<Long, Integer>();
        for (DailyCheckin checkin : allCheckins) {
            userCheckinCount.merge(checkin.getUserId(), 1, (oldVal, newVal) -> oldVal + newVal);
        }
        ArrayList<Map.Entry<Long, Integer>> sortedEntries = new ArrayList<>(userCheckinCount.entrySet());
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        ArrayList<Map<String, Object>> ranking = new ArrayList<Map<String, Object>>();
        int rank = 1;
        for (Map.Entry<Long, Integer> entry : sortedEntries) {
            if (rank > limit) break;
            Long userId = (Long)entry.getKey();
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("rank", rank++);
            item.put("userId", userId);
            item.put("totalDays", entry.getValue());
            User user = this.userMapper.selectById(userId);
            item.put("nickname", user != null ? (user.getRealName() != null ? user.getRealName() : user.getUsername()) : "\u7528\u6237" + userId);
            item.put("avatar", user != null ? user.getAvatar() : null);
            item.put("continuousDays", this.calculateContinuousDays(userId));
            ranking.add(item);
        }
        return ranking;
    }

    private int calculateContinuousDays(Long userId) {
        DailyCheckin checkin;
        LocalDate today = LocalDate.now();
        List<DailyCheckin> checkins = this.dailyCheckinMapper.selectList(new LambdaQueryWrapper<DailyCheckin>().eq(DailyCheckin::getUserId, userId).ge(DailyCheckin::getCheckinDate,today.minusDays(60L)).orderByDesc(DailyCheckin::getCheckinDate));
        if (checkins.isEmpty()) {
            return 0;
        }
        int continuousDays = 0;
        LocalDate expectedDate = today;
        if (!checkins.get(0).getCheckinDate().equals(expectedDate)) {
            expectedDate = expectedDate.minusDays(1L);
        }
        Iterator<DailyCheckin> iterator = checkins.iterator();
        while (iterator.hasNext() && (checkin = iterator.next()).getCheckinDate().equals(expectedDate)) {
            ++continuousDays;
            expectedDate = expectedDate.minusDays(1L);
        }
        return continuousDays;
    }

    private int calculateMaxContinuousDays(Long userId) {
        List<DailyCheckin> checkins = this.dailyCheckinMapper.selectList(new LambdaQueryWrapper<DailyCheckin>().eq(DailyCheckin::getUserId, userId).orderByAsc(DailyCheckin::getCheckinDate));
        if (checkins.isEmpty()) {
            return 0;
        }
        int maxDays = 1;
        int currentDays = 1;
        for (int i = 1; i < checkins.size(); ++i) {
            LocalDate currDate;
            LocalDate prevDate = checkins.get(i - 1).getCheckinDate();
            if (ChronoUnit.DAYS.between(prevDate, currDate = checkins.get(i).getCheckinDate()) == 1L) {
                maxDays = Math.max(maxDays, ++currentDays);
                continue;
            }
            currentDays = 1;
        }
        return maxDays;
    }

    private void updateStudentStudyDays(Long userId) {
        StudentInfo studentInfo = this.studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>().eq(StudentInfo::getUserId, userId));
        if (studentInfo != null) {
            Long actualDays = this.dailyCheckinMapper.selectCount(new LambdaQueryWrapper<DailyCheckin>().eq(DailyCheckin::getUserId, userId));
            studentInfo.setStudyDays(Integer.valueOf(actualDays.intValue()));
            int continuousDays = this.calculateContinuousDays(userId);
            studentInfo.setContinuousDays(Integer.valueOf(continuousDays));
            this.studentInfoMapper.updateById(studentInfo);
        }
    }

    public DailyCheckinService(DailyCheckinMapper dailyCheckinMapper, StudentInfoMapper studentInfoMapper, UserMapper userMapper) {
        this.dailyCheckinMapper = dailyCheckinMapper;
        this.studentInfoMapper = studentInfoMapper;
        this.userMapper = userMapper;
    }
}

