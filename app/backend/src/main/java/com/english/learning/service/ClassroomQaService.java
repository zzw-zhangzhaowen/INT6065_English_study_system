package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.Classroom;
import com.english.learning.entity.ClassroomAnswer;
import com.english.learning.entity.ClassroomQuestion;
import com.english.learning.entity.ClassroomQuestionLike;
import com.english.learning.entity.User;
import com.english.learning.mapper.ClassroomAnswerMapper;
import com.english.learning.mapper.ClassroomMapper;
import com.english.learning.mapper.ClassroomQuestionLikeMapper;
import com.english.learning.mapper.ClassroomQuestionMapper;
import com.english.learning.mapper.UserMapper;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClassroomQaService {
    private static final Logger log = LoggerFactory.getLogger(ClassroomQaService.class);
    private final ClassroomMapper classroomMapper;
    private final ClassroomQuestionMapper questionMapper;
    private final ClassroomAnswerMapper answerMapper;
    private final ClassroomQuestionLikeMapper questionLikeMapper;
    private final UserMapper userMapper;

    public ClassroomQuestion createQuestion(Long classroomId, Long userId, String content, Boolean isAnonymous) {
        ClassroomQuestion question = new ClassroomQuestion();
        question.setClassroomId(classroomId);
        question.setUserId(userId);
        question.setContent(content);
        question.setStatus("OPEN");
        question.setIsAnonymous(Boolean.valueOf(isAnonymous != null && isAnonymous != false));
        question.setLikeCount(0);
        this.questionMapper.insert(question);
        return (ClassroomQuestion)this.questionMapper.selectById(question.getId());
    }

    public List<Map<String, Object>> getQuestions(Long classroomId) {
        LambdaQueryWrapper<ClassroomQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassroomQuestion::getClassroomId, classroomId)
               .orderByDesc(ClassroomQuestion::getLikeCount)
               .orderByDesc(ClassroomQuestion::getCreateTime);
        List<ClassroomQuestion> questions = this.questionMapper.selectList(wrapper);
        return questions.stream().map(q -> {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", q.getId());
            map.put("classroomId", q.getClassroomId());
            map.put("userId", q.getUserId());
            map.put("content", q.getContent());
            map.put("status", q.getStatus());
            map.put("isAnonymous", q.getIsAnonymous());
            map.put("likeCount", q.getLikeCount());
            map.put("createTime", q.getCreateTime());
            if (Boolean.TRUE.equals(q.getIsAnonymous())) {
                map.put("username", "\u533f\u540d\u7528\u6237");
                map.put("avatar", null);
            } else {
                User user = (User)this.userMapper.selectById(q.getUserId());
                map.put("username", user != null ? (user.getRealName() != null ? user.getRealName() : user.getUsername()) : "\u672a\u77e5");
                map.put("avatar", user != null ? user.getAvatar() : null);
            }
            Long answerCount = this.answerMapper.selectCount(new LambdaQueryWrapper<ClassroomAnswer>().eq(ClassroomAnswer::getQuestionId, q.getId()));
            map.put("answerCount", answerCount);
            return map;
        }).collect(Collectors.toList());
    }

    public ClassroomAnswer createAnswer(Long questionId, Long classroomId, Long userId, String content) {
        ClassroomQuestion question = (ClassroomQuestion)this.questionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException("\u95ee\u9898\u4e0d\u5b58\u5728");
        }
        if (!question.getClassroomId().equals(classroomId)) {
            throw new BusinessException("\u95ee\u9898\u4e0d\u5c5e\u4e8e\u5f53\u524d\u8bfe\u5802");
        }
        ClassroomAnswer answer = new ClassroomAnswer();
        answer.setQuestionId(questionId);
        answer.setClassroomId(classroomId);
        answer.setUserId(userId);
        answer.setContent(content);
        answer.setIsBest(Boolean.valueOf(false));
        this.answerMapper.insert(answer);
        if ("OPEN".equals(question.getStatus())) {
            question.setStatus("ANSWERED");
            this.questionMapper.updateById(question);
        }
        return (ClassroomAnswer)this.answerMapper.selectById(answer.getId());
    }

    public List<Map<String, Object>> getAnswers(Long classroomId, Long questionId) {
        ClassroomQuestion question = (ClassroomQuestion)this.questionMapper.selectById(questionId);
        if (question == null || !question.getClassroomId().equals(classroomId)) {
            throw new BusinessException("\u95ee\u9898\u4e0d\u5b58\u5728");
        }
        LambdaQueryWrapper<ClassroomAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassroomAnswer::getQuestionId, questionId)
               .orderByDesc(ClassroomAnswer::getIsBest)
               .orderByAsc(ClassroomAnswer::getCreateTime);
        List<ClassroomAnswer> answers = this.answerMapper.selectList(wrapper);
        return answers.stream().map(a -> {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", a.getId());
            map.put("questionId", a.getQuestionId());
            map.put("userId", a.getUserId());
            map.put("content", a.getContent());
            map.put("isBest", a.getIsBest());
            map.put("createTime", a.getCreateTime());
            User user = (User)this.userMapper.selectById(a.getUserId());
            map.put("username", user != null ? (user.getRealName() != null ? user.getRealName() : user.getUsername()) : "\u672a\u77e5");
            map.put("avatar", user != null ? user.getAvatar() : null);
            return map;
        }).collect(Collectors.toList());
    }

    public int likeQuestion(Long classroomId, Long questionId, Long userId) {
        ClassroomQuestion question = (ClassroomQuestion)this.questionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException("\u95ee\u9898\u4e0d\u5b58\u5728");
        }
        if (!question.getClassroomId().equals(classroomId)) {
            throw new BusinessException("\u95ee\u9898\u4e0d\u5c5e\u4e8e\u5f53\u524d\u8bfe\u5802");
        }
        Long existingLike = this.questionLikeMapper.selectCount(new LambdaQueryWrapper<ClassroomQuestionLike>()
            .eq(ClassroomQuestionLike::getQuestionId, questionId)
            .eq(ClassroomQuestionLike::getUserId, userId));
        if (existingLike > 0L) {
            throw new BusinessException("\u60a8\u5df2\u7ecf\u70b9\u8fc7\u8d5e\u4e86");
        }
        ClassroomQuestionLike like = new ClassroomQuestionLike();
        like.setQuestionId(questionId);
        like.setUserId(userId);
        this.questionLikeMapper.insert(like);
        question.setLikeCount(Integer.valueOf(question.getLikeCount() + 1));
        this.questionMapper.updateById(question);
        return question.getLikeCount();
    }

    public void closeQuestion(Long classroomId, Long questionId, Long userId, boolean isAdmin) {
        ClassroomQuestion question = (ClassroomQuestion)this.questionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException("\u95ee\u9898\u4e0d\u5b58\u5728");
        }
        if (!question.getClassroomId().equals(classroomId)) {
            throw new BusinessException("\u95ee\u9898\u4e0d\u5c5e\u4e8e\u5f53\u524d\u8bfe\u5802");
        }
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("\u8bfe\u5802\u4e0d\u5b58\u5728");
        }
        if (!isAdmin && !Objects.equals(classroom.getTeacherId(), userId)) {
            throw new BusinessException("\u65e0\u6743\u5173\u95ed\u95ee\u9898");
        }
        question.setStatus("CLOSED");
        this.questionMapper.updateById(question);
    }

    @Transactional(rollbackFor={Exception.class})
    public void markBestAnswer(Long classroomId, Long answerId, Long userId, boolean isAdmin) {
        ClassroomAnswer answer = (ClassroomAnswer)this.answerMapper.selectById(answerId);
        if (answer == null) {
            throw new BusinessException("\u56de\u7b54\u4e0d\u5b58\u5728");
        }
        ClassroomQuestion question = (ClassroomQuestion)this.questionMapper.selectById(answer.getQuestionId());
        if (question == null || !question.getClassroomId().equals(classroomId)) {
            throw new BusinessException("\u95ee\u9898\u4e0d\u5b58\u5728");
        }
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException("\u8bfe\u5802\u4e0d\u5b58\u5728");
        }
        if (!isAdmin && !Objects.equals(classroom.getTeacherId(), userId)) {
            throw new BusinessException("\u65e0\u6743\u6807\u8bb0\u6700\u4f73\u56de\u7b54");
        }
        LambdaQueryWrapper<ClassroomAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassroomAnswer::getQuestionId, answer.getQuestionId())
               .eq(ClassroomAnswer::getIsBest, true);
        List<ClassroomAnswer> oldBests = this.answerMapper.selectList(wrapper);
        for (ClassroomAnswer old : oldBests) {
            old.setIsBest(Boolean.valueOf(false));
            this.answerMapper.updateById(old);
        }
        answer.setIsBest(Boolean.valueOf(true));
        this.answerMapper.updateById(answer);
    }

    public ClassroomQaService(ClassroomMapper classroomMapper, ClassroomQuestionMapper questionMapper, ClassroomAnswerMapper answerMapper, ClassroomQuestionLikeMapper questionLikeMapper, UserMapper userMapper) {
        this.classroomMapper = classroomMapper;
        this.questionMapper = questionMapper;
        this.answerMapper = answerMapper;
        this.questionLikeMapper = questionLikeMapper;
        this.userMapper = userMapper;
    }
}

