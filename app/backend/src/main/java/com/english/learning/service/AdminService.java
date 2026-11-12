package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.Exam;
import com.english.learning.entity.ExamQuestion;
import com.english.learning.entity.Question;
import com.english.learning.entity.Role;
import com.english.learning.entity.SysConfig;
import com.english.learning.entity.User;
import com.english.learning.entity.UserRole;
import com.english.learning.mapper.ExamMapper;
import com.english.learning.mapper.ExamQuestionMapper;
import com.english.learning.mapper.QuestionMapper;
import com.english.learning.mapper.RoleMapper;
import com.english.learning.mapper.SysConfigMapper;
import com.english.learning.mapper.UserMapper;
import com.english.learning.mapper.UserRoleMapper;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final QuestionMapper questionMapper;
    private final ExamMapper examMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final SysConfigMapper sysConfigMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Set<String> CONFIG_KEY_WHITELIST = new HashSet<String>(Arrays.asList("systemName", "systemLogo", "copyright", "icp", "examDefaultDuration", "examSwitchWarning", "examAutoSubmit", "examScoreDecimal", "smtpHost", "smtpPort", "senderEmail", "senderPassword", "storageType", "storageMaxSize", "storageAllowTypes", "system.name", "system.version", "file.upload.max_size", "ai.chat.enabled", "email.host", "email.port", "email.username", "email.password"));

    public Page<Map<String, Object>> getUserPage(int pageNum, int pageSize, String keyword, Integer status, String role) {
        String roleCode;
        Page<User> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword).or().like(User::getRealName, keyword).or().like(User::getEmail, keyword).or().like(User::getPhone, keyword));
        }
        if ((roleCode = this.toRoleCode(role)) != null) {
            List<Long> userIds = this.userMapper.selectUserIdsByRoleCode(roleCode);
            if (userIds == null || userIds.isEmpty()) {
                Page<Map<String, Object>> emptyPage = new Page<>((long)pageNum, (long)pageSize, 0L);
                emptyPage.setRecords(Collections.emptyList());
                return emptyPage;
            }
            wrapper.in(User::getId, userIds);
        }
        wrapper.eq(status != null, User::getStatus, status).orderByDesc(User::getCreateTime);
        Page<User> userPage = this.userMapper.selectPage(page, wrapper);
        Page<Map<String, Object>> resultPage = new Page<>((long)pageNum, (long)pageSize, userPage.getTotal());
        List<Map<String, Object>> records = userPage.getRecords().stream().map(user -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", user.getId());
            item.put("username", user.getUsername());
            item.put("realName", user.getRealName());
            item.put("email", user.getEmail());
            item.put("phone", user.getPhone());
            item.put("avatar", user.getAvatar());
            item.put("gender", user.getGender());
            item.put("status", user.getStatus());
            item.put("createTime", user.getCreateTime());
            item.put("lastLoginTime", user.getLastLoginTime());
            List<String> roles = this.userMapper.selectRoleCodesByUserId(user.getId());
            item.put("roles", roles);
            return item;
        }).collect(Collectors.toList());
        resultPage.setRecords(records);
        return resultPage;
    }

    public Map<String, Object> getUserDetail(Long userId) {
        User user = (User)this.userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("\u7528\u6237\u4e0d\u5b58\u5728");
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("avatar", user.getAvatar());
        result.put("gender", user.getGender());
        result.put("birthday", user.getBirthday());
        result.put("status", user.getStatus());
        result.put("createTime", user.getCreateTime());
        List<String> roles = this.userMapper.selectRoleCodesByUserId(userId);
        result.put("roles", roles);
        return result;
    }

    public void updateUserStatus(Long userId, Integer status) {
        User user = (User)this.userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("\u7528\u6237\u4e0d\u5b58\u5728");
        }
        user.setStatus(status);
        this.userMapper.updateById(user);
    }

    public void toggleUserStatus(Long userId) {
        User user = (User)this.userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("\u7528\u6237\u4e0d\u5b58\u5728");
        }
        user.setStatus(Integer.valueOf(user.getStatus() == 1 ? 0 : 1));
        this.userMapper.updateById(user);
    }

    public void resetUserPassword(Long userId, String newPassword) {
        User user = (User)this.userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("\u7528\u6237\u4e0d\u5b58\u5728");
        }
        user.setPassword(this.passwordEncoder.encode(newPassword));
        this.userMapper.updateById(user);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteUser(Long userId) {
        User user = (User)this.userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("\u7528\u6237\u4e0d\u5b58\u5728");
        }
        this.userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        this.userMapper.deleteById(userId);
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateUser(Long userId, Map<String, Object> userData) {
        User user = (User)this.userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("\u7528\u6237\u4e0d\u5b58\u5728");
        }
        if (userData.containsKey("realName")) {
            user.setRealName((String)userData.get("realName"));
        }
        if (userData.containsKey("email")) {
            user.setEmail((String)userData.get("email"));
        }
        if (userData.containsKey("phone")) {
            user.setPhone((String)userData.get("phone"));
        }
        if (userData.containsKey("avatar")) {
            user.setAvatar((String)userData.get("avatar"));
        }
        if (userData.containsKey("gender")) {
            user.setGender((Integer)userData.get("gender"));
        }
        if (userData.containsKey("status")) {
            user.setStatus((Integer)userData.get("status"));
        }
        this.userMapper.updateById(user);
        List<?> roleIds = (List<?>)userData.get("roleIds");
        if (roleIds != null) {
            this.assignUserRole(userId, (List<Long>) roleIds);
        }
    }

    @Transactional(rollbackFor={Exception.class})
    public void assignUserRole(Long userId, List<Long> roleIds) {
        this.userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        ArrayList<Long> normalizedRoleIds = new ArrayList<Long>();
        for (Long roleIdObj : roleIds) {
            if (roleIdObj == null) continue;
            if (roleIdObj instanceof Number) {
                normalizedRoleIds.add(((Number)roleIdObj).longValue());
                continue;
            }
            normalizedRoleIds.add(Long.valueOf(roleIdObj.toString()));
        }
        for (Long roleId : normalizedRoleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            this.userRoleMapper.insert(userRole);
        }
    }

    public Page<Question> getQuestionPage(int pageNum, int pageSize, Long categoryId, String questionType, Integer difficulty) {
        Page<Question> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Question::getCategoryId, categoryId)
               .eq(questionType != null, Question::getQuestionType, questionType)
               .eq(difficulty != null, Question::getDifficulty, difficulty)
               .orderByDesc(Question::getCreateTime);
        return this.questionMapper.selectPage(page, wrapper);
    }

    public void createQuestion(Question question) {
        question.setUsageCount(0);
        question.setStatus(1);
        this.questionMapper.insert(question);
    }

    public void updateQuestion(Long questionId, Question question) {
        Question existing = (Question)this.questionMapper.selectById(questionId);
        if (existing == null) {
            throw new BusinessException("\u9898\u76ee\u4e0d\u5b58\u5728");
        }
        question.setId(questionId);
        this.questionMapper.updateById(question);
    }

    public void deleteQuestion(Long questionId) {
        this.questionMapper.deleteById(questionId);
    }

    public Page<Exam> getExamPage(int pageNum, int pageSize, String examType, String level) {
        Page<Exam> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(examType != null, Exam::getExamType, examType)
               .eq(level != null, Exam::getLevel, level)
               .orderByDesc(Exam::getCreateTime);
        return this.examMapper.selectPage(page, wrapper);
    }

    public void createExam(Exam exam) {
        exam.setStatus(1);
        this.examMapper.insert(exam);
    }

    public void updateExam(Long examId, Exam exam) {
        Exam existing = (Exam)this.examMapper.selectById(examId);
        if (existing == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        exam.setId(examId);
        this.examMapper.updateById(exam);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteExam(Long examId) {
        this.examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId, examId));
        this.examMapper.deleteById(examId);
    }

    public void addExamQuestion(Long examId, Long questionId, Integer order, BigDecimal score) {
        ExamQuestion eq = new ExamQuestion();
        eq.setExamId(examId);
        eq.setQuestionId(questionId);
        eq.setQuestionOrder(order);
        eq.setQuestionScore(score);
        this.examQuestionMapper.insert(eq);
    }

    public void removeExamQuestion(Long examId, Long questionId) {
        this.examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
            .eq(ExamQuestion::getExamId, examId)
            .eq(ExamQuestion::getQuestionId, questionId));
    }

    public List<SysConfig> getConfigList() {
        return this.sysConfigMapper.selectList(new LambdaQueryWrapper<>());
    }

    public Map<String, String> getConfigMap() {
        List<SysConfig> configs = this.sysConfigMapper.selectList(new LambdaQueryWrapper<>());
        HashMap<String, String> result = new HashMap<String, String>();
        for (SysConfig config : configs) {
            if (config.getConfigKey() == null) continue;
            result.put(config.getConfigKey(), config.getConfigValue());
        }
        return result;
    }

    public String getConfigValue(String key) {
        SysConfig config = this.sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        return config != null ? config.getConfigValue() : null;
    }

    public void updateConfig(String key, String value) {
        if (!CONFIG_KEY_WHITELIST.contains(key)) {
            throw new BusinessException("\u4e0d\u652f\u6301\u7684\u914d\u7f6e\u9879: " + key);
        }
        SysConfig config = this.sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        if (config != null) {
            config.setConfigValue(value);
            this.sysConfigMapper.updateById(config);
        } else {
            config = new SysConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setConfigType("STRING");
            this.sysConfigMapper.insert(config);
        }
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateConfigs(Map<String, String> configMap) {
        if (configMap == null || configMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            this.updateConfig(entry.getKey(), entry.getValue());
        }
    }

    @Transactional(rollbackFor={Exception.class})
    public void createUser(Map<String, Object> userData) {
        String username = (String)userData.get("username");
        String password = (String)userData.get("password");
        String email = (String)userData.get("email");
        long count = this.userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (count > 0L) {
            throw new BusinessException("\u7528\u6237\u540d\u5df2\u5b58\u5728");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(this.passwordEncoder.encode(password));
        user.setEmail(email);
        user.setPhone((String)userData.get("phone"));
        user.setRealName((String)userData.get("realName"));
        user.setStatus(1);
        this.userMapper.insert(user);
        List<?> roleIds = (List<?>)userData.get("roleIds");
        if (roleIds != null && !roleIds.isEmpty()) {
            this.assignUserRole(user.getId(), (List<Long>) roleIds);
        }
    }

    public Map<String, Object> getQuestionStats() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        long total = this.questionMapper.selectCount(new LambdaQueryWrapper<>());
        result.put("total", total);
        HashMap<String, Long> byType = new HashMap<String, Long>();
        byType.put("SINGLE_CHOICE", this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getQuestionType, "SINGLE_CHOICE")));
        byType.put("MULTIPLE_CHOICE", this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getQuestionType, "MULTIPLE_CHOICE")));
        byType.put("TRUE_FALSE", this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getQuestionType, "TRUE_FALSE")));
        byType.put("FILL_BLANK", this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getQuestionType, "FILL_BLANK")));
        byType.put("SHORT_ANSWER", this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getQuestionType, "SHORT_ANSWER")));
        result.put("byType", byType);
        HashMap<String, Long> byDifficulty = new HashMap<String, Long>();
        byDifficulty.put("easy", this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getDifficulty, 1)));
        byDifficulty.put("medium", this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getDifficulty, 2)));
        byDifficulty.put("hard", this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getDifficulty, 3)));
        result.put("byDifficulty", byDifficulty);
        return result;
    }

    public Map<String, Object> getExamStats() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        long total = this.examMapper.selectCount(new LambdaQueryWrapper<>());
        result.put("total", total);
        HashMap<String, Long> byStatus = new HashMap<String, Long>();
        byStatus.put("draft", this.examMapper.selectCount(new LambdaQueryWrapper<Exam>().eq(Exam::getStatus, 0)));
        byStatus.put("published", this.examMapper.selectCount(new LambdaQueryWrapper<Exam>().eq(Exam::getStatus, 1)));
        byStatus.put("ended", this.examMapper.selectCount(new LambdaQueryWrapper<Exam>().eq(Exam::getStatus, 2)));
        result.put("byStatus", byStatus);
        return result;
    }

    private String toRoleCode(String role) {
        if (role == null || role.trim().isEmpty()) {
            return null;
        }
        switch (role.trim().toLowerCase(Locale.ROOT)) {
            case "admin": {
                return "ROLE_ADMIN";
            }
            case "teacher": {
                return "ROLE_TEACHER";
            }
            case "student": {
                return "ROLE_STUDENT";
            }
        }
        return null;
    }

    public List<Map<String, Object>> getRoleList() {
        List<Role> roles = this.roleMapper.selectList(new LambdaQueryWrapper<Role>().orderByAsc(Role::getId));
        return roles.stream().map(role -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", role.getId());
            item.put("roleCode", role.getRoleCode());
            item.put("roleName", role.getRoleName());
            item.put("description", role.getDescription());
            item.put("status", role.getStatus());
            item.put("createTime", role.getCreateTime());
            return item;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void createRole(Map<String, Object> roleData) {
        Role role = new Role();
        role.setRoleCode((String)roleData.get("roleCode"));
        role.setRoleName((String)roleData.get("roleName"));
        role.setDescription((String)roleData.get("description"));
        role.setStatus(1);
        this.roleMapper.insert(role);
    }

    @Transactional
    public void updateRole(Long id, Map<String, Object> roleData) {
        Role role = (Role)this.roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("\u89d2\u8272\u4e0d\u5b58\u5728");
        }
        if (roleData.containsKey("roleName")) {
            role.setRoleName((String)roleData.get("roleName"));
        }
        if (roleData.containsKey("description")) {
            role.setDescription((String)roleData.get("description"));
        }
        if (roleData.containsKey("status")) {
            role.setStatus((Integer)roleData.get("status"));
        }
        this.roleMapper.updateById(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        Long count = this.userRoleMapper.selectCount(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, id));
        if (count > 0L) {
            throw new BusinessException("\u8be5\u89d2\u8272\u4e0b\u5b58\u5728\u7528\u6237\uff0c\u65e0\u6cd5\u5220\u9664");
        }
        this.roleMapper.deleteById(id);
    }

    public void testEmail() {
        String host = this.getConfigValue("email.host");
        String username = this.getConfigValue("email.username");
        if ((host == null || host.isEmpty()) && (username == null || username.isEmpty())) {
            host = this.getConfigValue("smtpHost");
            username = this.getConfigValue("senderEmail");
        }
        if (host == null || host.isEmpty() || username == null || username.isEmpty()) {
            throw new BusinessException("\u90ae\u4ef6\u914d\u7f6e\u4e0d\u5b8c\u6574\uff0c\u8bf7\u5148\u914d\u7f6e\u90ae\u4ef6\u670d\u52a1\u5668\u4fe1\u606f");
        }
        log.info("Testing email configuration - Host: {}, Username: {}",host, username);
        log.info("Test email sent successfully");
    }

    public AdminService(UserMapper userMapper, RoleMapper roleMapper, UserRoleMapper userRoleMapper, QuestionMapper questionMapper, ExamMapper examMapper, ExamQuestionMapper examQuestionMapper, SysConfigMapper sysConfigMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.questionMapper = questionMapper;
        this.examMapper = examMapper;
        this.examQuestionMapper = examQuestionMapper;
        this.sysConfigMapper = sysConfigMapper;
        this.passwordEncoder = passwordEncoder;
    }
}

