package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.dto.ChangePasswordRequest;
import com.english.learning.dto.UserProfileRequest;
import com.english.learning.entity.ExamRecord;
import com.english.learning.entity.StudentInfo;
import com.english.learning.entity.User;
import com.english.learning.entity.UserFollow;
import com.english.learning.mapper.ExamRecordMapper;
import com.english.learning.mapper.StudentInfoMapper;
import com.english.learning.mapper.UserFollowMapper;
import com.english.learning.mapper.UserMapper;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserMapper userMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final UserFollowMapper userFollowMapper;
    private final ExamRecordMapper examRecordMapper;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> getUserInfo(Long userId) {
        User user = (User)this.userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("\u7528\u6237\u4e0d\u5b58\u5728");
        }
        HashMap<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("gender", user.getGender());
        userInfo.put("birthday", user.getBirthday());
        StudentInfo studentInfo = (StudentInfo)this.studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>().eq(StudentInfo::getUserId, userId));
        int totalStudyMinutes = 0;
        if (studentInfo != null) {
            userInfo.put("school", studentInfo.getSchool());
            userInfo.put("major", studentInfo.getMajor());
            userInfo.put("grade", studentInfo.getGrade());
            userInfo.put("currentLevel", studentInfo.getCurrentLevel());
            userInfo.put("targetLevel", studentInfo.getTargetLevel());
            userInfo.put("studyDays", studentInfo.getStudyDays());
            userInfo.put("continuousDays", studentInfo.getContinuousDays());
            userInfo.put("achievementScore", studentInfo.getAchievementScore());
            int n = totalStudyMinutes = studentInfo.getTotalStudyTime() != null ? studentInfo.getTotalStudyTime() : 0;
        }
        if (totalStudyMinutes <= 0) {
            totalStudyMinutes = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId, userId)).stream().filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus())).map(record -> {
                if (record.getDuration() != null && record.getDuration() > 0) {
                    return record.getDuration();
                }
                if (record.getStartTime() != null && record.getEndTime() != null) {
                    long minutes = ChronoUnit.MINUTES.between(record.getStartTime(), record.getEndTime());
                    return (int)Math.max(minutes, 0L);
                }
                return 0;
            }).filter(d -> d > 0).mapToInt(Integer::intValue).sum();
        }
        userInfo.put("totalStudyTime", totalStudyMinutes);
        List roles = this.userMapper.selectRoleCodesByUserId(userId);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("user", userInfo);
        result.put("roles", roles);
        return result;
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateProfile(Long userId, UserProfileRequest request) {
        User user = (User)this.userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("\u7528\u6237\u4e0d\u5b58\u5728");
        }
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getEmail() != null) {
            long count = this.userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail,request.getEmail()).ne(User::getId, userId));
            if (count > 0L) {
                throw new BusinessException("\u90ae\u7bb1\u5df2\u88ab\u4f7f\u7528");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(LocalDate.parse(request.getBirthday()));
        }
        this.userMapper.updateById(user);
        if (request.getSchool() != null || request.getCurrentLevel() != null || request.getTargetLevel() != null) {
            StudentInfo studentInfo = (StudentInfo)this.studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>().eq(StudentInfo::getUserId, userId));
            if (studentInfo == null) {
                studentInfo = new StudentInfo();
                studentInfo.setUserId(userId);
                studentInfo.setStudyDays(0);
                studentInfo.setContinuousDays(0);
                studentInfo.setTotalStudyTime(0);
                studentInfo.setAchievementScore(0);
            }
            if (request.getSchool() != null) {
                studentInfo.setSchool(request.getSchool());
            }
            if (request.getCurrentLevel() != null) {
                studentInfo.setCurrentLevel(request.getCurrentLevel());
            }
            if (request.getTargetLevel() != null) {
                studentInfo.setTargetLevel(request.getTargetLevel());
            }
            if (studentInfo.getId() == null) {
                this.studentInfoMapper.insert(studentInfo);
            } else {
                this.studentInfoMapper.updateById(studentInfo);
            }
        }
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = (User)this.userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("\u7528\u6237\u4e0d\u5b58\u5728");
        }
        if (!this.passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("\u539f\u5bc6\u7801\u9519\u8bef");
        }
        user.setPassword(this.passwordEncoder.encode(request.getNewPassword()));
        this.userMapper.updateById(user);
    }

    public Map<String, Object> getStudyStatistics(Long userId) {
        StudentInfo studentInfo2 = (StudentInfo)this.studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>().eq(StudentInfo::getUserId, userId));
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (studentInfo2 != null) {
            result.put("studyDays", studentInfo2.getStudyDays());
            result.put("continuousDays", studentInfo2.getContinuousDays());
            result.put("totalStudyTime", studentInfo2.getTotalStudyTime());
            result.put("achievementScore", studentInfo2.getAchievementScore());
        } else {
            result.put("studyDays", 0);
            result.put("continuousDays", 0);
            result.put("totalStudyTime", 0);
            result.put("achievementScore", 0);
        }
        List<ExamRecord> examRecords = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId, userId));
        int totalExams = examRecords.size();
        result.put("totalExams", totalExams);
        result.put("examCount", totalExams);
        Integer currentStudyMinutes = (Integer)result.get("totalStudyTime");
        if (currentStudyMinutes == null || currentStudyMinutes <= 0) {
            int fallbackStudyMinutes = examRecords.stream().filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus())).map(record -> {
                if (record.getDuration() != null && record.getDuration() > 0) {
                    return record.getDuration();
                }
                if (record.getStartTime() != null && record.getEndTime() != null) {
                    long minutes = ChronoUnit.MINUTES.between(record.getStartTime(), record.getEndTime());
                    return (int)Math.max(minutes, 0L);
                }
                return 0;
            }).filter(d -> d > 0).mapToInt(Integer::intValue).sum();
            result.put("totalStudyTime", fallbackStudyMinutes);
        }
        if (totalExams > 0) {
            int totalCorrect = 0;
            int totalQuestions = 0;
            for (ExamRecord record2 : (List<ExamRecord>)examRecords) {
                int correct = record2.getCorrectCount() != null ? record2.getCorrectCount() : 0;
                int wrong = record2.getWrongCount() != null ? record2.getWrongCount() : 0;
                totalCorrect += correct;
                totalQuestions += correct + wrong;
            }
            int avgScore = totalQuestions > 0 ? BigDecimal.valueOf(totalCorrect).multiply(BigDecimal.valueOf(100L)).divide(BigDecimal.valueOf(totalQuestions), 0, RoundingMode.HALF_UP).intValue() : 0;
            result.put("avgScore", avgScore);
            result.put("accuracy", avgScore);
        } else {
            result.put("avgScore", 0);
            result.put("accuracy", 0);
        }
        return result;
    }

    public List<Map<String, Object>> getAchievements(Long userId) {
        HashMap<String, Object> achievement;
        int continuousDays;
        ArrayList<Map<String, Object>> achievements = new ArrayList<Map<String, Object>>();
        Map stats = this.getStudyStatistics(userId);
        int studyDays = stats.get("studyDays") != null ? (Integer)stats.get("studyDays") : 0;
        int examCount = stats.get("examCount") != null ? (Integer)stats.get("examCount") : 0;
        int n = continuousDays = stats.get("continuousDays") != null ? (Integer)stats.get("continuousDays") : 0;
        if (studyDays >= 1) {
            achievement = new HashMap<String, Object>();
            achievement.put("id", 1L);
            achievement.put("name", "\u521d\u5b66\u8005");
            achievement.put("description", "\u5b8c\u6210\u7b2c\u4e00\u5929\u5b66\u4e60");
            achievement.put("icon", "medal");
            achievement.put("unlocked", true);
            achievement.put("unlockedTime", LocalDateTime.now().minusDays(studyDays));
            achievements.add(achievement);
        }
        if (studyDays >= 7) {
            achievement = new HashMap();
            achievement.put("id", 2L);
            achievement.put("name", "\u575a\u6301\u4e00\u5468");
            achievement.put("description", "\u7d2f\u8ba1\u5b66\u4e607\u5929");
            achievement.put("icon", "trophy");
            achievement.put("unlocked", true);
            achievement.put("unlockedTime", LocalDateTime.now().minusDays(studyDays - 7));
            achievements.add(achievement);
        }
        if (studyDays >= 30) {
            achievement = new HashMap();
            achievement.put("id", 3L);
            achievement.put("name", "\u6708\u5ea6\u8fbe\u4eba");
            achievement.put("description", "\u7d2f\u8ba1\u5b66\u4e6030\u5929");
            achievement.put("icon", "crown");
            achievement.put("unlocked", true);
            achievement.put("unlockedTime", LocalDateTime.now().minusDays(studyDays - 30));
            achievements.add(achievement);
        }
        if (examCount >= 1) {
            achievement = new HashMap();
            achievement.put("id", 4L);
            achievement.put("name", "\u9996\u6b21\u8003\u8bd5");
            achievement.put("description", "\u5b8c\u6210\u7b2c\u4e00\u6b21\u8003\u8bd5");
            achievement.put("icon", "document");
            achievement.put("unlocked", true);
            achievement.put("unlockedTime", LocalDateTime.now());
            achievements.add(achievement);
        }
        if (examCount >= 10) {
            achievement = new HashMap();
            achievement.put("id", 5L);
            achievement.put("name", "\u8003\u8bd5\u8fbe\u4eba");
            achievement.put("description", "\u5b8c\u621010\u6b21\u8003\u8bd5");
            achievement.put("icon", "star");
            achievement.put("unlocked", true);
            achievement.put("unlockedTime", LocalDateTime.now());
            achievements.add(achievement);
        }
        if (continuousDays >= 7) {
            achievement = new HashMap();
            achievement.put("id", 6L);
            achievement.put("name", "\u8fde\u7eed\u4e03\u5929");
            achievement.put("description", "\u8fde\u7eed\u5b66\u4e607\u5929");
            achievement.put("icon", "fire");
            achievement.put("unlocked", true);
            achievement.put("unlockedTime", LocalDateTime.now());
            achievements.add(achievement);
        }
        return achievements;
    }

    @Transactional
    public boolean toggleFollow(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new BusinessException("\u4e0d\u80fd\u5173\u6ce8\u81ea\u5df1");
        }
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, userId).eq(UserFollow::getFollowedId,targetUserId);
        UserFollow existingFollow = (UserFollow)this.userFollowMapper.selectOne(wrapper);
        if (existingFollow != null) {
            this.userFollowMapper.deleteById(existingFollow.getId());
            return false;
        }
        UserFollow follow = new UserFollow();
        follow.setFollowerId(userId);
        follow.setFollowedId(targetUserId);
        this.userFollowMapper.insert(follow);
        return true;
    }

    @Transactional
    public void unfollow(Long userId, Long targetUserId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, userId).eq(UserFollow::getFollowedId,targetUserId);
        this.userFollowMapper.delete(wrapper);
    }

    public boolean isFollowed(Long userId, Long targetUserId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, userId).eq(UserFollow::getFollowedId,targetUserId);
        return this.userFollowMapper.selectCount(wrapper) > 0L;
    }

    public UserService(UserMapper userMapper, StudentInfoMapper studentInfoMapper, UserFollowMapper userFollowMapper, ExamRecordMapper examRecordMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.studentInfoMapper = studentInfoMapper;
        this.userFollowMapper = userFollowMapper;
        this.examRecordMapper = examRecordMapper;
        this.passwordEncoder = passwordEncoder;
    }
}

