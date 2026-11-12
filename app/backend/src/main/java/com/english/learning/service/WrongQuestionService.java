package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.Question;
import com.english.learning.entity.WrongQuestion;
import com.english.learning.mapper.QuestionMapper;
import com.english.learning.mapper.WrongQuestionMapper;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WrongQuestionService {
    private static final Logger log = LoggerFactory.getLogger(WrongQuestionService.class);
    private final WrongQuestionMapper wrongQuestionMapper;
    private final QuestionMapper questionMapper;

    public Page<Map<String, Object>> getWrongQuestionPage(int pageNum, int pageSize, Long studentId, Integer masteryLevel, String questionType, Integer days, String keyword) {
        Map<Long, Question> questionMap;
        Page<WrongQuestion> wrongPage;
        List<Long> questionIds;
        List<Long> filteredQuestionIds = null;
        if (questionType != null && !questionType.isEmpty() || keyword != null && !keyword.isEmpty()) {
            List<Question> matchedQuestions;
            LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
            if (questionType != null && !questionType.isEmpty()) {
                qWrapper.eq(Question::getQuestionType,questionType);
            }
            if (keyword != null && !keyword.isEmpty()) {
                qWrapper.and(w -> w.like(Question::getTitle, keyword).or().like(Question::getContent, keyword));
            }
            if ((filteredQuestionIds = (matchedQuestions = this.questionMapper.selectList(qWrapper)).stream().map(Question::getId).collect(Collectors.toList())).isEmpty()) {
                return new Page<>((long)pageNum, (long)pageSize, 0L);
            }
        }
        Page<WrongQuestion> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getStudentId,studentId).eq(masteryLevel != null, WrongQuestion::getMasteryLevel,masteryLevel).orderByDesc(WrongQuestion::getLastWrongTime);
        if (days != null && days > 0) {
            wrapper.ge(WrongQuestion::getLastWrongTime,LocalDateTime.now().minusDays(days.intValue()));
        }
        if (filteredQuestionIds != null) {
            wrapper.in(WrongQuestion::getQuestionId, filteredQuestionIds);
        }
        if (!(questionIds = (wrongPage = (Page<WrongQuestion>)this.wrongQuestionMapper.selectPage((IPage<WrongQuestion>)page, wrapper)).getRecords().stream().map(WrongQuestion::getQuestionId).collect(Collectors.toList())).isEmpty()) {
            List<Question> questions = this.questionMapper.selectList(new LambdaQueryWrapper<Question>().in(Question::getId, questionIds));
            questionMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));
        } else {
            questionMap = new HashMap<>();
        }
        Page<Map<String, Object>> resultPage = new Page<>((long)pageNum, (long)pageSize, wrongPage.getTotal());
        Map<Long, Question> finalQuestionMap = questionMap;
        List<Map<String, Object>> records = wrongPage.getRecords().stream().map(wq -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", wq.getId());
            item.put("questionId", wq.getQuestionId());
            item.put("wrongCount", wq.getWrongCount());
            item.put("lastWrongTime", wq.getLastWrongTime());
            item.put("masteryLevel", wq.getMasteryLevel());
            item.put("notes", wq.getNotes());
            Question question = finalQuestionMap.get(wq.getQuestionId());
            if (question != null) {
                item.put("questionType", question.getQuestionType());
                item.put("title", question.getTitle());
                item.put("content", question.getContent());
                item.put("options", question.getOptions());
                item.put("answer", question.getAnswer());
                item.put("analysis", question.getAnalysis());
                item.put("difficulty", question.getDifficulty());
            }
            return item;
        }).collect(Collectors.toList());
        resultPage.setRecords(records);
        return resultPage;
    }

    public void addWrongQuestion(Long studentId, Long questionId) {
        WrongQuestion existing = this.wrongQuestionMapper.selectOne(new LambdaQueryWrapper<WrongQuestion>().eq(WrongQuestion::getStudentId,studentId).eq(WrongQuestion::getQuestionId,questionId));
        if (existing != null) {
            existing.setWrongCount(Integer.valueOf(existing.getWrongCount() + 1));
            existing.setLastWrongTime(LocalDateTime.now());
            existing.setMasteryLevel(0);
            this.wrongQuestionMapper.updateById(existing);
        } else {
            WrongQuestion wq = new WrongQuestion();
            wq.setStudentId(studentId);
            wq.setQuestionId(questionId);
            wq.setWrongCount(1);
            wq.setLastWrongTime(LocalDateTime.now());
            wq.setMasteryLevel(0);
            this.wrongQuestionMapper.insert(wq);
        }
    }

    public void updateMasteryLevel(Long wrongQuestionId, Long studentId, Integer masteryLevel) {
        WrongQuestion wq = (WrongQuestion)this.wrongQuestionMapper.selectById(wrongQuestionId);
        if (wq == null) {
            throw new BusinessException("\u9519\u9898\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        if (!wq.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u4fee\u6539\u6b64\u8bb0\u5f55");
        }
        wq.setMasteryLevel(masteryLevel);
        this.wrongQuestionMapper.updateById(wq);
    }

    public void addNotes(Long wrongQuestionId, Long studentId, String notes) {
        WrongQuestion wq = (WrongQuestion)this.wrongQuestionMapper.selectById(wrongQuestionId);
        if (wq == null) {
            throw new BusinessException("\u9519\u9898\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        if (!wq.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u4fee\u6539\u6b64\u8bb0\u5f55");
        }
        wq.setNotes(notes);
        this.wrongQuestionMapper.updateById(wq);
    }

    public void deleteWrongQuestion(Long wrongQuestionId, Long studentId) {
        WrongQuestion wq = (WrongQuestion)this.wrongQuestionMapper.selectById(wrongQuestionId);
        if (wq == null) {
            throw new BusinessException("\u9519\u9898\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        if (!wq.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u5220\u9664\u6b64\u8bb0\u5f55");
        }
        this.wrongQuestionMapper.deleteById(wrongQuestionId);
    }

    public Map<String, Object> getStatistics(Long studentId) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        long total = this.wrongQuestionMapper.selectCount(new LambdaQueryWrapper<WrongQuestion>().eq(WrongQuestion::getStudentId,studentId));
        result.put("total", total);
        long notMastered = this.wrongQuestionMapper.selectCount(new LambdaQueryWrapper<WrongQuestion>().eq(WrongQuestion::getStudentId,studentId).eq(WrongQuestion::getMasteryLevel,0));
        result.put("notMastered", notMastered);
        long basicMastered = this.wrongQuestionMapper.selectCount(new LambdaQueryWrapper<WrongQuestion>().eq(WrongQuestion::getStudentId,studentId).eq(WrongQuestion::getMasteryLevel,1));
        result.put("basicMastered", basicMastered);
        long mastered = this.wrongQuestionMapper.selectCount(new LambdaQueryWrapper<WrongQuestion>().eq(WrongQuestion::getStudentId,studentId).eq(WrongQuestion::getMasteryLevel,2));
        result.put("mastered", mastered);
        return result;
    }

    public Map<String, Object> getWrongQuestionDetail(Long id, Long studentId) {
        WrongQuestion wq = (WrongQuestion)this.wrongQuestionMapper.selectById(id);
        if (wq == null || !wq.getStudentId().equals(studentId)) {
            throw new BusinessException("\u9519\u9898\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("id", wq.getId());
        result.put("questionId", wq.getQuestionId());
        result.put("wrongCount", wq.getWrongCount());
        result.put("lastWrongTime", wq.getLastWrongTime());
        result.put("masteryLevel", wq.getMasteryLevel());
        result.put("notes", wq.getNotes());
        Question question = (Question)this.questionMapper.selectById(wq.getQuestionId());
        if (question != null) {
            result.put("questionType", question.getQuestionType());
            result.put("title", question.getTitle());
            result.put("content", question.getContent());
            result.put("options", question.getOptions());
            result.put("answer", question.getAnswer());
            result.put("analysis", question.getAnalysis());
            result.put("difficulty", question.getDifficulty());
        }
        return result;
    }

    public List<Map<String, Object>> getPracticeQuestions(Long studentId, int limit, String type) {
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getStudentId,studentId).lt(WrongQuestion::getMasteryLevel,3).orderByDesc(WrongQuestion::getWrongCount).last("LIMIT " + limit);
        List<WrongQuestion> wrongQuestions = this.wrongQuestionMapper.selectList(wrapper);
        List<Long> questionIds = wrongQuestions.stream().map(WrongQuestion::getQuestionId).collect(Collectors.toList());
        if (questionIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.in(Question::getId, questionIds);
        if (type != null && !type.isEmpty()) {
            qWrapper.eq(Question::getQuestionType,type);
        }
        List<Question> questions = this.questionMapper.selectList(qWrapper);
        Map<Long, WrongQuestion> wrongMap = wrongQuestions.stream().collect(Collectors.toMap(WrongQuestion::getQuestionId, w -> w));
        return questions.stream().map((Question q) -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", q.getId());
            item.put("type", q.getQuestionType());
            item.put("title", q.getTitle());
            item.put("content", q.getContent());
            item.put("options", q.getOptions());
            item.put("correctAnswer", q.getAnswer());
            item.put("analysis", q.getAnalysis());
            item.put("category", q.getCategoryId());
            item.put("difficulty", q.getDifficulty());
            WrongQuestion wq = wrongMap.get(q.getId());
            if (wq != null) {
                item.put("errorCount", wq.getWrongCount());
                item.put("masteryLevel", wq.getMasteryLevel());
            }
            return item;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSimilarQuestions(Long wrongQuestionId, Long studentId, int limit) {
        WrongQuestion wq = (WrongQuestion)this.wrongQuestionMapper.selectById(wrongQuestionId);
        if (wq == null) {
            return List.of();
        }
        Question originalQuestion = (Question)this.questionMapper.selectById(wq.getQuestionId());
        if (originalQuestion == null) {
            return List.of();
        }
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getQuestionType,originalQuestion.getQuestionType()).eq(originalQuestion.getCategoryId() != null, Question::getCategoryId,originalQuestion.getCategoryId()).ne(Question::getId,originalQuestion.getId()).last("LIMIT " + limit);
        List<Question> similarQuestions = this.questionMapper.selectList(wrapper);
        return similarQuestions.stream().map((Question q) -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", q.getId());
            item.put("type", q.getQuestionType());
            item.put("title", q.getTitle());
            item.put("content", q.getContent());
            item.put("options", q.getOptions());
            item.put("correctAnswer", q.getAnswer());
            item.put("analysis", q.getAnalysis());
            item.put("category", q.getCategoryId());
            item.put("difficulty", q.getDifficulty());
            item.put("errorCount", 0);
            return item;
        }).collect(Collectors.toList());
    }

    public void recordPracticeResult(Long studentId, Long questionId, boolean isCorrect) {
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getStudentId,studentId).eq(WrongQuestion::getQuestionId,questionId);
        WrongQuestion wq = this.wrongQuestionMapper.selectOne(wrapper);
        if (wq != null) {
            if (isCorrect) {
                int newLevel = Math.min(wq.getMasteryLevel() + 1, 3);
                wq.setMasteryLevel(Integer.valueOf(newLevel));
            } else {
                wq.setWrongCount(Integer.valueOf(wq.getWrongCount() + 1));
                wq.setLastWrongTime(LocalDateTime.now());
                wq.setMasteryLevel(Integer.valueOf(Math.max(wq.getMasteryLevel() - 1, 0)));
            }
            this.wrongQuestionMapper.updateById(wq);
        } else if (!isCorrect) {
            this.addWrongQuestion(studentId, questionId);
        }
    }

    public WrongQuestionService(WrongQuestionMapper wrongQuestionMapper, QuestionMapper questionMapper) {
        this.wrongQuestionMapper = wrongQuestionMapper;
        this.questionMapper = questionMapper;
    }
}

