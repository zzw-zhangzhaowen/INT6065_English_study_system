package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.ClassStudent;
import com.english.learning.entity.Classroom;
import com.english.learning.entity.ClassroomMessage;
import com.english.learning.entity.ClassroomParticipant;
import com.english.learning.entity.TeacherClass;
import com.english.learning.entity.User;
import com.english.learning.mapper.ClassStudentMapper;
import com.english.learning.mapper.ClassroomMapper;
import com.english.learning.mapper.ClassroomMessageMapper;
import com.english.learning.mapper.ClassroomParticipantMapper;
import com.english.learning.mapper.TeacherClassMapper;
import com.english.learning.mapper.UserMapper;
import java.io.Serializable;
import java.time.Duration;
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
public class ClassroomService {
    private static final Logger log = LoggerFactory.getLogger(ClassroomService.class);
    private final ClassroomMapper classroomMapper;
    private final ClassroomParticipantMapper participantMapper;
    private final ClassroomMessageMapper messageMapper;
    private final UserMapper userMapper;
    private final TeacherClassMapper teacherClassMapper;
    private final ClassStudentMapper classStudentMapper;

    public boolean hasClassroomAccess(Long classroomId, Long userId, String role) {
        if ("ROLE_ADMIN".equals(role)) {
            return true;
        }
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            return false;
        }
        if (classroom.getTeacherId().equals(userId)) {
            return true;
        }
        LambdaQueryWrapper<ClassroomParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassroomParticipant::getClassroomId, classroomId)
               .eq(ClassroomParticipant::getStudentId, userId);
        return this.participantMapper.selectCount(wrapper) > 0L;
    }

    @Transactional(rollbackFor={Exception.class})
    public Long createClassroom(Long teacherId, String classroomName, String classroomType, String description, String coverImage, LocalDateTime startTime, LocalDateTime endTime, Integer maxStudents) {
        Classroom classroom = new Classroom();
        classroom.setTeacherId(teacherId);
        classroom.setClassroomName(classroomName);
        classroom.setClassroomType(classroomType != null ? classroomType : "LIVE");
        classroom.setDescription(description);
        classroom.setCoverImage(coverImage);
        classroom.setStartTime(startTime);
        classroom.setEndTime(endTime);
        classroom.setMaxStudents(Integer.valueOf(maxStudents != null ? maxStudents : 100));
        classroom.setCurrentStudents(0);
        classroom.setStatus("PENDING");
        this.classroomMapper.insert(classroom);
        return classroom.getId();
    }

    public void updateClassroom(Long classroomId, Long teacherId, String classroomName, String description, String coverImage, LocalDateTime startTime, LocalDateTime endTime, Integer maxStudents) {
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("\u8bfe\u5802\u4e0d\u5b58\u5728");
        }
        if (!classroom.getTeacherId().equals(teacherId)) {
            throw new BusinessException("\u65e0\u6743\u4fee\u6539\u6b64\u8bfe\u5802");
        }
        if ("LIVE".equals(classroom.getStatus())) {
            throw new BusinessException("\u76f4\u64ad\u4e2d\u7684\u8bfe\u5802\u65e0\u6cd5\u4fee\u6539");
        }
        if (classroomName != null) {
            classroom.setClassroomName(classroomName);
        }
        if (description != null) {
            classroom.setDescription(description);
        }
        if (coverImage != null) {
            classroom.setCoverImage(coverImage);
        }
        if (startTime != null) {
            classroom.setStartTime(startTime);
        }
        if (endTime != null) {
            classroom.setEndTime(endTime);
        }
        if (maxStudents != null) {
            classroom.setMaxStudents(maxStudents);
        }
        this.classroomMapper.updateById(classroom);
    }

    public void deleteClassroom(Long classroomId, Long teacherId) {
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("\u8bfe\u5802\u4e0d\u5b58\u5728");
        }
        if (!classroom.getTeacherId().equals(teacherId)) {
            throw new BusinessException("\u65e0\u6743\u5220\u9664\u6b64\u8bfe\u5802");
        }
        if ("LIVE".equals(classroom.getStatus())) {
            throw new BusinessException("\u76f4\u64ad\u4e2d\u7684\u8bfe\u5802\u65e0\u6cd5\u5220\u9664");
        }
        this.classroomMapper.deleteById(classroomId);
    }

    public void startClassroom(Long classroomId, Long teacherId) {
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("\u8bfe\u5802\u4e0d\u5b58\u5728");
        }
        if (!classroom.getTeacherId().equals(teacherId)) {
            throw new BusinessException("\u65e0\u6743\u64cd\u4f5c\u6b64\u8bfe\u5802");
        }
        if (!"PENDING".equals(classroom.getStatus())) {
            throw new BusinessException("\u8bfe\u5802\u72b6\u6001\u4e0d\u6b63\u786e");
        }
        classroom.setStatus("LIVE");
        classroom.setStartTime(LocalDateTime.now());
        this.classroomMapper.updateById(classroom);
    }

    public void endClassroom(Long classroomId, Long teacherId) {
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("课堂不存在");
        }
        if (!classroom.getTeacherId().equals(teacherId)) {
            throw new BusinessException("无权操作此课堂");
        }
        // 幂等处理：如果课堂已经结束，直接返回成功
        if ("ENDED".equals(classroom.getStatus())) {
            log.info("课堂已处于结束状态，无需重复结束: {}", classroomId);
            return;
        }
        if (!"LIVE".equals(classroom.getStatus())) {
            throw new BusinessException("课堂未在直播中，当前状态: " + classroom.getStatus());
        }
        classroom.setStatus("ENDED");
        classroom.setEndTime(LocalDateTime.now());
        if (classroom.getStartTime() != null) {
            long seconds = Duration.between(classroom.getStartTime(), classroom.getEndTime()).getSeconds();
            int minutes = (int)(seconds / 60L);
            classroom.setDuration(Integer.valueOf(minutes));
            log.info("课堂时长：{} 秒（{} 分钟）",seconds,minutes);
        }
        this.classroomMapper.updateById(classroom);
        LocalDateTime now = LocalDateTime.now();
        this.participantMapper.batchSetLeaveTime(classroomId, now);
    }

    public void saveRecordUrl(Long classroomId, Long teacherId, String recordUrl) {
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("\u8bfe\u5802\u4e0d\u5b58\u5728");
        }
        if (!classroom.getTeacherId().equals(teacherId)) {
            throw new BusinessException("\u65e0\u6743\u64cd\u4f5c\u6b64\u8bfe\u5802");
        }
        classroom.setRecordUrl(recordUrl);
        this.classroomMapper.updateById(classroom);
    }

    public Page<Map<String, Object>> getClassroomPage(int pageNum, int pageSize, String status, Long teacherId, String classroomType, Long studentId) {
        Page<Classroom> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, Classroom::getStatus, status)
               .eq(teacherId != null, Classroom::getTeacherId, teacherId)
               .eq(classroomType != null, Classroom::getClassroomType, classroomType);
        if (studentId != null) {
            List<Long> classIds = this.classStudentMapper.selectList(new LambdaQueryWrapper<ClassStudent>()
                .eq(ClassStudent::getStudentId, studentId)).stream()
                .map(ClassStudent::getClassId).collect(Collectors.toList());
            if (!classIds.isEmpty()) {
                List<Long> myTeacherIds = this.teacherClassMapper.selectList(new LambdaQueryWrapper<TeacherClass>()
                    .in(TeacherClass::getClassId, classIds)).stream()
                    .map(TeacherClass::getTeacherId).distinct().collect(Collectors.toList());
                wrapper.in(Classroom::getTeacherId, myTeacherIds);
            } else {
                return new Page<>((long)pageNum, (long)pageSize, 0L);
            }
        }
        wrapper.orderByDesc(Classroom::getCreateTime);
        Page<Classroom> classroomPage = this.classroomMapper.selectPage(page, wrapper);
        List<Long> teacherIds = classroomPage.getRecords().stream()
            .map(Classroom::getTeacherId).distinct().collect(Collectors.toList());
        Map<Long, User> teacherMap = new HashMap<>();
        if (!teacherIds.isEmpty()) {
            List<User> teachers = this.userMapper.selectList(new LambdaQueryWrapper<User>()
                .in(User::getId, teacherIds));
            teacherMap = teachers.stream().collect(Collectors.toMap(User::getId, u -> u));
        }
        Page<Map<String, Object>> resultPage = new Page<>((long)pageNum, (long)pageSize, classroomPage.getTotal());
        Map<Long, User> finalTeacherMap = teacherMap;
        List<Map<String, Object>> records = classroomPage.getRecords().stream().map(classroom -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", classroom.getId());
            item.put("teacherId", classroom.getTeacherId());
            item.put("classroomName", classroom.getClassroomName());
            item.put("classroomType", classroom.getClassroomType());
            item.put("description", classroom.getDescription());
            item.put("coverImage", classroom.getCoverImage());
            item.put("startTime", classroom.getStartTime());
            item.put("endTime", classroom.getEndTime());
            item.put("duration", classroom.getDuration());
            item.put("maxStudents", classroom.getMaxStudents());
            item.put("currentStudents", classroom.getCurrentStudents());
            item.put("roomUrl", classroom.getRoomUrl());
            item.put("recordUrl", classroom.getRecordUrl());
            item.put("status", classroom.getStatus());
            item.put("createTime", classroom.getCreateTime());
            User teacher = finalTeacherMap.get(classroom.getTeacherId());
            if (teacher != null) {
                item.put("teacherName", teacher.getRealName() != null ? teacher.getRealName() : teacher.getUsername());
                item.put("teacherAvatar", teacher.getAvatar());
            }
            return item;
        }).collect(Collectors.toList());
        resultPage.setRecords(records);
        return resultPage;
    }

    public Map<String, Object> getClassroomDetail(Long classroomId) {
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("\u8bfe\u5802\u4e0d\u5b58\u5728");
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("classroom", classroom);
        User teacher = (User)this.userMapper.selectById(classroom.getTeacherId());
        if (teacher != null) {
            HashMap<String, Object> teacherInfo = new HashMap<String, Object>();
            teacherInfo.put("id", teacher.getId());
            teacherInfo.put("username", teacher.getUsername());
            teacherInfo.put("realName", teacher.getRealName());
            teacherInfo.put("avatar", teacher.getAvatar());
            result.put("teacher", teacherInfo);
        }
        LambdaQueryWrapper<ClassroomParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassroomParticipant::getClassroomId, classroomId)
               .orderByDesc(ClassroomParticipant::getJoinTime);
        List<ClassroomParticipant> participants = this.participantMapper.selectList(wrapper);
        List<Long> studentIds = participants.stream()
            .map(ClassroomParticipant::getStudentId).distinct().collect(Collectors.toList());
        Map<Long, User> studentMap = new HashMap<>();
        if (!studentIds.isEmpty()) {
            List<User> students = this.userMapper.selectList(new LambdaQueryWrapper<User>()
                .in(User::getId, studentIds));
            studentMap = students.stream().collect(Collectors.toMap(User::getId, u -> u));
        }
        Map<Long, User> finalStudentMap = studentMap;
        List<Map<String, Object>> participantList = participants.stream().map(p -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", p.getId());
            item.put("studentId", p.getStudentId());
            item.put("joinTime", p.getJoinTime());
            item.put("leaveTime", p.getLeaveTime());
            item.put("duration", p.getDuration());
            item.put("isSigned", p.getIsSigned());
            User student = (User)finalStudentMap.get(p.getStudentId());
            if (student != null) {
                item.put("studentName", student.getRealName() != null ? student.getRealName() : student.getUsername());
                item.put("studentAvatar", student.getAvatar());
            }
            return item;
        }).collect(Collectors.toList());
        result.put("participants", participantList);
        return result;
    }

    @Transactional(rollbackFor={Exception.class})
    public void joinClassroom(Long classroomId, Long studentId) {
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("\u8bfe\u5802\u4e0d\u5b58\u5728");
        }
        if (!"LIVE".equals(classroom.getStatus()) && !"PENDING".equals(classroom.getStatus())) {
            throw new BusinessException("\u8bfe\u5802\u5df2\u7ed3\u675f\uff0c\u65e0\u6cd5\u52a0\u5165");
        }
        if (classroom.getCurrentStudents() >= classroom.getMaxStudents()) {
            throw new BusinessException("\u8bfe\u5802\u4eba\u6570\u5df2\u6ee1");
        }
        LambdaQueryWrapper<ClassroomParticipant> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(ClassroomParticipant::getClassroomId, classroomId)
                     .eq(ClassroomParticipant::getStudentId, studentId)
                     .isNull(ClassroomParticipant::getLeaveTime);
        if (this.participantMapper.selectCount(activeWrapper) > 0L) {
            return;
        }
        LambdaQueryWrapper<ClassroomParticipant> leftWrapper = new LambdaQueryWrapper<>();
        leftWrapper.eq(ClassroomParticipant::getClassroomId, classroomId)
                   .eq(ClassroomParticipant::getStudentId, studentId)
                   .isNotNull(ClassroomParticipant::getLeaveTime)
                   .orderByDesc(ClassroomParticipant::getJoinTime)
                   .last("LIMIT 1");
        ClassroomParticipant existing = (ClassroomParticipant)this.participantMapper.selectOne(leftWrapper);
        if (existing != null) {
            existing.setLeaveTime(null);
            existing.setJoinTime(LocalDateTime.now());
            existing.setDuration(null);
            this.participantMapper.updateById(existing);
        } else {
            ClassroomParticipant participant = new ClassroomParticipant();
            participant.setClassroomId(classroomId);
            participant.setStudentId(studentId);
            participant.setJoinTime(LocalDateTime.now());
            participant.setIsSigned(0);
            try {
                this.participantMapper.insert(participant);
            }
            catch (Exception e) {
                log.warn("\u52a0\u5165\u8bfe\u5802\u5e76\u53d1\u51b2\u7a81: classroomId={}, studentId={}",classroomId,studentId);
                return;
            }
        }
        this.classroomMapper.incrementStudents(classroomId);
    }

    @Transactional(rollbackFor={Exception.class})
    public void leaveClassroom(Long classroomId, Long studentId) {
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("\u8bfe\u5802\u4e0d\u5b58\u5728");
        }
        LambdaQueryWrapper<ClassroomParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassroomParticipant::getClassroomId, classroomId)
               .eq(ClassroomParticipant::getStudentId, studentId)
               .isNull(ClassroomParticipant::getLeaveTime);
        ClassroomParticipant participant = this.participantMapper.selectOne(wrapper);
        if (participant == null) {
            throw new BusinessException("\u60a8\u4e0d\u5728\u6b64\u8bfe\u5802\u4e2d");
        }
        participant.setLeaveTime(LocalDateTime.now());
        if (participant.getJoinTime() != null) {
            long minutes = Duration.between(participant.getJoinTime(), participant.getLeaveTime()).toMinutes();
            participant.setDuration(Integer.valueOf((int)minutes));
        }
        this.participantMapper.updateById(participant);
        this.classroomMapper.decrementStudents(classroomId);
    }

    public List<Classroom> getTeacherClassrooms(Long teacherId) {
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Classroom::getTeacherId, teacherId)
               .orderByDesc(Classroom::getCreateTime);
        return this.classroomMapper.selectList(wrapper);
    }

    public Page<Map<String, Object>> getStudentClassrooms(Long studentId, int pageNum, int pageSize) {
        LambdaQueryWrapper<ClassroomParticipant> pWrapper = new LambdaQueryWrapper<>();
        pWrapper.eq(ClassroomParticipant::getStudentId, studentId)
                .orderByDesc(ClassroomParticipant::getJoinTime);
        List<ClassroomParticipant> participations = this.participantMapper.selectList(pWrapper);
        List<Long> classroomIds = participations.stream()
            .map(ClassroomParticipant::getClassroomId).distinct().collect(Collectors.toList());
        if (classroomIds.isEmpty()) {
            return new Page<>((long)pageNum, (long)pageSize, 0L);
        }
        Page<Classroom> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Classroom::getId, classroomIds)
               .orderByDesc(Classroom::getCreateTime);
        Page<Classroom> classroomPage = this.classroomMapper.selectPage(page, wrapper);
        List<Long> teacherIds = classroomPage.getRecords().stream()
            .map(Classroom::getTeacherId).distinct().collect(Collectors.toList());
        Map<Long, User> teacherMap = new HashMap<>();
        if (!teacherIds.isEmpty()) {
            List<User> teachers = this.userMapper.selectList(new LambdaQueryWrapper<User>()
                .in(User::getId, teacherIds));
            teacherMap = teachers.stream().collect(Collectors.toMap(User::getId, u -> u));
        }
        Page<Map<String, Object>> resultPage = new Page<>((long)pageNum, (long)pageSize, classroomPage.getTotal());
        Map<Long, User> finalTeacherMap = teacherMap;
        List<Map<String, Object>> records = classroomPage.getRecords().stream().map(classroom -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", classroom.getId());
            item.put("classroomName", classroom.getClassroomName());
            item.put("classroomType", classroom.getClassroomType());
            item.put("description", classroom.getDescription());
            item.put("coverImage", classroom.getCoverImage());
            item.put("startTime", classroom.getStartTime());
            item.put("endTime", classroom.getEndTime());
            item.put("status", classroom.getStatus());
            User teacher = (User)finalTeacherMap.get(classroom.getTeacherId());
            if (teacher != null) {
                item.put("teacherName", teacher.getRealName() != null ? teacher.getRealName() : teacher.getUsername());
            }
            return item;
        }).collect(Collectors.toList());
        resultPage.setRecords(records);
        return resultPage;
    }

    public List<Map<String, Object>> getLiveClassrooms(Long studentId) {
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Classroom::getStatus, "LIVE")
               .orderByDesc(Classroom::getStartTime);
        List<Classroom> classrooms = this.classroomMapper.selectList(wrapper);
        if (studentId != null) {
            List<Long> classIds = this.classStudentMapper.selectList(new LambdaQueryWrapper<ClassStudent>()
                .eq(ClassStudent::getStudentId, studentId)).stream()
                .map(ClassStudent::getClassId).collect(Collectors.toList());
            if (!classIds.isEmpty()) {
                List<Long> myTeacherIds = this.teacherClassMapper.selectList(new LambdaQueryWrapper<TeacherClass>()
                    .in(TeacherClass::getClassId, classIds)).stream()
                    .map(TeacherClass::getTeacherId).distinct().collect(Collectors.toList());
                classrooms = classrooms.stream().filter(c -> myTeacherIds.contains(c.getTeacherId())).collect(Collectors.toList());
            } else {
                classrooms = List.of();
            }
        }
        List<Long> teacherIds = classrooms.stream()
            .map(Classroom::getTeacherId).distinct().collect(Collectors.toList());
        Map<Long, User> teacherMap = new HashMap<>();
        if (!teacherIds.isEmpty()) {
            List<User> teachers = this.userMapper.selectList(new LambdaQueryWrapper<User>()
                .in(User::getId, teacherIds));
            teacherMap = teachers.stream().collect(Collectors.toMap(User::getId, u -> u));
        }
        Map<Long, User> finalTeacherMap = teacherMap;
        return classrooms.stream().map(classroom -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", classroom.getId());
            item.put("classroomName", classroom.getClassroomName());
            item.put("classroomType", classroom.getClassroomType());
            item.put("description", classroom.getDescription());
            item.put("coverImage", classroom.getCoverImage());
            item.put("startTime", classroom.getStartTime());
            item.put("currentStudents", classroom.getCurrentStudents());
            item.put("maxStudents", classroom.getMaxStudents());
            User teacher = (User)finalTeacherMap.get(classroom.getTeacherId());
            if (teacher != null) {
                item.put("teacherName", teacher.getRealName() != null ? teacher.getRealName() : teacher.getUsername());
                item.put("teacherAvatar", teacher.getAvatar());
            }
            return item;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTodaySchedule(Long teacherId) {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1L);
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Classroom::getTeacherId, teacherId)
               .ge(Classroom::getStartTime, todayStart)
               .lt(Classroom::getStartTime, todayEnd)
               .orderByAsc(Classroom::getStartTime);
        List<Classroom> classrooms = this.classroomMapper.selectList(wrapper);
        return classrooms.stream().map(classroom -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", classroom.getId());
            item.put("title", classroom.getClassroomName());
            item.put("name", classroom.getClassroomName());
            item.put("startTime", classroom.getStartTime() != null ? classroom.getStartTime().toLocalTime().toString().substring(0, 5) : "");
            item.put("endTime", classroom.getEndTime() != null ? classroom.getEndTime().toLocalTime().toString().substring(0, 5) : "");
            item.put("className", classroom.getClassroomType() != null ? classroom.getClassroomType() : "");
            item.put("studentCount", classroom.getCurrentStudents());
            item.put("status", classroom.getStatus());
            return item;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void signIn(Long classroomId, Long studentId) {
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("\u8bfe\u5802\u4e0d\u5b58\u5728");
        }
        if (!"LIVE".equals(classroom.getStatus())) {
            throw new BusinessException("\u8bfe\u5802\u672a\u5f00\u59cb\u6216\u5df2\u7ed3\u675f");
        }
        if (classroom.getSignInActive() == null || classroom.getSignInActive() == 0) {
            throw new BusinessException("\u6559\u5e08\u5c1a\u672a\u53d1\u8d77\u7b7e\u5230");
        }
        LambdaQueryWrapper<ClassroomParticipant> allWrapper = new LambdaQueryWrapper<>();
        allWrapper.eq(ClassroomParticipant::getClassroomId, classroomId)
                  .eq(ClassroomParticipant::getStudentId, studentId)
                  .isNotNull(ClassroomParticipant::getSignInTime);
        if (this.participantMapper.selectCount(allWrapper) > 0L) {
            throw new BusinessException("\u5df2\u7b7e\u5230\uff0c\u8bf7\u52ff\u91cd\u590d\u7b7e\u5230");
        }
        LambdaQueryWrapper<ClassroomParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassroomParticipant::getClassroomId, classroomId)
               .eq(ClassroomParticipant::getStudentId, studentId)
               .isNull(ClassroomParticipant::getLeaveTime);
        ClassroomParticipant participant = this.participantMapper.selectOne(wrapper);
        if (participant == null) {
            throw new BusinessException("\u8bf7\u5148\u52a0\u5165\u8bfe\u5802");
        }
        participant.setSignInTime(LocalDateTime.now());
        participant.setIsSigned(1);
        this.participantMapper.updateById(participant);
    }

    @Transactional
    public void activateSignIn(Long classroomId, Long teacherId) {
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("\u8bfe\u5802\u4e0d\u5b58\u5728");
        }
        if (!classroom.getTeacherId().equals(teacherId)) {
            throw new BusinessException("\u65e0\u6743\u64cd\u4f5c\u6b64\u8bfe\u5802");
        }
        if (!"LIVE".equals(classroom.getStatus())) {
            throw new BusinessException("\u8bfe\u5802\u672a\u5728\u76f4\u64ad\u4e2d");
        }
        classroom.setSignInActive(1);
        this.classroomMapper.updateById(classroom);
    }

    public List<Map<String, Object>> getSignInList(Long classroomId) {
        LambdaQueryWrapper<ClassroomParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassroomParticipant::getClassroomId, classroomId)
               .orderByDesc(ClassroomParticipant::getSignInTime);
        List<ClassroomParticipant> participants = this.participantMapper.selectList(wrapper);
        return participants.stream().map(p -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("studentId", p.getStudentId());
            item.put("joinTime", p.getJoinTime());
            item.put("signInTime", p.getSignInTime());
            item.put("signed", p.getSignInTime() != null);
            User student = (User)this.userMapper.selectById(p.getStudentId());
            if (student != null) {
                item.put("studentName", student.getRealName() != null ? student.getRealName() : student.getUsername());
                item.put("avatar", student.getAvatar());
            }
            return item;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getSignInStatus(Long classroomId, Long studentId) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        result.put("signInActive", classroom != null && classroom.getSignInActive() != null && classroom.getSignInActive() == 1);
        LambdaQueryWrapper<ClassroomParticipant> allSignedWrapper = new LambdaQueryWrapper<>();
        allSignedWrapper.eq(ClassroomParticipant::getClassroomId,classroomId).eq(ClassroomParticipant::getStudentId,studentId).isNotNull(ClassroomParticipant::getSignInTime);
        boolean alreadySigned = this.participantMapper.selectCount(allSignedWrapper) > 0L;
        LambdaQueryWrapper<ClassroomParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassroomParticipant::getClassroomId,classroomId).eq(ClassroomParticipant::getStudentId,studentId).isNull(ClassroomParticipant::getLeaveTime);
        ClassroomParticipant participant = (ClassroomParticipant)this.participantMapper.selectOne(wrapper);
        if (participant == null) {
            result.put("joined", false);
            result.put("signed", alreadySigned);
            result.put("signInTime", null);
        } else {
            result.put("joined", true);
            result.put("signed", alreadySigned);
            result.put("signInTime", participant.getSignInTime());
            result.put("joinTime", participant.getJoinTime());
        }
        return result;
    }

    public Map<String, Object> getClassroomMessages(Long classroomId, int pageNum, int pageSize) {
        Page<ClassroomMessage> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<ClassroomMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassroomMessage::getClassroomId,classroomId).orderByAsc(ClassroomMessage::getCreateTime);
        Page<ClassroomMessage> msgPage = (Page<ClassroomMessage>)this.messageMapper.selectPage((IPage<ClassroomMessage>)page, wrapper);
        List<Long> userIds = msgPage.getRecords().stream().map(ClassroomMessage::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            userMap = this.userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, userIds)).stream().collect(Collectors.toMap(User::getId, u -> u));
        }
        Map<Long, User> finalUserMap = userMap;
        List<Map<String, Object>> records = msgPage.getRecords().stream().map(msg -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", msg.getId());
            item.put("userId", msg.getUserId());
            item.put("content", msg.getContent());
            item.put("messageType", msg.getMessageType());
            item.put("createTime", msg.getCreateTime());
            User user = (User)finalUserMap.get(msg.getUserId());
            if (user != null) {
                item.put("username", user.getRealName() != null ? user.getRealName() : user.getUsername());
                item.put("avatar", user.getAvatar());
            }
            return item;
        }).collect(Collectors.toList());
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("records", records);
        result.put("total", msgPage.getTotal());
        return result;
    }

    public ClassroomService(ClassroomMapper classroomMapper, ClassroomParticipantMapper participantMapper, ClassroomMessageMapper messageMapper, UserMapper userMapper, TeacherClassMapper teacherClassMapper, ClassStudentMapper classStudentMapper) {
        this.classroomMapper = classroomMapper;
        this.participantMapper = participantMapper;
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
        this.teacherClassMapper = teacherClassMapper;
        this.classStudentMapper = classStudentMapper;
    }
}

