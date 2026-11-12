package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.dto.ExamRequest;
import com.english.learning.dto.ExamSubmitRequest;
import com.english.learning.entity.AnswerRecord;
import com.english.learning.entity.Exam;
import com.english.learning.entity.ExamQuestion;
import com.english.learning.entity.ExamRecord;
import com.english.learning.entity.Passage;
import com.english.learning.entity.Question;
import com.english.learning.entity.User;
import com.english.learning.mapper.AnswerRecordMapper;
import com.english.learning.mapper.ExamMapper;
import com.english.learning.mapper.ExamQuestionMapper;
import com.english.learning.mapper.ExamRecordMapper;
import com.english.learning.mapper.PassageMapper;
import com.english.learning.mapper.QuestionMapper;
import com.english.learning.mapper.UserMapper;
import com.english.learning.service.WrongQuestionService;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExamService {
    private static final Logger log = LoggerFactory.getLogger(ExamService.class);
    private final ExamMapper examMapper;
    private final ExamRecordMapper examRecordMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final QuestionMapper questionMapper;
    private final AnswerRecordMapper answerRecordMapper;
    private final WrongQuestionService wrongQuestionService;
    private final PassageMapper passageMapper;
    private final UserMapper userMapper;

    public Page<Exam> getExamPage(int pageNum, int pageSize, String examType, String level, String keyword, String status) {
        return this.getExamPage(pageNum, pageSize, examType, level, keyword, status, false, null);
    }

    public Page<Exam> getExamPage(int pageNum, int pageSize, String examType, String level, String keyword, String status, boolean forTeacher, Long teacherId) {
        Page<Exam> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(examType != null && !examType.isEmpty(), Exam::getExamType,examType).eq(level != null && !level.isEmpty(), Exam::getLevel,level).like(keyword != null && !keyword.isEmpty(), Exam::getExamName, keyword);
        if (forTeacher && teacherId != null) {
            wrapper.eq(Exam::getCreatorId,teacherId);
        } else {
            wrapper.eq(Exam::getStatus,1);
        }
        if (status != null && !status.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            switch (status) {
                case "pending": {
                    wrapper.and(w -> w.gt(Exam::getStartTime,now).or().isNull(Exam::getStartTime));
                    break;
                }
                case "ongoing": {
                    wrapper.le(Exam::getStartTime,now).and(w -> w.ge(Exam::getEndTime,now).or(inner -> inner.isNull(Exam::getEndTime).apply("DATE_ADD(start_time, INTERVAL duration MINUTE) >= {0}", new Object[]{now})));
                    break;
                }
                case "finished": {
                    wrapper.and(w -> w.lt(Exam::getEndTime,now).or(inner -> inner.isNull(Exam::getEndTime).apply("DATE_ADD(start_time, INTERVAL duration MINUTE) < {0}", new Object[]{now})));
                }
            }
        }
        wrapper.orderByDesc(Exam::getCreateTime);
        Page<Exam> result = (Page<Exam>)this.examMapper.selectPage((IPage<Exam>)page, wrapper);
        if (!result.getRecords().isEmpty()) {
            result.getRecords().forEach(exam -> {
                long questionCount = this.examQuestionMapper.selectCount(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,exam.getId()));
                exam.setQuestionCount(Integer.valueOf((int)questionCount));
            });
        }
        return result;
    }

    public Map<String, Object> getExamDetail(Long examId) {
        Exam exam = this.examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("exam", exam);
        List<ExamQuestion> examQuestions = this.examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,examId));
        result.put("questionCount", examQuestions.size());
        if (!examQuestions.isEmpty()) {
            List<Long> questionIds = examQuestions.stream().map(ExamQuestion::getQuestionId).collect(Collectors.toList());
            List<Question> questions = this.questionMapper.selectList(new LambdaQueryWrapper<Question>().in(Question::getId, questionIds));
            Map<String, Long> typeCountMap = questions.stream().collect(Collectors.groupingBy(q -> q.getQuestionType() != null ? q.getQuestionType() : "\u5176\u4ed6", Collectors.counting()));
            List<Map<String, Object>> questionTypes = typeCountMap.entrySet().stream().map(entry -> {
                HashMap<String, Object> typeMap = new HashMap<>();
                typeMap.put("name", this.getQuestionTypeName((String)entry.getKey()));
                typeMap.put("type", entry.getKey());
                typeMap.put("count", entry.getValue());
                return typeMap;
            }).collect(Collectors.toList());
            result.put("questionTypes", questionTypes);
        } else {
            result.put("questionTypes", List.of());
        }
        return result;
    }

    private String getQuestionTypeName(String type) {
        switch (type) {
            case "SINGLE_CHOICE": {
                return "\u5355\u9009\u9898";
            }
            case "MULTI_CHOICE": {
                return "\u591a\u9009\u9898";
            }
            case "TRUE_FALSE": {
                return "\u5224\u65ad\u9898";
            }
            case "FILL_BLANK": {
                return "\u586b\u7a7a\u9898";
            }
            case "SHORT_ANSWER": {
                return "\u7b80\u7b54\u9898";
            }
            case "READING": {
                return "\u9605\u8bfb\u7406\u89e3";
            }
            case "LISTENING": {
                return "\u542c\u529b\u9898";
            }
            case "TRANSLATION": {
                return "\u7ffb\u8bd1\u9898";
            }
            case "WRITING": {
                return "\u5199\u4f5c\u9898";
            }
            case "CLOZE": {
                return "\u5b8c\u5f62\u586b\u7a7a";
            }
        }
        return type;
    }

    @Transactional(rollbackFor={Exception.class})
    public Map<String, Object> startExam(Long examId, Long studentId) {
        Exam exam = (Exam)this.examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        LocalDateTime now = LocalDateTime.now();
        if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
            throw new BusinessException("\u8003\u8bd5\u5c1a\u672a\u5f00\u59cb");
        }
        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            throw new BusinessException("\u8003\u8bd5\u5df2\u7ed3\u675f");
        }
        long attemptCount = this.examRecordMapper.selectCount(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getExamId,examId).eq(ExamRecord::getStudentId,studentId));
        if (attemptCount >= (long)exam.getMaxAttempts().intValue()) {
            throw new BusinessException("\u5df2\u8d85\u8fc7\u6700\u5927\u8003\u8bd5\u6b21\u6570");
        }
        ExamRecord inProgressRecord = this.examRecordMapper.selectOne(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getExamId,examId).eq(ExamRecord::getStudentId,studentId).eq(ExamRecord::getStatus, "IN_PROGRESS"));
        if (inProgressRecord != null) {
            HashMap<String, Object> result = new HashMap<String, Object>();
            result.put("recordId", inProgressRecord.getId());
            result.put("duration", exam.getDuration());
            result.put("questions", this.getExamQuestions(examId));
            result.put("remainingTime", this.calculateRemainingTime(inProgressRecord, exam.getDuration()));
            return result;
        }
        ExamRecord record = new ExamRecord();
        record.setExamId(examId);
        record.setStudentId(studentId);
        record.setStartTime(now);
        record.setTotalScore(exam.getTotalScore());
        record.setObtainedScore(BigDecimal.ZERO);
        record.setStatus("IN_PROGRESS");
        record.setCheatWarningCount(0);
        this.examRecordMapper.insert(record);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("recordId", record.getId());
        result.put("duration", exam.getDuration());
        result.put("questions", this.getExamQuestions(examId));
        result.put("remainingTime", exam.getDuration() * 60);
        return result;
    }

    private List<Map<String, Object>> getExamQuestions(Long examId) {
        Map<Long, Passage> passageMap;
        List<ExamQuestion> examQuestions = this.examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,examId).orderByAsc(ExamQuestion::getQuestionOrder));
        List<Long> questionIds = examQuestions.stream().map(ExamQuestion::getQuestionId).collect(Collectors.toList());
        if (questionIds.isEmpty()) {
            return List.of();
        }
        List<Question> questions = this.questionMapper.selectList(new LambdaQueryWrapper<Question>().in(Question::getId, questionIds));
        Map<Long, Question> questionMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));
        List<Long> passageIds = questions.stream().map(Question::getPassageId).filter(passageId -> passageId != null).distinct().collect(Collectors.toList());
        if (!passageIds.isEmpty()) {
            List<Passage> passages = this.passageMapper.selectList(new LambdaQueryWrapper<Passage>().in(Passage::getId, passageIds));
            passageMap = passages.stream().collect(Collectors.toMap(Passage::getId, p -> p));
        } else {
            passageMap = new HashMap<>();
        }
        return examQuestions.stream().map(eq -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("examQuestionId", eq.getId());
            item.put("questionId", eq.getQuestionId());
            item.put("order", eq.getQuestionOrder());
            item.put("score", eq.getQuestionScore());
            Question question = (Question)questionMap.get(eq.getQuestionId());
            if (question != null) {
                Passage passage;
                item.put("questionType", question.getQuestionType());
                item.put("title", question.getTitle());
                item.put("content", question.getContent());
                item.put("options", question.getOptions());
                item.put("difficulty", question.getDifficulty());
                item.put("audioUrl", question.getAudioUrl());
                item.put("imageUrl", question.getImageUrl());
                if (question.getPassageId() != null && (passage = (Passage)passageMap.get(question.getPassageId())) != null) {
                    item.put("passage", passage.getContent());
                    item.put("passageTitle", passage.getTitle());
                }
            }
            return item;
        }).collect(Collectors.toList());
    }

    private long calculateRemainingTime(ExamRecord record, Integer duration) {
        long elapsedSeconds = ChronoUnit.SECONDS.between(record.getStartTime(), LocalDateTime.now());
        long totalSeconds = (long)duration.intValue() * 60L;
        return Math.max(0L, totalSeconds - elapsedSeconds);
    }

    @Transactional(rollbackFor={Exception.class})
    public Map<String, Object> submitExam(Long recordId, ExamSubmitRequest request) {
        ExamRecord record = (ExamRecord)this.examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("\u8003\u8bd5\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        if (!"IN_PROGRESS".equals(record.getStatus())) {
            throw new BusinessException("\u8003\u8bd5\u5df2\u7ed3\u675f");
        }
        Exam exam = (Exam)this.examMapper.selectById(record.getExamId());
        List<ExamQuestion> examQuestions = this.examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,record.getExamId()));
        Map<Long, ExamQuestion> examQuestionMap = examQuestions.stream().collect(Collectors.toMap(ExamQuestion::getQuestionId, eq -> eq, (existing, replacement) -> existing));
        List<Long> questionIds = examQuestions.stream().map(ExamQuestion::getQuestionId).distinct().collect(Collectors.toList());
        Map<Long, Question> questionMap = new HashMap<>();
        if (!questionIds.isEmpty()) {
            List<Question> questions = this.questionMapper.selectList(new LambdaQueryWrapper<Question>().in(Question::getId, questionIds));
            questionMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));
        }
        BigDecimal totalObtainedScore = BigDecimal.ZERO;
        int correctCount = 0;
        int wrongCount = 0;
        log.info("\u5f00\u59cb\u6279\u6539\u7b54\u6848 - recordId: {}, \u7b54\u9898\u6570: {}",recordId,request.getAnswers().size());
        this.answerRecordMapper.delete(new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId,recordId));
        for (ExamSubmitRequest.AnswerItem answerItem : request.getAnswers()) {
            Question question = (Question)questionMap.get(answerItem.getQuestionId());
            ExamQuestion examQuestion = examQuestionMap.get(answerItem.getQuestionId());
            if (question == null || examQuestion == null) {
                log.warn("\u9898\u76ee\u4e0d\u5b58\u5728 - questionId: {}",answerItem.getQuestionId());
                continue;
            }
            boolean isCorrect = this.checkAnswer(question, answerItem.getAnswer());
            BigDecimal score = isCorrect ? examQuestion.getQuestionScore() : BigDecimal.ZERO;
            log.info("\u9898\u76ee {} \u5224\u5206: isCorrect={}, score={}, questionScore={}", new Object[]{answerItem.getQuestionId(), isCorrect, score, examQuestion.getQuestionScore()});
            AnswerRecord answerRecord = new AnswerRecord();
            answerRecord.setExamRecordId(recordId);
            answerRecord.setQuestionId(answerItem.getQuestionId());
            answerRecord.setStudentAnswer(answerItem.getAnswer());
            answerRecord.setIsCorrect(Integer.valueOf(isCorrect ? 1 : 0));
            answerRecord.setObtainedScore(score);
            answerRecord.setAnswerTime(answerItem.getAnswerTime());
            this.answerRecordMapper.insert(answerRecord);
            try {
                this.questionMapper.incrementUsageCount(answerItem.getQuestionId());
                this.questionMapper.updateCorrectRate(answerItem.getQuestionId(), Integer.valueOf(isCorrect ? 100 : 0));
                log.debug("\u66f4\u65b0\u9898\u76ee\u7edf\u8ba1 - questionId: {}, isCorrect: {}",answerItem.getQuestionId(),isCorrect);
            }
            catch (Exception e) {
                log.error("\u66f4\u65b0\u9898\u76ee\u7edf\u8ba1\u5931\u8d25 - questionId: {}",answerItem.getQuestionId(),e);
            }
            if (isCorrect) {
                ++correctCount;
                totalObtainedScore = totalObtainedScore.add(score);
                continue;
            }
            ++wrongCount;
            this.wrongQuestionService.addWrongQuestion(record.getStudentId(), answerItem.getQuestionId());
        }
        log.info("\u6279\u6539\u5b8c\u6210 - totalObtainedScore: {}, correctCount: {}, wrongCount: {}", new Object[]{totalObtainedScore, correctCount, wrongCount});
        LocalDateTime now = LocalDateTime.now();
        record.setEndTime(now);
        record.setSubmitTime(now);
        record.setObtainedScore(totalObtainedScore);
        record.setCorrectCount(Integer.valueOf(correctCount));
        record.setWrongCount(Integer.valueOf(wrongCount));
        record.setStatus("COMPLETED");
        long duration = ChronoUnit.MINUTES.between(record.getStartTime(), now);
        record.setDuration(Integer.valueOf((int)duration));
        log.info("\u66f4\u65b0\u8003\u8bd5\u8bb0\u5f55 - recordId: {}, obtainedScore: {}, correctCount: {}, wrongCount: {}", new Object[]{recordId, totalObtainedScore, correctCount, wrongCount});
        this.examRecordMapper.updateById(record);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("recordId", recordId);
        result.put("totalScore", record.getTotalScore());
        result.put("obtainedScore", totalObtainedScore);
        result.put("correctCount", correctCount);
        result.put("wrongCount", wrongCount);
        result.put("duration", duration);
        result.put("passed", totalObtainedScore.compareTo(exam.getPassScore()) >= 0);
        log.info("\u8003\u8bd5\u63d0\u4ea4\u6210\u529f - recordId: {}, \u8fd4\u56de\u7ed3\u679c: {}",recordId, result);
        return result;
    }

    private boolean checkAnswer(Question question, String studentAnswer) {
        if (studentAnswer == null || question.getAnswer() == null) {
            log.warn("\u7b54\u6848\u4e3a\u7a7a - questionId: {}, studentAnswer: {}, correctAnswer: {}", new Object[]{question.getId(), studentAnswer, question.getAnswer()});
            return false;
        }
        String correctAnswer = question.getAnswer().trim();
        String userAnswer = studentAnswer.trim();
        String questionType = question.getQuestionType();
        log.info("\u68c0\u67e5\u7b54\u6848 - questionId: {}, questionType: {}, userAnswer: {}, correctAnswer: {}", new Object[]{question.getId(), questionType, userAnswer, correctAnswer});
        if (questionType != null && (questionType.contains("CHOICE") || questionType.equals("LISTENING") || questionType.equals("READING") || questionType.equals("CLOZE"))) {
            int index;
            if (userAnswer.matches("\\d+")) {
                index = Integer.parseInt(userAnswer);
                userAnswer = String.valueOf((char)(65 + index));
                log.info("\u7528\u6237\u7b54\u6848\u6570\u5b57\u8f6c\u5b57\u6bcd: {} -> {}",studentAnswer,userAnswer);
            }
            if (correctAnswer.matches("\\d+")) {
                index = Integer.parseInt(correctAnswer);
                correctAnswer = String.valueOf((char)(65 + index));
                log.info("\u6807\u51c6\u7b54\u6848\u6570\u5b57\u8f6c\u5b57\u6bcd: {} -> {}",question.getAnswer(),correctAnswer);
            }
            boolean isCorrect = correctAnswer.equalsIgnoreCase(userAnswer);
            log.info("\u9009\u62e9\u9898\u5224\u5206\u7ed3\u679c: {}",isCorrect);
            return isCorrect;
        }
        if (questionType != null && (questionType.equals("WRITING") || questionType.equals("TRANSLATION"))) {
            boolean isCorrect = !userAnswer.isEmpty() && userAnswer.length() >= 50;
            log.info("\u4e3b\u89c2\u9898\u5224\u5206\u7ed3\u679c: {}, \u7b54\u6848\u957f\u5ea6: {}",isCorrect,userAnswer.length());
            return isCorrect;
        }
        boolean isCorrect = correctAnswer.equalsIgnoreCase(userAnswer);
        log.info("\u5176\u4ed6\u9898\u578b\u5224\u5206\u7ed3\u679c: {}",isCorrect);
        return isCorrect;
    }

    @Transactional(rollbackFor={Exception.class})
    public void submitExam(Long recordId, BigDecimal obtainedScore, Integer correctCount, Integer wrongCount) {
        ExamRecord record = (ExamRecord)this.examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("\u8003\u8bd5\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        LocalDateTime now = LocalDateTime.now();
        record.setEndTime(now);
        record.setSubmitTime(now);
        record.setObtainedScore(obtainedScore);
        record.setCorrectCount(correctCount);
        record.setWrongCount(wrongCount);
        record.setStatus("COMPLETED");
        this.examRecordMapper.updateById(record);
    }

    public Page<Map<String, Object>> getExamRecordPageWithExamName(int pageNum, int pageSize, Long studentId, String status) {
        Page<ExamRecord> recordPage = this.getExamRecordPage(pageNum, pageSize, studentId, status);
        List<Long> examIds = recordPage.getRecords().stream().map(ExamRecord::getExamId).distinct().collect(Collectors.toList());
        Map<Long, Exam> examMap = new HashMap<>();
        if (!examIds.isEmpty()) {
            List<Exam> exams = this.examMapper.selectList(new LambdaQueryWrapper<Exam>().in(Exam::getId, examIds));
            examMap = exams.stream().collect(Collectors.toMap(Exam::getId, e -> e, (a, b) -> a));
        }
        Map<Long, Exam> finalExamMap = examMap;
        List<Map<String, Object>> enrichedRecords = recordPage.getRecords().stream().map(record -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", record.getId());
            item.put("examId", record.getExamId());
            item.put("studentId", record.getStudentId());
            item.put("obtainedScore", record.getObtainedScore());
            item.put("totalScore", record.getTotalScore());
            item.put("correctCount", record.getCorrectCount());
            item.put("wrongCount", record.getWrongCount());
            item.put("status", record.getStatus());
            item.put("startTime", record.getStartTime());
            item.put("endTime", record.getEndTime());
            item.put("submitTime", record.getSubmitTime());
            item.put("createTime", record.getCreateTime());
            Exam exam = (Exam)finalExamMap.get(record.getExamId());
            if (exam != null) {
                item.put("examName", exam.getExamName());
                item.put("examLevel", exam.getLevel());
            }
            return item;
        }).collect(Collectors.toList());
        Page<Map<String, Object>> resultPage = new Page<>((long)pageNum, (long)pageSize, recordPage.getTotal());
        resultPage.setRecords(enrichedRecords);
        return resultPage;
    }

    public Page<ExamRecord> getExamRecordPage(int pageNum, int pageSize, Long studentId, String status) {
        Page<ExamRecord> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(studentId != null, ExamRecord::getStudentId,studentId).eq(status != null, ExamRecord::getStatus, status).orderByDesc(ExamRecord::getCreateTime);
        return (Page<ExamRecord>)this.examRecordMapper.selectPage(page, wrapper);
    }

    public Map<String, Object> getExamRecordDetail(Long recordId) {
        ExamRecord r;
        ExamRecord record = (ExamRecord)this.examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("\u8003\u8bd5\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        Exam exam = (Exam)this.examMapper.selectById(record.getExamId());
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("record", record);
        result.put("exam", exam);
        List<ExamRecord> allRecords = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getExamId,record.getExamId()).eq(ExamRecord::getStatus, "COMPLETED").orderByDesc(ExamRecord::getObtainedScore));
        int rank = 1;
        Iterator iterator = allRecords.iterator();
        while (iterator.hasNext() && !(r = (ExamRecord)iterator.next()).getId().equals(recordId)) {
            ++rank;
        }
        int totalCount = allRecords.size();
        int percentile = totalCount > 0 ? Math.round((float)(totalCount - rank) * 100.0f / (float)totalCount) : 0;
        result.put("rank", rank);
        result.put("percentile", percentile);
        List<AnswerRecord> answerRecords = this.answerRecordMapper.selectList(new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId,recordId));
        List<Long> questionIds = answerRecords.stream().map(AnswerRecord::getQuestionId).collect(Collectors.toList());
        if (!questionIds.isEmpty()) {
            List<Question> questions = this.questionMapper.selectList(new LambdaQueryWrapper<Question>().in(Question::getId, questionIds));
            Map<Long, Question> questionMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));
            List<ExamQuestion> examQuestions = this.examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,record.getExamId()).in(ExamQuestion::getQuestionId, questionIds));
            Map<Long, ExamQuestion> examQuestionMap = examQuestions.stream().collect(Collectors.toMap(ExamQuestion::getQuestionId, eq -> eq));
            List<Map<String, Object>> answerDetails = answerRecords.stream().map(ar -> {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("answerRecord", ar);
                Question question = (Question)questionMap.get(ar.getQuestionId());
                item.put("question", question);
                ExamQuestion examQuestion = (ExamQuestion)examQuestionMap.get(ar.getQuestionId());
                if (examQuestion != null) {
                    item.put("questionScore", examQuestion.getQuestionScore());
                }
                return item;
            }).collect(Collectors.toList());
            result.put("answerDetails", answerDetails);
        }
        return result;
    }


    @Transactional(rollbackFor={Exception.class})
    public void createExam(ExamRequest request, Long creatorId) {
        Exam exam = new Exam();
        exam.setExamName(request.getExamName());
        exam.setExamType(request.getExamType());
        exam.setLevel(request.getLevel());
        exam.setDescription(request.getDescription());
        exam.setTotalScore(request.getTotalScore() != null ? request.getTotalScore() : new BigDecimal("100"));
        exam.setPassScore(request.getPassScore() != null ? request.getPassScore() : new BigDecimal("60"));
        exam.setDuration(request.getDuration());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getStartTime().plusMinutes(request.getDuration().intValue()));
        exam.setMaxAttempts(Integer.valueOf(request.getMaxAttempts() != null ? request.getMaxAttempts() : 1));
        exam.setEnableAntiCheat(Integer.valueOf(request.getEnableAntiCheat() != null ? request.getEnableAntiCheat() : 1));
        exam.setRandomQuestion(Integer.valueOf(request.getRandomQuestion() != null ? request.getRandomQuestion() : 0));
        exam.setRandomOption(Integer.valueOf(request.getRandomOption() != null ? request.getRandomOption() : 0));
        exam.setShowResult(Integer.valueOf(request.getShowResult() != null ? request.getShowResult() : 1));
        exam.setCreatorId(creatorId);
        exam.setStatus(0);
        this.examMapper.insert(exam);
        log.info("\u521b\u5efa\u8003\u8bd5: {}",exam.getExamName());
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateExam(Long id, ExamRequest request, Long operatorId, boolean isAdmin) {
        Exam existing = (Exam)this.examMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        this.assertExamOwnerOrAdmin(existing, operatorId, isAdmin);
        if (request.getExamName() != null) {
            existing.setExamName(request.getExamName());
        }
        if (request.getExamType() != null) {
            existing.setExamType(request.getExamType());
        }
        if (request.getLevel() != null) {
            existing.setLevel(request.getLevel());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getTotalScore() != null) {
            existing.setTotalScore(request.getTotalScore());
        }
        if (request.getPassScore() != null) {
            existing.setPassScore(request.getPassScore());
        }
        if (request.getDuration() != null) {
            existing.setDuration(request.getDuration());
        }
        if (request.getStartTime() != null) {
            existing.setStartTime(request.getStartTime());
        }
        if (request.getMaxAttempts() != null) {
            existing.setMaxAttempts(request.getMaxAttempts());
        }
        if (request.getEnableAntiCheat() != null) {
            existing.setEnableAntiCheat(request.getEnableAntiCheat());
        }
        if (request.getRandomQuestion() != null) {
            existing.setRandomQuestion(request.getRandomQuestion());
        }
        if (request.getRandomOption() != null) {
            existing.setRandomOption(request.getRandomOption());
        }
        if (request.getShowResult() != null) {
            existing.setShowResult(request.getShowResult());
        }
        if (request.getStartTime() != null || request.getDuration() != null) {
            existing.setEndTime(existing.getStartTime().plusMinutes(existing.getDuration().intValue()));
        }
        this.examMapper.updateById(existing);
        log.info("\u66f4\u65b0\u8003\u8bd5: {}",id);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteExam(Long id, Long operatorId, boolean isAdmin) {
        Exam exam = (Exam)this.examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        this.assertExamOwnerOrAdmin(exam, operatorId, isAdmin);
        long inProgressCount = this.examRecordMapper.selectCount(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getExamId,id).eq(ExamRecord::getStatus, "IN_PROGRESS"));
        if (inProgressCount > 0L) {
            throw new BusinessException("\u8be5\u8003\u8bd5\u6709" + inProgressCount + "\u540d\u5b66\u751f\u6b63\u5728\u8003\u8bd5\u4e2d\uff0c\u65e0\u6cd5\u5220\u9664");
        }
        this.examMapper.deleteById(id);
        log.info("\u5220\u9664\u8003\u8bd5: {}",id);
    }

    @Transactional(rollbackFor={Exception.class})
    public void publishExam(Long id, Long operatorId, boolean isAdmin) {
        Exam exam = (Exam)this.examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        this.assertExamOwnerOrAdmin(exam, operatorId, isAdmin);
        long questionCount = this.examQuestionMapper.selectCount(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,id));
        if (questionCount == 0L) {
            throw new BusinessException("\u8003\u8bd5\u6ca1\u6709\u9898\u76ee\uff0c\u65e0\u6cd5\u53d1\u5e03");
        }
        if (exam.getStartTime() == null) {
            throw new BusinessException("\u8003\u8bd5\u672a\u8bbe\u7f6e\u5f00\u59cb\u65f6\u95f4\uff0c\u65e0\u6cd5\u53d1\u5e03");
        }
        exam.setStatus(1);
        this.examMapper.updateById(exam);
        log.info("\u53d1\u5e03\u8003\u8bd5: {}",exam.getExamName());
    }

    public Map<String, Object> getExamStats(Long examId, Long operatorId, boolean isAdmin) {
        Exam exam = (Exam)this.examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        this.assertExamOwnerOrAdmin(exam, operatorId, isAdmin);
        long totalAttempts = this.examRecordMapper.selectCount(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getExamId,examId));
        long completedAttempts = this.examRecordMapper.selectCount(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getExamId,examId).eq(ExamRecord::getStatus, "COMPLETED"));
        HashMap<String, Object> stats = new HashMap<String, Object>();
        stats.put("totalAttempts", totalAttempts);
        stats.put("completedAttempts", completedAttempts);
        BigDecimal avgScore = this.examRecordMapper.selectAvgScore(examId);
        stats.put("avgScore", avgScore != null ? avgScore : BigDecimal.ZERO);
        if (completedAttempts > 0L) {
            Long passCount = this.examRecordMapper.selectPassCount(examId, exam.getPassScore());
            double passRate = (double)passCount.longValue() / (double)completedAttempts * 100.0;
            stats.put("passRate", (double)Math.round(passRate * 10.0) / 10.0);
        } else {
            stats.put("passRate", 0);
        }
        List distribution = this.examRecordMapper.selectScoreDistribution(examId);
        stats.put("scoreDistribution", distribution);
        return stats;
    }

    @Transactional(rollbackFor={Exception.class})
    public void saveAnswer(Long recordId, Long questionId, String answer) {
        AnswerRecord existingRecord = (AnswerRecord)this.answerRecordMapper.selectOne(new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId,recordId).eq(AnswerRecord::getQuestionId,questionId));
        if (existingRecord != null) {
            existingRecord.setStudentAnswer(answer);
            this.answerRecordMapper.updateById(existingRecord);
        } else {
            AnswerRecord record = new AnswerRecord();
            record.setExamRecordId(recordId);
            record.setQuestionId(questionId);
            record.setStudentAnswer(answer);
            record.setIsCorrect(0);
            record.setObtainedScore(BigDecimal.ZERO);
            this.answerRecordMapper.insert(record);
        }
    }

    public List<Map<String, Object>> getExamQuestionList(Long examId) {
        return this.getExamQuestions(examId);
    }

    public List<Map<String, Object>> getOngoingExams(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getStatus,1).le(Exam::getStartTime,now).ge(Exam::getEndTime,now).orderByAsc(Exam::getEndTime);
        List<Exam> exams = this.examMapper.selectList(wrapper);
        return exams.stream().map(exam -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", exam.getId());
            item.put("title", exam.getExamName());
            item.put("examName", exam.getExamName());
            item.put("startTime", exam.getStartTime());
            item.put("endTime", exam.getEndTime());
            item.put("duration", exam.getDuration());
            long participantCount = this.examRecordMapper.selectCount(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getExamId,exam.getId()));
            item.put("participantCount", participantCount);
            long submittedCount = this.examRecordMapper.selectCount(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getExamId,exam.getId()).eq(ExamRecord::getStatus, "COMPLETED"));
            item.put("submittedCount", submittedCount);
            long remainingMinutes = ChronoUnit.MINUTES.between(now, exam.getEndTime());
            item.put("remainingMinutes", remainingMinutes);
            return item;
        }).collect(Collectors.toList());
    }

    public Page<Exam> getTeacherExams(Long teacherId, int pageNum, int pageSize) {
        Page<Exam> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getCreatorId,teacherId).orderByDesc(Exam::getCreateTime);
        return (Page<Exam>)this.examMapper.selectPage(page, wrapper);
    }

    public Map<String, Object> getExamReview(Long recordId, Long userId) {
        ExamRecord record = (ExamRecord)this.examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("\u8003\u8bd5\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        if (!record.getStudentId().equals(userId)) {
            throw new BusinessException("\u65e0\u6743\u67e5\u770b\u6b64\u8003\u8bd5\u56de\u987e");
        }
        Exam exam = (Exam)this.examMapper.selectById(record.getExamId());
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("examId", exam.getId());
        result.put("title", exam.getExamName());
        result.put("score", record.getObtainedScore());
        result.put("totalScore", exam.getTotalScore());
        result.put("passed", record.getObtainedScore() != null && record.getObtainedScore().compareTo(exam.getPassScore()) >= 0);
        List<AnswerRecord> answerRecords2 = this.answerRecordMapper.selectList(new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId,recordId));
        Map<Long, AnswerRecord> answerMap = answerRecords2.stream().collect(Collectors.toMap(AnswerRecord::getQuestionId, a -> a, (a, b) -> a));
        List<ExamQuestion> examQuestions2 = this.examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,exam.getId()).orderByAsc(ExamQuestion::getQuestionOrder));
        List<Long> reviewQuestionIds = examQuestions2.stream().map(ExamQuestion::getQuestionId).collect(Collectors.toList());
        Map<Long, Question> reviewQuestionMap = new HashMap<>();
        if (!reviewQuestionIds.isEmpty()) {
            List<Question> questionList = this.questionMapper.selectList(new LambdaQueryWrapper<Question>().in(Question::getId, reviewQuestionIds));
            reviewQuestionMap = questionList.stream().collect(Collectors.toMap(Question::getId, q -> q));
        }
        Map<Long, Question> finalReviewQuestionMap = reviewQuestionMap;
        List<Map<String, Object>> questions = examQuestions2.stream().map(eq -> {
            Question question = (Question)finalReviewQuestionMap.get(eq.getQuestionId());
            HashMap<String, Object> qMap = new HashMap<String, Object>();
            if (question != null) {
                qMap.put("id", question.getId());
                qMap.put("type", question.getQuestionType());
                qMap.put("content", question.getContent());
                qMap.put("options", question.getOptions());
                qMap.put("correctAnswer", question.getAnswer());
                qMap.put("analysis", question.getAnalysis());
            }
            qMap.put("score", eq.getQuestionScore());
            AnswerRecord ar = (AnswerRecord)answerMap.get(eq.getQuestionId());
            if (ar != null) {
                qMap.put("userAnswer", ar.getStudentAnswer());
                qMap.put("isCorrect", ar.getIsCorrect() != null && ar.getIsCorrect() == 1);
            } else {
                qMap.put("userAnswer", null);
                qMap.put("isCorrect", false);
            }
            return qMap;
        }).collect(Collectors.toList());
        result.put("questions", questions);
        return result;
    }

    public ExamRecord getExamRecordById(Long recordId) {
        ExamRecord record = (ExamRecord)this.examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("\u8003\u8bd5\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        return record;
    }

    public void addExamQuestion(Long examId, Long questionId, Integer order, BigDecimal score, Long operatorId, boolean isAdmin) {
        Exam exam = (Exam)this.examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        this.assertExamOwnerOrAdmin(exam, operatorId, isAdmin);
        Question question = (Question)this.questionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException("\u9898\u76ee\u4e0d\u5b58\u5728");
        }
        Long exists = this.examQuestionMapper.selectCount(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,examId).eq(ExamQuestion::getQuestionId,questionId));
        if (exists > 0L) {
            throw new BusinessException("\u8be5\u9898\u76ee\u5df2\u5728\u8003\u8bd5\u4e2d");
        }
        ExamQuestion eq = new ExamQuestion();
        eq.setExamId(examId);
        eq.setQuestionId(questionId);
        eq.setQuestionOrder(order);
        eq.setQuestionScore(score);
        this.examQuestionMapper.insert(eq);
    }

    @Transactional(rollbackFor={Exception.class})
    public void batchAddExamQuestions(Long examId, List<Map<String, Object>> questions, Long operatorId, boolean isAdmin) {
        Exam exam = (Exam)this.examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        this.assertExamOwnerOrAdmin(exam, operatorId, isAdmin);
        List<ExamQuestion> existing = this.examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,examId).orderByDesc(ExamQuestion::getQuestionOrder));
        int maxOrder = existing.isEmpty() ? 0 : (((ExamQuestion)existing.get(0)).getQuestionOrder() != null ? ((ExamQuestion)existing.get(0)).getQuestionOrder() : 0);
        for (Map<String, Object> q : questions) {
            Long questionId = Long.valueOf(q.get("questionId").toString());
            BigDecimal score = q.get("questionScore") != null ? new BigDecimal(q.get("questionScore").toString()) : new BigDecimal("5");
            Integer order = q.get("questionOrder") != null ? Integer.valueOf(q.get("questionOrder").toString()) : ++maxOrder;
            Long exists = this.examQuestionMapper.selectCount(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,examId).eq(ExamQuestion::getQuestionId,questionId));
            if (exists > 0L) continue;
            ExamQuestion eq = new ExamQuestion();
            eq.setExamId(examId);
            eq.setQuestionId(questionId);
            eq.setQuestionOrder(order);
            eq.setQuestionScore(score);
            this.examQuestionMapper.insert(eq);
        }
    }

    public void removeExamQuestion(Long examId, Long questionId, Long operatorId, boolean isAdmin) {
        Exam exam = (Exam)this.examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        this.assertExamOwnerOrAdmin(exam, operatorId, isAdmin);
        this.examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,examId).eq(ExamQuestion::getQuestionId,questionId));
    }

    public List<Map<String, Object>> getExamStudentRecords(Long examId, Long operatorId, boolean isAdmin) {
        Exam exam = (Exam)this.examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        this.assertExamOwnerOrAdmin(exam, operatorId, isAdmin);
        List<ExamRecord> records = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getExamId,examId).eq(ExamRecord::getStatus, "COMPLETED").orderByDesc(ExamRecord::getObtainedScore));
        if (records.isEmpty()) {
            return List.of();
        }
        List<Long> studentIds = records.stream().map(ExamRecord::getStudentId).distinct().collect(Collectors.toList());
        List<User> users = this.userMapper.selectByIds(studentIds);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));
        int rank = 0;
        for (ExamRecord record2 : (List<ExamRecord>)records) {
            record2.setRankPosition(Integer.valueOf(++rank));
        }
        return records.stream().map(record -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("recordId", record.getId());
            item.put("studentId", record.getStudentId());
            User user = (User)userMap.get(record.getStudentId());
            item.put("realName", user != null ? user.getRealName() : "\u672a\u77e5");
            item.put("username", user != null ? user.getUsername() : "");
            item.put("obtainedScore", record.getObtainedScore());
            item.put("totalScore", record.getTotalScore());
            item.put("correctCount", record.getCorrectCount());
            item.put("wrongCount", record.getWrongCount());
            item.put("duration", record.getDuration());
            item.put("submitTime", record.getSubmitTime());
            item.put("rank", record.getRankPosition());
            return item;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getQuestionAnalysis(Long examId, Long operatorId, boolean isAdmin) {
        Exam exam = (Exam)this.examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        this.assertExamOwnerOrAdmin(exam, operatorId, isAdmin);
        List<ExamQuestion> examQuestions3 = this.examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,examId).orderByAsc(ExamQuestion::getQuestionOrder));
        if (examQuestions3.isEmpty()) {
            return List.of();
        }
        List<Long> questionIds2 = examQuestions3.stream().map(ExamQuestion::getQuestionId).collect(Collectors.toList());
        List<Question> questions2 = this.questionMapper.selectList(new LambdaQueryWrapper<Question>().in(Question::getId, questionIds2));
        Map<Long, Question> questionMap2 = questions2.stream().collect(Collectors.toMap(Question::getId, q -> q, (a, b) -> a));
        List<Map<String, Object>> accuracyList = this.answerRecordMapper.selectQuestionAccuracy(examId);
        HashMap<Long, Map<String, Object>> accuracyMap = new HashMap<>();
        for (Map<String, Object> acc : accuracyList) {
            Long qId = Long.valueOf(acc.get("questionId").toString());
            accuracyMap.put(qId, acc);
        }
        return examQuestions3.stream().map(eq -> {
            Question question = (Question)questionMap2.get(eq.getQuestionId());
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("questionId", eq.getQuestionId());
            item.put("questionOrder", eq.getQuestionOrder());
            item.put("questionType", question != null ? question.getQuestionType() : "");
            item.put("title", question != null ? question.getTitle() : "");
            item.put("questionScore", eq.getQuestionScore());
            Map<String, Object> acc = accuracyMap.get(eq.getQuestionId());
            if (acc != null) {
                long total = Long.parseLong(acc.get("totalAnswers").toString());
                long correct = Long.parseLong(acc.get("correctAnswers").toString());
                item.put("totalAnswers", total);
                item.put("correctAnswers", correct);
                item.put("accuracy", total > 0L ? (double)Math.round((double)correct / (double)total * 1000.0) / 10.0 : 0.0);
            } else {
                item.put("totalAnswers", 0);
                item.put("correctAnswers", 0);
                item.put("accuracy", 0);
            }
            return item;
        }).collect(Collectors.toList());
    }

    private void assertExamOwnerOrAdmin(Exam exam, Long operatorId, boolean isAdmin) {
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        if (!(isAdmin || operatorId != null && operatorId.equals(exam.getCreatorId()))) {
            throw new BusinessException("\u65e0\u6743\u64cd\u4f5c\u6b64\u8003\u8bd5");
        }
    }

    public Map<String, Object> getExamMonitor(Long examId, Long operatorId, boolean isAdmin) {
        Exam exam = (Exam)this.examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("\u8003\u8bd5\u4e0d\u5b58\u5728");
        }
        this.assertExamOwnerOrAdmin(exam, operatorId, isAdmin);
        HashMap<String, Object> result = new HashMap<String, Object>();
        HashMap<String, Object> examInfo = new HashMap<String, Object>();
        examInfo.put("id", exam.getId());
        examInfo.put("examName", exam.getExamName());
        examInfo.put("startTime", exam.getStartTime());
        examInfo.put("endTime", exam.getEndTime());
        examInfo.put("duration", exam.getDuration());
        result.put("exam", examInfo);
        long totalQuestions = this.examQuestionMapper.selectCount(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId,examId));
        result.put("totalQuestions", totalQuestions);
        List<ExamRecord> records2 = this.examRecordMapper.selectList(new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getExamId,examId).orderByDesc(ExamRecord::getStartTime));
        long inProgressCount2 = records2.stream().filter(r -> "IN_PROGRESS".equals(r.getStatus())).count();
        long completedCount2 = records2.stream().filter(r -> "COMPLETED".equals(r.getStatus())).count();
        HashMap<String, Number> summary = new HashMap<String, Number>();
        summary.put("total", records2.size());
        summary.put("inProgress", inProgressCount2);
        summary.put("completed", completedCount2);
        result.put("summary", summary);
        if (records2.isEmpty()) {
            result.put("students", List.of());
            return result;
        }
        List<Long> studentIds2 = records2.stream().map(ExamRecord::getStudentId).distinct().collect(Collectors.toList());
        List<User> users2 = this.userMapper.selectByIds(studentIds2);
        Map<Long, User> userMap2 = users2.stream().collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));
        List<Long> recordIds = records2.stream().map(ExamRecord::getId).collect(Collectors.toList());
        List<AnswerRecord> allAnswers = this.answerRecordMapper.selectList(new LambdaQueryWrapper<AnswerRecord>().in(AnswerRecord::getExamRecordId, recordIds));
        Map<Long, Long> answeredCountMap = allAnswers.stream().collect(Collectors.groupingBy(AnswerRecord::getExamRecordId, Collectors.counting()));
        List<Map<String, Object>> students = records2.stream().map(record -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            User user = (User)userMap2.get(record.getStudentId());
            item.put("studentId", record.getStudentId());
            item.put("realName", user != null ? user.getRealName() : "\u672a\u77e5");
            item.put("username", user != null ? user.getUsername() : "");
            item.put("status", record.getStatus());
            item.put("answeredCount", answeredCountMap.getOrDefault(record.getId(), 0L));
            item.put("totalQuestions", totalQuestions);
            item.put("cheatWarningCount", record.getCheatWarningCount() != null ? record.getCheatWarningCount() : 0);
            item.put("startTime", record.getStartTime());
            item.put("submitTime", record.getSubmitTime());
            item.put("obtainedScore", record.getObtainedScore());
            return item;
        }).collect(Collectors.toList());
        result.put("students", students);
        return result;
    }

    public ExamService(ExamMapper examMapper, ExamRecordMapper examRecordMapper, ExamQuestionMapper examQuestionMapper, QuestionMapper questionMapper, AnswerRecordMapper answerRecordMapper, WrongQuestionService wrongQuestionService, PassageMapper passageMapper, UserMapper userMapper) {
        this.examMapper = examMapper;
        this.examRecordMapper = examRecordMapper;
        this.examQuestionMapper = examQuestionMapper;
        this.questionMapper = questionMapper;
        this.answerRecordMapper = answerRecordMapper;
        this.wrongQuestionService = wrongQuestionService;
        this.passageMapper = passageMapper;
        this.userMapper = userMapper;
    }
}

