package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.dto.LoginRequest;
import com.english.learning.dto.RegisterRequest;
import com.english.learning.entity.Role;
import com.english.learning.entity.User;
import com.english.learning.entity.UserRole;
import com.english.learning.mapper.RoleMapper;
import com.english.learning.mapper.UserMapper;
import com.english.learning.mapper.UserRoleMapper;
import com.english.learning.util.JwtUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String RESET_CODE_PREFIX = "reset_code:";
    private static final long RESET_CODE_EXPIRE_MINUTES = 5L;

    public Map<String, Object> login(LoginRequest request) {
        Object roleCode;
        log.info("\u767b\u5f55\u8bf7\u6c42 - \u8d26\u53f7: {}",request.getUsername());
        User user = this.userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername,request.getUsername()).or().eq(User::getEmail,request.getUsername()).or().eq(User::getPhone,request.getUsername()));
        if (user == null) {
            log.error("\u767b\u5f55\u5931\u8d25 - \u7528\u6237\u4e0d\u5b58\u5728: {}",request.getUsername());
            throw new BusinessException("\u7528\u6237\u540d\u6216\u5bc6\u7801\u9519\u8bef");
        }
        boolean matches = this.passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            log.error("\u767b\u5f55\u5931\u8d25 - \u5bc6\u7801\u9519\u8bef: \u7528\u6237 {}",request.getUsername());
            throw new BusinessException("\u7528\u6237\u540d\u6216\u5bc6\u7801\u9519\u8bef");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("\u8d26\u53f7\u5df2\u88ab\u7981\u7528");
        }
        List<String> roles = this.userMapper.selectRoleCodesByUserId(user.getId());
        Object object = roleCode = roles.isEmpty() ? "ROLE_STUDENT" : (String)roles.get(0);
        if (!((String)roleCode).startsWith("ROLE_")) {
            roleCode = "ROLE_" + (String)roleCode;
        }
        String token = this.jwtUtil.generateToken(user.getUsername(), user.getId(), (String)roleCode);
        user.setLastLoginTime(LocalDateTime.now());
        this.userMapper.updateById(user);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("token", token);
        result.put("userInfo", this.buildUserInfo(user, (String)roleCode));
        return result;
    }

    @Transactional(rollbackFor={Exception.class})
    public void register(RegisterRequest request) {
        long count = this.userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername,request.getUsername()));
        if (count > 0L) {
            throw new BusinessException("\u7528\u6237\u540d\u5df2\u5b58\u5728");
        }
        count = this.userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail,request.getEmail()));
        if (count > 0L) {
            throw new BusinessException("\u90ae\u7bb1\u5df2\u88ab\u6ce8\u518c");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRealName(request.getRealName());
        user.setStatus(1);
        this.userMapper.insert(user);
        String roleCode = "STUDENT".equals(request.getRoleType()) ? "ROLE_STUDENT" : "ROLE_TEACHER";
        Role role = this.roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode,roleCode));
        if (role != null) {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(role.getId());
            this.userRoleMapper.insert(userRole);
        }
    }

    private Map<String, Object> buildUserInfo(User user, String roleCode) {
        HashMap<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getRealName() != null && !user.getRealName().trim().isEmpty() ? user.getRealName() : user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("role", roleCode);
        return userInfo;
    }

    public void sendResetCode(String email) {
        User user = this.userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail,email));
        if (user == null) {
            throw new BusinessException("\u90ae\u7bb1\u4e0d\u5b58\u5728");
        }
        String code = String.format("%06d", new Random().nextInt(1000000));
        String redisKey = RESET_CODE_PREFIX + email;
        this.redisTemplate.opsForValue().set(redisKey,code, 5L, TimeUnit.MINUTES);
        log.info("\u5411\u90ae\u7bb1 {} \u53d1\u9001\u5bc6\u7801\u91cd\u7f6e\u9a8c\u8bc1\u7801: {}",email,code);
    }

    @Transactional(rollbackFor={Exception.class})
    public void resetPassword(String email, String code, String newPassword) {
        String redisKey = RESET_CODE_PREFIX + email;
        Object storedCode = this.redisTemplate.opsForValue().get(redisKey);
        if (storedCode == null) {
            throw new BusinessException("\u9a8c\u8bc1\u7801\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u83b7\u53d6");
        }
        if (!storedCode.toString().equals(code)) {
            throw new BusinessException("\u9a8c\u8bc1\u7801\u9519\u8bef");
        }
        User user = this.userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail,email));
        if (user == null) {
            throw new BusinessException("\u7528\u6237\u4e0d\u5b58\u5728");
        }
        user.setPassword(this.passwordEncoder.encode(newPassword));
        this.userMapper.updateById(user);
        this.redisTemplate.delete(redisKey);
        log.info("\u7528\u6237 {} \u91cd\u7f6e\u5bc6\u7801\u6210\u529f",user.getUsername());
    }

    public AuthService(UserMapper userMapper, RoleMapper roleMapper, UserRoleMapper userRoleMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }
}

