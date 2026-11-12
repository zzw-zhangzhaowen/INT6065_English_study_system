package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.AiChatRecord;
import com.english.learning.entity.PronunciationEvaluation;
import com.english.learning.entity.WritingCorrection;
import com.english.learning.mapper.AiChatRecordMapper;
import com.english.learning.mapper.PronunciationEvaluationMapper;
import com.english.learning.mapper.WritingCorrectionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AIService {
    private static final Logger log = LoggerFactory.getLogger(AIService.class);
    private final AiChatRecordMapper aiChatRecordMapper;
    private final WritingCorrectionMapper writingCorrectionMapper;
    private final PronunciationEvaluationMapper pronunciationEvaluationMapper;
    private final ObjectMapper objectMapper;

    @Transactional(rollbackFor={Exception.class})
    public Map<String, Object> chat(String question, Long userId, String sessionId) {
        log.info("AI Chat - User: {}, Question: {}",userId,question);
        if (question == null || question.trim().isEmpty()) {
            throw new BusinessException("\u8bf7\u8f93\u5165\u95ee\u9898\u5185\u5bb9");
        }
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }
        AiChatRecord userRecord = new AiChatRecord();
        userRecord.setUserId(userId);
        userRecord.setSessionId(sessionId);
        userRecord.setMessageType("USER");
        userRecord.setMessageContent(question);
        this.aiChatRecordMapper.insert(userRecord);
        String answer = this.processWithLocalAI(question);
        AiChatRecord aiRecord = new AiChatRecord();
        aiRecord.setUserId(userId);
        aiRecord.setSessionId(sessionId);
        aiRecord.setMessageType("AI");
        aiRecord.setMessageContent(answer);
        this.aiChatRecordMapper.insert(aiRecord);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("sessionId", sessionId);
        result.put("question", question);
        result.put("answer", answer);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    public List<AiChatRecord> getChatHistory(Long userId, String sessionId) {
        return this.aiChatRecordMapper.selectList(new LambdaQueryWrapper<AiChatRecord>()
            .eq(AiChatRecord::getUserId, userId)
            .eq(sessionId != null, AiChatRecord::getSessionId, sessionId)
            .orderByAsc(AiChatRecord::getCreateTime));
    }

    public List<Map<String, Object>> getSessionList(Long userId) {
        List<AiChatRecord> records = this.aiChatRecordMapper.selectList(new LambdaQueryWrapper<AiChatRecord>()
            .eq(AiChatRecord::getUserId, userId)
            .eq(AiChatRecord::getMessageType, "USER")
            .orderByDesc(AiChatRecord::getCreateTime));
        LinkedHashMap<String, Map<String, Object>> sessionMap = new LinkedHashMap<>();
        for (AiChatRecord record : records) {
            if (sessionMap.containsKey(record.getSessionId())) continue;
            HashMap<String, Object> session = new HashMap<String, Object>();
            session.put("sessionId", record.getSessionId());
            session.put("firstMessage", record.getMessageContent());
            session.put("createTime", record.getCreateTime());
            sessionMap.put(record.getSessionId(), session);
        }
        return new ArrayList<Map<String, Object>>(sessionMap.values());
    }

    @Transactional(rollbackFor={Exception.class})
    public Map<String, Object> correctWriting(String content, String title, String level, Long studentId) {
        String errorsJson;
        log.info("AI Writing Correction - Level: {}, StudentId: {}",level,studentId);
        Map<String, Object> analysis = this.analyzeWriting(content, level);
        WritingCorrection correction = new WritingCorrection();
        correction.setStudentId(studentId);
        correction.setTitle(title);
        correction.setContent(content);
        correction.setWordCount(Integer.valueOf(content.split("\\s+").length));
        correction.setTotalScore((BigDecimal)analysis.get("totalScore"));
        correction.setContentScore((BigDecimal)analysis.get("contentScore"));
        correction.setStructureScore((BigDecimal)analysis.get("structureScore"));
        correction.setLanguageScore((BigDecimal)analysis.get("languageScore"));
        correction.setVocabularyScore((BigDecimal)analysis.get("vocabularyScore"));
        try {
            errorsJson = this.objectMapper.writeValueAsString(analysis.get("errors"));
            log.debug("Serialized errors JSON: {}",errorsJson);
        }
        catch (Exception e) {
            log.error("Failed to serialize errors, using empty array: ", (Throwable)e);
            errorsJson = "[]";
        }
        correction.setErrors(errorsJson);
        correction.setSuggestions((String)analysis.get("suggestions"));
        correction.setCorrectedContent((String)analysis.get("correctedContent"));
        this.writingCorrectionMapper.insert(correction);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("correctionId", correction.getId());
        result.putAll(analysis);
        return result;
    }

    public List<WritingCorrection> getWritingHistory(Long studentId) {
        return this.writingCorrectionMapper.selectList(new LambdaQueryWrapper<WritingCorrection>()
            .eq(WritingCorrection::getStudentId, studentId)
            .orderByDesc(WritingCorrection::getCreateTime));
    }

    @Transactional(rollbackFor={Exception.class})
    public Map<String, Object> evaluatePronunciation(String audioUrl, String referenceText, Long studentId) {
        log.info("AI Pronunciation Evaluation - Audio: {}, StudentId: {}",audioUrl,studentId);
        Map<String, Object> evaluation = this.analyzePronunciation(audioUrl, referenceText);
        PronunciationEvaluation record = new PronunciationEvaluation();
        record.setStudentId(studentId);
        record.setTextContent(referenceText);
        record.setAudioUrl(audioUrl);
        record.setTotalScore((BigDecimal)evaluation.get("totalScore"));
        record.setAccuracyScore((BigDecimal)evaluation.get("accuracyScore"));
        record.setFluencyScore((BigDecimal)evaluation.get("fluencyScore"));
        record.setIntonationScore((BigDecimal)evaluation.get("intonationScore"));
        record.setSpeedScore((BigDecimal)evaluation.get("speedScore"));
        record.setRecognizedText((String)evaluation.get("recognizedText"));
        try {
            record.setErrorDetails(this.objectMapper.writeValueAsString(evaluation.get("errorDetails")));
        }
        catch (Exception e) {
            record.setErrorDetails("[]");
        }
        record.setSuggestions((String)evaluation.get("suggestions"));
        this.pronunciationEvaluationMapper.insert(record);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("evaluationId", record.getId());
        result.putAll(evaluation);
        return result;
    }

    public List<PronunciationEvaluation> getPronunciationHistory(Long studentId) {
        return this.pronunciationEvaluationMapper.selectList(new LambdaQueryWrapper<PronunciationEvaluation>()
            .eq(PronunciationEvaluation::getStudentId, studentId)
            .orderByDesc(PronunciationEvaluation::getCreateTime));
    }

    private String processWithLocalAI(String input) {
        String lowerInput = input.toLowerCase();
        if (lowerInput.contains("hello") || lowerInput.contains("hi") || input.contains("\u4f60\u597d") || input.contains("\u5927\u5bb6\u597d")) {
            return "Hello! \u5f88\u9ad8\u5174\u80fd\u5e2e\u52a9\u4f60\u5b66\u4e60\u82f1\u8bed\ud83d\udc4b \n\n\u4f5c\u4e3a\u60a8\u7684AI\u82f1\u8bed\u5b66\u4e60\u52a9\u624b\uff0c\u6211\u53ef\u4ee5\u5e2e\u52a9\u60a8\uff1a\n\u2022 \u89e3\u7b54\u8bed\u6cd5\u95ee\u9898\n\u2022 \u8bb2\u89e3\u8bcd\u6c47\u7528\u6cd5\n\u2022 \u5206\u4eab\u5b66\u4e60\u65b9\u6cd5\n\u2022 \u63d0\u4f9b\u8003\u8bd5\u6280\u5de7\n\n\u8bf7\u544a\u8bc9\u6211\u4f60\u60f3\u4e86\u89e3\u4ec0\u4e48\uff1f";
        }
        if (lowerInput.contains("grammar") || input.contains("\u8bed\u6cd5")) {
            if (input.contains("\u865a\u62df\u8bed\u6c14") || lowerInput.contains("subjunctive")) {
                return "\u865a\u62df\u8bed\u6c14(Subjunctive Mood)\u662f\u8868\u793a\u5047\u8bbe\u3001\u613f\u671b\u3001\u5efa\u8bae\u7b49\u975e\u771f\u5b9e\u60c5\u51b5\u7684\u8bed\u6cd5\u5f62\u5f0f\uff1a\n\n**1. \u4e0e\u73b0\u5728\u4e8b\u5b9e\u76f8\u53cd**\n   \u516c\u5f0f\uff1aIf + \u4e3b\u8bed + \u8fc7\u53bb\u5f0f, \u4e3b\u8bed + would/could/might + \u52a8\u8bcd\u539f\u5f62\n   \u4f8b\uff1aIf I were you, I would study harder.\n\n**2. \u4e0e\u8fc7\u53bb\u4e8b\u5b9e\u76f8\u53cd**\n   \u516c\u5f0f\uff1aIf + \u4e3b\u8bed + had + \u8fc7\u53bb\u5206\u8bcd, \u4e3b\u8bed + would/could/might + have + \u8fc7\u53bb\u5206\u8bcd\n   \u4f8b\uff1aIf I had studied harder, I would have passed the exam.\n\n**3. \u4e0e\u5c06\u6765\u4e8b\u5b9e\u76f8\u53cd**\n   \u516c\u5f0f\uff1aIf + \u4e3b\u8bed + should/were to + \u52a8\u8bcd\u539f\u5f62, \u4e3b\u8bed + would + \u52a8\u8bcd\u539f\u5f62\n   \u4f8b\uff1aIf it should rain tomorrow, we would cancel the trip.\n\n\u8fd8\u6709\u5176\u4ed6\u8bed\u6cd5\u95ee\u9898\u5417\uff1f";
            }
            return "Grammar is an important part of English learning.\n\n\u6211\u53ef\u4ee5\u5e2e\u4f60\u8bb2\u89e3\u7684\u8bed\u6cd5\u8bdd\u9898\u5305\u62ec\uff1a\n\u2022 \u865a\u62df\u8bed\u6c14(Subjunctive Mood)\n\u2022 \u5b9a\u8bed\u4ece\u53e5(Relative Clauses)\n\u2022 \u65f6\u6001(Tenses)\n\u2022 \u88ab\u52a8\u8bed\u6001(Passive Voice)\n\u2022 \u975e\u8c13\u8bed\u52a8\u8bcd(Non-finite Verbs)\n\nWhich specific grammar topic would you like to discuss?";
        }
        if (lowerInput.contains("vocabulary") || lowerInput.contains("word") || input.contains("\u8bcd\u6c47") || input.contains("\u5355\u8bcd")) {
            return "Building vocabulary is essential for English learning! \ufffd\udcda\n\n**\u6709\u6548\u7684\u8bcd\u6c47\u5b66\u4e60\u65b9\u6cd5\uff1a**\n1. **\u8bed\u5883\u8bb0\u5fc6\u6cd5**\uff1a\u5728\u53e5\u5b50\u548c\u6587\u7ae0\u4e2d\u5b66\u4e60\u5355\u8bcd\n2. **\u8bcd\u6839\u8bcd\u7f00\u6cd5**\uff1a\u638c\u63e1\u5e38\u7528\u8bcd\u6839\uff0c\u4e3e\u4e00\u53cd\u4e09\n3. **\u95ea\u5361\u590d\u4e60**\uff1a\u4f7f\u7528Anki\u7b49\u5de5\u5177\u8fdb\u884c\u95f4\u9694\u590d\u4e60\n4. **\u4e3b\u9898\u5206\u7c7b**\uff1a\u6309\u8bdd\u9898(\u65c5\u6e38\u3001\u5de5\u4f5c\u7b49)\u5206\u7c7b\u8bb0\u5fc6\n5. **\u591a\u611f\u5b98\u53c2\u4e0e**\uff1a\u542c\u3001\u8bf4\u3001\u8bfb\u3001\u5199\u7ed3\u5408\n\n\u5efa\u8bae\u6bcf\u5929\u5b66\u4e6020-30\u4e2a\u65b0\u8bcd\uff0c\u5e76\u575a\u6301\u590d\u4e60\u65e7\u8bcd\u3002";
        }
        if (lowerInput.contains("listening") || input.contains("\u542c\u529b")) {
            return "To improve listening skills, here are some effective strategies: \ufffd\udfa7\n\n**1. \u7cbe\u542c\u4e0e\u6cdb\u542c\u7ed3\u5408**\n   \u2022 \u7cbe\u542c\uff1a\u9009\u62e9\u9002\u5408\u81ea\u5df1\u6c34\u5e73\u7684\u6750\u6599\uff0c\u53cd\u590d\u542c\u76f4\u5230\u5b8c\u5168\u542c\u61c2\n   \u2022 \u6cdb\u542c\uff1a\u5927\u91cf\u63a5\u89e6\u82f1\u8bed\u97f3\u9891\uff0c\u57f9\u517b\u8bed\u611f\n\n**2. \u9009\u62e9\u5408\u9002\u7684\u6750\u6599**\n   \u2022 \u521d\u7ea7\uff1aVOA\u6162\u901f\u82f1\u8bed\u3001BBC Learning English\n   \u2022 \u4e2d\u7ea7\uff1aTED\u6f14\u8bb2\u3001\u82f1\u8bed\u65b0\u95fb\n   \u2022 \u9ad8\u7ea7\uff1a\u82f1\u7f8e\u5267\u3001\u539f\u7248\u7535\u5f71\n\n**3. \u6ce8\u610f\u542c\u529b\u6280\u5de7**\n   \u2022 \u6293\u5173\u952e\u8bcd\u548c\u4e3b\u65e8\n   \u2022 \u6ce8\u610f\u8fde\u8bfb\u3001\u5f31\u8bfb\u73b0\u8c61\n   \u2022 \u8fb9\u542c\u8fb9\u505a\u7b14\u8bb0\n\n\ufffd\udccc \u5efa\u8bae\u6bcf\u5929\u81f3\u5c11\u7ec3\u4e6030\u5206\u949f\u542c\u529b\uff01";
        }
        if (lowerInput.contains("speaking") || input.contains("\u53e3\u8bed") || input.contains("\u53d1\u97f3")) {
            return "Speaking practice is crucial for English fluency! \ufffd\udde3\ufe0f\n\n**\u63d0\u9ad8\u53e3\u8bed\u7684\u65b9\u6cd5\uff1a**\n1. **\u8ddf\u8bfb\u6a21\u4eff(Shadowing)**\uff1a\u8ddf\u7740\u539f\u58f0\u540c\u6b65\u6717\u8bfb\n2. **\u5f55\u97f3\u81ea\u6211\u68c0\u67e5**\uff1a\u5f55\u4e0b\u81ea\u5df1\u7684\u53d1\u97f3\u5e76\u5bf9\u6bd4\u539f\u58f0\n3. **\u8bed\u8a00\u4ea4\u6362**\uff1a\u627e\u8bed\u4f34\u8fdb\u884c\u4ea4\u6d41(Language Exchange)\n4. **\u6bcf\u65e5\u72ec\u767d**\uff1a\u6bcf\u5929\u7528\u82f1\u8bed\u81ea\u8a00\u81ea\u8bed5-10\u5206\u949f\n5. **\u4e3b\u9898\u6f14\u8bb2**\uff1a\u9009\u62e9\u4e00\u4e2a\u8bdd\u9898\u8fdb\u884c2-3\u5206\u949f\u5373\u5174\u6f14\u8bb2\n\n**\u53d1\u97f3\u6280\u5de7\uff1a**\n\u2022 \u638c\u63e1\u97f3\u6807(Phonetics)\n\u2022 \u6ce8\u610f\u91cd\u97f3\u548c\u8bed\u8c03(Stress & Intonation)\n\u2022 \u7ec3\u4e60\u8fde\u8bfb\u6280\u5de7(Linking)\n\nTry to practice speaking daily and record yourself to identify areas for improvement!";
        }
        if (lowerInput.contains("reading") || input.contains("\u9605\u8bfb")) {
            return "\u9605\u8bfb\u80fd\u529b\u7684\u63d0\u5347\u9700\u8981\u7cfb\u7edf\u7684\u8bad\u7ec3\uff01\ufffd\udcda\n\n**\u9605\u8bfb\u6280\u5de7\uff1a**\n1. **Skimming(\u7565\u8bfb)**\uff1a\u5feb\u901f\u638c\u63e1\u6587\u7ae0\u4e3b\u65e8\n2. **Scanning(\u626b\u8bfb)**\uff1a\u5bfb\u627e\u7279\u5b9a\u4fe1\u606f\n3. **Intensive Reading(\u7cbe\u8bfb)**\uff1a\u7ec6\u81f4\u9605\u8bfb\uff0c\u5b66\u4e60\u8bcd\u6c47\u548c\u8bed\u6cd5\n4. **Extensive Reading(\u6cdb\u8bfb)**\uff1a\u5927\u91cf\u9605\u8bfb\uff0c\u63d0\u9ad8\u9605\u8bfb\u901f\u5ea6\n\n**\u9605\u8bfb\u6750\u6599\u63a8\u8350\uff1a**\n\u2022 \u521d\u7ea7\uff1aGraded Readers\uff08\u5206\u7ea7\u8bfb\u7269\uff09\n\u2022 \u4e2d\u7ea7\uff1aThe Economist, National Geographic\n\u2022 \u9ad8\u7ea7\uff1a\u82f1\u6587\u539f\u7248\u5c0f\u8bf4\n\n\u5efa\u8bae\u6bcf\u5929\u9605\u8bfb\u81f3\u5c1130\u5206\u949f\uff0c\u901a\u8fc7\u9605\u8bfb\u6269\u5927\u8bcd\u6c47\u91cf\uff01";
        }
        if (lowerInput.contains("writing") || input.contains("\u5199\u4f5c") || input.contains("\u4f5c\u6587")) {
            return "Good writing skills require practice and patience! \u270d\ufe0f\n\n**\u5199\u4f5c\u63d0\u5347\u6b65\u9aa4\uff1a**\n1. **\u79ef\u7d2f\u8bcd\u6c47\u548c\u77ed\u8bed**\uff1a\u5b66\u4e60\u9ad8\u7ea7\u8bcd\u6c47\u548c\u5730\u9053\u8868\u8fbe\n2. **\u5b66\u4e60\u8303\u6587**\uff1a\u5206\u6790\u4f18\u79c0\u4f5c\u6587\u7684\u7ed3\u6784\u548c\u8868\u8fbe\n3. **\u638c\u63e1\u5199\u4f5c\u6a21\u677f**\uff1a\u4e86\u89e3\u4e0d\u540c\u7c7b\u578b\u4f5c\u6587\u7684\u7ed3\u6784\n4. **\u591a\u7ec3\u4e60**\uff1a\u5efa\u8bae\u6bcf\u5468\u5b8c\u62102-3\u7bc7\u4f5c\u6587\n5. **\u83b7\u53d6\u53cd\u9988**\uff1a\u627e\u8001\u5e08\u6216\u8bed\u4f34\u6279\u6539\n\n**\u5e38\u89c1\u4f5c\u6587\u7c7b\u578b\uff1a**\n\u2022 Argumentative(\u8bae\u8bba\u6587)\n\u2022 Descriptive(\u63cf\u5199\u6587)\n\u2022 Narrative(\u8bb0\u53d9\u6587)\n\u2022 Expository(\u8bf4\u660e\u6587)\n\n\ufffd\udca1 \u4f60\u53ef\u4ee5\u4f7f\u7528\u6211\u4eec\u7684AI\u5199\u4f5c\u6279\u6539\u529f\u80fd\u83b7\u53d6\u5b9e\u65f6\u53cd\u9988\uff01";
        }
        if (lowerInput.contains("exam") || lowerInput.contains("test") || lowerInput.contains("cet") || input.contains("\u8003\u8bd5") || input.contains("\u56db\u7ea7") || input.contains("\u516d\u7ea7")) {
            return "\u82f1\u8bed\u8003\u8bd5\u5907\u8003\u6280\u5de7\uff01\ufffd\udfaf\n\n**\u5907\u8003\u7b56\u7565\uff1a**\n1. **\u4e86\u89e3\u8003\u8bd5\u683c\u5f0f**\uff1a\u719f\u6089\u9898\u578b\u548c\u5206\u503c\u5206\u5e03\n2. **\u5236\u5b9a\u590d\u4e60\u8ba1\u5212**\uff1a\u6309\u6a21\u5757\u8fdb\u884c\u7cfb\u7edf\u590d\u4e60\n3. **\u505a\u771f\u9898**\uff1a\u8fd1\u5e745-10\u5e74\u771f\u9898\u81f3\u5c11\u505a2\u904d\n4. **\u6a21\u62df\u8003\u8bd5**\uff1a\u4e25\u683c\u6309\u8003\u8bd5\u65f6\u95f4\u8fdb\u884c\u6a21\u62df\n5. **\u5f31\u9879\u7a81\u7834**\uff1a\u9488\u5bf9\u8584\u5f31\u73af\u8282\u91cd\u70b9\u7ec3\u4e60\n\n**\u8003\u8bd5\u5f53\u5929\u6ce8\u610f\u4e8b\u9879\uff1a**\n\u2022 \u63d0\u524d15\u5206\u949f\u5230\u8fbe\u8003\u573a\n\u2022 \u5e26\u9f50\u51c6\u8003\u8bc1\u3001\u8eab\u4efd\u8bc1\u30012B\u94f3\u7b14\u3001\u6a61\u76ae\n\u2022 \u5148\u6613\u540e\u96be\uff0c\u5408\u7406\u5206\u914d\u65f6\u95f4\n\u2022 \u4fdd\u6301\u5e73\u5e38\u5fc3\uff0c\u4e0d\u8981\u7d27\u5f20\n\nGood luck with your exam! \ufffd\udf89";
        }
        if (lowerInput.contains("how to learn") || lowerInput.contains("study method") || input.contains("\u5b66\u4e60\u65b9\u6cd5") || input.contains("\u600e\u4e48\u5b66")) {
            return "\u9ad8\u6548\u7684\u82f1\u8bed\u5b66\u4e60\u65b9\u6cd5\uff01\ufffd\udf93\n\n**1. \u5236\u5b9a\u660e\u786e\u76ee\u6807**\n   \u2022 \u8bbe\u5b9a\u77ed\u671f\u548c\u957f\u671f\u5b66\u4e60\u76ee\u6807\n   \u2022 \u4f7f\u7528SMART\u539f\u5219(\u5177\u4f53\u3001\u53ef\u8861\u91cf\u3001\u53ef\u8fbe\u6210)\n\n**2. \u575a\u6301\u6bcf\u65e5\u5b66\u4e60**\n   \u2022 \u6bcf\u5929\u81f3\u5c11\u5b66\u4e601\u5c0f\u65f6\n   \u2022 \u5229\u7528\u788e\u7247\u65f6\u95f4\u8fdb\u884c\u590d\u4e60\n\n**3. \u591a\u7ef4\u5ea6\u5b66\u4e60**\n   \u2022 \u542c\u3001\u8bf4\u3001\u8bfb\u3001\u5199\u5168\u9762\u53d1\u5c55\n   \u2022 \u4e0d\u8981\u504f\u79d1\uff0c\u5747\u8861\u63d0\u5347\n\n**4. \u521b\u9020\u8bed\u8a00\u73af\u5883**\n   \u2022 \u770b\u82f1\u6587\u7535\u5f71/\u7f8e\u5267\n   \u2022 \u542c\u82f1\u8bed\u97f3\u4e50/\u64ad\u5ba2\n   \u2022 \u53c2\u52a0\u82f1\u8bed\u89d2\u6d3b\u52a8\n\n**5. \u53ca\u65f6\u590d\u4e60\u5de9\u56fa**\n   \u2022 \u4f7f\u7528\u8273\u5bbe\u6d69\u65af\u8bb0\u5fc6\u66f2\u7ebf\n   \u2022 \u5b9a\u671f\u590d\u4e60\u65e7\u77e5\u8bc6\n\n\u8bb0\u4f4f\uff1a**\u575a\u6301\u6bd4\u65b9\u6cd5\u66f4\u91cd\u8981\uff01**";
        }
        if (lowerInput.contains("thank") || input.contains("\u8c22\u8c22")) {
            return "\u4e0d\u5ba2\u6c14\uff01\u5f88\u9ad8\u5174\u80fd\u5e2e\u52a9\u5230\u4f60\uff01\ufffd\udd17\n\n\u5982\u679c\u8fd8\u6709\u5176\u4ed6\u82f1\u8bed\u5b66\u4e60\u76f8\u5173\u7684\u95ee\u9898\uff0c\u6b22\u8fce\u968f\u65f6\u63d0\u95ee\u3002\n\u795d\u4f60\u5b66\u4e60\u8fdb\u6b65\uff01\ufffd\udf93";
        }
        if (input.length() < 10) {
            return "Thank you for your question! \ud83d\udc4b\n\n\u60a8\u597d\uff01\u6211\u662f\u60a8\u7684AI\u82f1\u8bed\u5b66\u4e60\u52a9\u624b\u3002\u8bf7\u95ee\u60a8\u60f3\u54a8\u8be2\u54ea\u65b9\u9762\u7684\u5185\u5bb9\uff1f\n\n\u60a8\u53ef\u4ee5\u95ee\u6211\uff1a\n\u2022 \u8bed\u6cd5\u77e5\u8bc6\u70b9\uff08\u5982\u865a\u62df\u8bed\u6c14\u3001\u5b9a\u8bed\u4ece\u53e5\uff09\n\u2022 \u8bcd\u6c47\u5b66\u4e60\u65b9\u6cd5\n\u2022 \u542c\u529b/\u53e3\u8bed/\u9605\u8bfb/\u5199\u4f5c\u6280\u5de7\n\u2022 \u8003\u8bd5\u5907\u8003\u5efa\u8bae";
        }
        return "Thank you for your question: \"" + (String)(input.length() > 50 ? input.substring(0, 50) + "..." : input) + "\"\n\n\u4f5c\u4e3a\u60a8\u7684AI\u82f1\u8bed\u5b66\u4e60\u52a9\u624b\uff0c\u6211\u4f1a\u5c3d\u529b\u4e3a\u60a8\u63d0\u4f9b\u5e2e\u52a9\u3002\ufffd\udca1\n\n\u8bf7\u95ee\u60a8\u60f3\u4e86\u89e3\uff1a\n1. **\u8bed\u6cd5\u77e5\u8bc6**\uff1a\u5982\u865a\u62df\u8bed\u6c14\u3001\u5b9a\u8bed\u4ece\u53e5\u3001\u65f6\u6001\u7b49\n2. **\u8bcd\u6c47\u5b66\u4e60**\uff1a\u5982\u4f55\u9ad8\u6548\u8bb0\u5fc6\u5355\u8bcd\u3001\u8bcd\u6c47\u79ef\u7d2f\u65b9\u6cd5\n3. **\u542c\u529b\u8bad\u7ec3**\uff1a\u63d0\u9ad8\u542c\u529b\u7406\u89e3\u80fd\u529b\u7684\u6280\u5de7\n4. **\u53e3\u8bed\u7ec3\u4e60**\uff1a\u53d1\u97f3\u3001\u6d41\u5229\u5ea6\u63d0\u5347\u65b9\u6cd5\n5. **\u9605\u8bfb\u6280\u5de7**\uff1a\u5feb\u901f\u9605\u8bfb\u548c\u7406\u89e3\u6587\u7ae0\u7684\u65b9\u6cd5\n6. **\u5199\u4f5c\u63d0\u5347**\uff1a\u4f5c\u6587\u7ed3\u6784\u548c\u8868\u8fbe\u65b9\u5f0f\n7. **\u8003\u8bd5\u5907\u8003**\uff1a\u56db\u516d\u7ea7\u3001\u96c5\u601d\u7b49\u8003\u8bd5\u6280\u5de7\n\n\u8bf7\u8be6\u7ec6\u8bf4\u660e\u60a8\u7684\u95ee\u9898\uff0c\u6211\u4f1a\u4e3a\u60a8\u63d0\u4f9b\u66f4\u5177\u4f53\u7684\u5efa\u8bae\u548c\u8bb2\u89e3\u3002";
    }

    private Map<String, Object> analyzeWriting(String content, String level) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        int wordCount = content.split("\\s+").length;
        BigDecimal baseScore = BigDecimal.valueOf(70L);
        if (wordCount >= 150) {
            baseScore = baseScore.add(BigDecimal.valueOf(10L));
        }
        if (wordCount >= 250) {
            baseScore = baseScore.add(BigDecimal.valueOf(5L));
        }
        result.put("totalScore", baseScore);
        result.put("contentScore", baseScore.subtract(BigDecimal.valueOf(2L)));
        result.put("structureScore", baseScore.subtract(BigDecimal.valueOf(3L)));
        result.put("languageScore", baseScore.subtract(BigDecimal.valueOf(1L)));
        result.put("vocabularyScore", baseScore.add(BigDecimal.valueOf(2L)));
        ArrayList<Map<String, Object>> errors = new ArrayList<>();
        HashMap<String, Object> error1 = new HashMap<String, Object>();
        error1.put("type", "grammar");
        error1.put("position", 10);
        error1.put("original", "example");
        error1.put("suggestion", "Example correction");
        errors.add(error1);
        result.put("errors", errors);
        result.put("suggestions", "Consider using more varied sentence structures and advanced vocabulary.");
        result.put("correctedContent", content);
        return result;
    }

    private Map<String, Object> analyzePronunciation(String audioUrl, String referenceText) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("totalScore", BigDecimal.valueOf(85.5));
        result.put("accuracyScore", BigDecimal.valueOf(88.0));
        result.put("fluencyScore", BigDecimal.valueOf(82.0));
        result.put("intonationScore", BigDecimal.valueOf(85.0));
        result.put("speedScore", BigDecimal.valueOf(87.0));
        result.put("recognizedText", referenceText);
        ArrayList errorDetails = new ArrayList();
        result.put("errorDetails", errorDetails);
        result.put("suggestions", "Good pronunciation overall. Try to pay more attention to word stress and intonation patterns.");
        return result;
    }

    public Map<String, Object> checkGrammar(String text) {
        HashMap<String, Object> error;
        log.info("AI Grammar Check - Text length: {}",text.length());
        HashMap<String, Object> result = new HashMap<String, Object>();
        ArrayList errors = new ArrayList();
        String correctedText = text;
        String[][] grammarRules = new String[][]{{"\\bi\\b", "I", "CAPITALIZATION", "\u7b2c\u4e00\u4eba\u79f0\u4ee3\u8bcd'I'\u5e94\u8be5\u5927\u5199"}, {"\\bdont\\b", "don't", "APOSTROPHE", "\u7f29\u5199\u8bcd\u9700\u8981\u6487\u53f7"}, {"\\bdoesnt\\b", "doesn't", "APOSTROPHE", "\u7f29\u5199\u8bcd\u9700\u8981\u6487\u53f7"}, {"\\bwont\\b", "won't", "APOSTROPHE", "\u7f29\u5199\u8bcd\u9700\u8981\u6487\u53f7"}, {"\\bcant\\b", "can't", "APOSTROPHE", "\u7f29\u5199\u8bcd\u9700\u8981\u6487\u53f7"}, {"\\bim\\b", "I'm", "APOSTROPHE", "\u7f29\u5199\u8bcd\u9700\u8981\u6487\u53f7"}, {"\\bive\\b", "I've", "APOSTROPHE", "\u7f29\u5199\u8bcd\u9700\u8981\u6487\u53f7"}, {"\\btheir\\s+is\\b", "there is", "WORD_CONFUSION", "'their'\u548c'there'\u6df7\u6dc6"}, {"\\bthey're\\s+is\\b", "there is", "WORD_CONFUSION", "'they're'\u548c'there'\u6df7\u6dc6"}, {"\\byour\\s+welcome\\b", "you're welcome", "WORD_CONFUSION", "'your'\u548c'you're'\u6df7\u6dc6"}, {"\\bits\\s+a\\s+\\w+\\s+that\\b", "it's", "WORD_CONFUSION", "'its'\u548c'it's'\u6df7\u6dc6"}, {"\\ba\\s+apple\\b", "an apple", "ARTICLE", "\u5143\u97f3\u524d\u5e94\u4f7f\u7528'an'"}, {"\\ba\\s+hour\\b", "an hour", "ARTICLE", "\u53d1\u97f3\u4ee5\u5143\u97f3\u5f00\u5934\u7684\u8bcd\u524d\u5e94\u4f7f\u7528'an'"}, {"\\ba\\s+honest\\b", "an honest", "ARTICLE", "\u53d1\u97f3\u4ee5\u5143\u97f3\u5f00\u5934\u7684\u8bcd\u524d\u5e94\u4f7f\u7528'an'"}, {"\\ban\\s+university\\b", "a university", "ARTICLE", "\u53d1\u97f3\u4ee5\u8f85\u97f3\u5f00\u5934\u7684\u8bcd\u524d\u5e94\u4f7f\u7528'a'"}, {"\\ban\\s+European\\b", "a European", "ARTICLE", "\u53d1\u97f3\u4ee5\u8f85\u97f3\u5f00\u5934\u7684\u8bcd\u524d\u5e94\u4f7f\u7528'a'"}, {"\\bhe\\s+don't\\b", "he doesn't", "SUBJECT_VERB", "\u7b2c\u4e09\u4eba\u79f0\u5355\u6570\u5e94\u4f7f\u7528'doesn't'"}, {"\\bshe\\s+don't\\b", "she doesn't", "SUBJECT_VERB", "\u7b2c\u4e09\u4eba\u79f0\u5355\u6570\u5e94\u4f7f\u7528'doesn't'"}, {"\\bit\\s+don't\\b", "it doesn't", "SUBJECT_VERB", "\u7b2c\u4e09\u4eba\u79f0\u5355\u6570\u5e94\u4f7f\u7528'doesn't'"}, {"\\bhe\\s+have\\b", "he has", "SUBJECT_VERB", "\u7b2c\u4e09\u4eba\u79f0\u5355\u6570\u5e94\u4f7f\u7528'has'"}, {"\\bshe\\s+have\\b", "she has", "SUBJECT_VERB", "\u7b2c\u4e09\u4eba\u79f0\u5355\u6570\u5e94\u4f7f\u7528'has'"}, {"\\bit\\s+have\\b", "it has", "SUBJECT_VERB", "\u7b2c\u4e09\u4eba\u79f0\u5355\u6570\u5e94\u4f7f\u7528'has'"}, {"\\bmore\\s+better\\b", "better", "DOUBLE_COMPARATIVE", "\u6bd4\u8f83\u7ea7\u4e0d\u9700\u8981'more'"}, {"\\bmore\\s+faster\\b", "faster", "DOUBLE_COMPARATIVE", "\u6bd4\u8f83\u7ea7\u4e0d\u9700\u8981'more'"}, {"\\bmost\\s+best\\b", "best", "DOUBLE_SUPERLATIVE", "\u6700\u9ad8\u7ea7\u4e0d\u9700\u8981'most'"}, {"\\balot\\b", "a lot", "SPACING", "'a lot'\u5e94\u8be5\u5206\u5f00\u5199"}, {"\\beveryday\\b(?!\\s+life|\\s+use)", "every day", "WORD_FORM", "\u4f5c\u4e3a\u526f\u8bcd\u65f6\u5e94\u5206\u5f00\u5199'every day'"}, {"\\s{2,}", " ", "SPACING", "\u591a\u4f59\u7684\u7a7a\u683c"}};
        int errorId = 1;
        for (String[] rule : grammarRules) {
            Pattern pattern = Pattern.compile(rule[0], 2);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                error = new HashMap<String, Object>();
                error.put("id", errorId++);
                error.put("type", rule[2]);
                error.put("position", matcher.start());
                error.put("length", matcher.end() - matcher.start());
                error.put("original", matcher.group());
                error.put("suggestion", rule[1]);
                error.put("message", rule[3]);
                errors.add(error);
                correctedText = correctedText.replaceFirst(rule[0], rule[1]);
            }
        }
        String[] sentences = text.split("(?<=[.!?])\\s+");
        int position = 0;
        for (String sentence : sentences) {
            if (!sentence.isEmpty() && Character.isLowerCase(sentence.charAt(0))) {
                error = new HashMap<>();
                error.put("id", errorId++);
                error.put("type", "CAPITALIZATION");
                error.put("position", position);
                error.put("length", 1);
                error.put("original", String.valueOf(sentence.charAt(0)));
                error.put("suggestion", String.valueOf(Character.toUpperCase(sentence.charAt(0))));
                error.put("message", "\u53e5\u5b50\u9996\u5b57\u6bcd\u5e94\u8be5\u5927\u5199");
                errors.add(error);
            }
            position += sentence.length() + 1;
        }
        int totalWords = text.split("\\s+").length;
        int errorCount = errors.size();
        double score = Math.max(0.0, 100.0 - (double)errorCount * 100.0 / (double)Math.max(totalWords, 1) * 5.0);
        score = Math.min(100.0, score);
        result.put("originalText", text);
        result.put("correctedText", correctedText);
        result.put("errors", errors);
        result.put("errorCount", errorCount);
        result.put("score", BigDecimal.valueOf(score).setScale(1, RoundingMode.HALF_UP));
        result.put("wordCount", totalWords);
        ArrayList<String> suggestions = new ArrayList<String>();
        if (errorCount == 0) {
            suggestions.add("Great job! No grammar errors detected.");
        } else {
            if (errors.stream().anyMatch(e -> "CAPITALIZATION".equals(((Map<?, ?>)e).get("type")))) {
                suggestions.add("Pay attention to capitalization rules.");
            }
            if (errors.stream().anyMatch(e -> "APOSTROPHE".equals(((Map<?, ?>)e).get("type")))) {
                suggestions.add("Remember to use apostrophes in contractions.");
            }
            if (errors.stream().anyMatch(e -> "SUBJECT_VERB".equals(((Map<?, ?>)e).get("type")))) {
                suggestions.add("Review subject-verb agreement rules.");
            }
            if (errors.stream().anyMatch(e -> "ARTICLE".equals(((Map<?, ?>)e).get("type")))) {
                suggestions.add("Practice using articles (a/an/the) correctly.");
            }
        }
        result.put("suggestions", suggestions);
        return result;
    }

    public Map<String, Object> recommendLearningPath(Long userId, String currentLevel, String targetLevel) {
        HashMap<String, Object> path2;
        HashMap<String, Object> path1;
        log.info("AI Learning Path Recommendation - User: {}, Current: {}, Target: {}", new Object[]{userId, currentLevel, targetLevel});
        HashMap<String, Object> result = new HashMap<String, Object>();
        ArrayList recommendedPaths = new ArrayList();
        if ("CET4".equals(targetLevel) || currentLevel == null) {
            path1 = new HashMap<String, Object>();
            path1.put("pathName", "\u56db\u7ea7\u8bcd\u6c47\u7a81\u7834");
            path1.put("description", "\u7cfb\u7edf\u5b66\u4e60\u56db\u7ea7\u6838\u5fc3\u8bcd\u6c474500\u8bcd");
            path1.put("estimatedDays", 60);
            path1.put("difficulty", 2);
            path1.put("priority", 1);
            recommendedPaths.add(path1);
            path2 = new HashMap<String, Object>();
            path2.put("pathName", "\u56db\u7ea7\u542c\u529b\u5f3a\u5316");
            path2.put("description", "\u63d0\u5347\u542c\u529b\u7406\u89e3\u80fd\u529b\uff0c\u638c\u63e1\u542c\u529b\u6280\u5de7");
            path2.put("estimatedDays", 45);
            path2.put("difficulty", 2);
            path2.put("priority", 2);
            recommendedPaths.add(path2);
            HashMap<String, Object> path3 = new HashMap<String, Object>();
            path3.put("pathName", "\u56db\u7ea7\u9605\u8bfb\u7406\u89e3");
            path3.put("description", "\u63d0\u9ad8\u9605\u8bfb\u901f\u5ea6\u548c\u7406\u89e3\u51c6\u786e\u7387");
            path3.put("estimatedDays", 30);
            path3.put("difficulty", 2);
            path3.put("priority", 3);
            recommendedPaths.add(path3);
        }
        if ("CET6".equals(targetLevel)) {
            path1 = new HashMap();
            path1.put("pathName", "\u516d\u7ea7\u8bcd\u6c47\u8fdb\u9636");
            path1.put("description", "\u638c\u63e1\u516d\u7ea7\u9ad8\u9891\u8bcd\u6c47\u548c\u9ad8\u7ea7\u8868\u8fbe");
            path1.put("estimatedDays", 90);
            path1.put("difficulty", 3);
            path1.put("priority", 1);
            recommendedPaths.add(path1);
            path2 = new HashMap();
            path2.put("pathName", "\u516d\u7ea7\u5199\u4f5c\u63d0\u5347");
            path2.put("description", "\u5b66\u4e60\u9ad8\u7ea7\u5199\u4f5c\u6280\u5de7\u548c\u6a21\u677f");
            path2.put("estimatedDays", 45);
            path2.put("difficulty", 3);
            path2.put("priority", 2);
            recommendedPaths.add(path2);
        }
        result.put("recommendedPaths", recommendedPaths);
        HashMap<String, Integer> dailyPlan = new HashMap<String, Integer>();
        dailyPlan.put("vocabularyTime", 30);
        dailyPlan.put("listeningTime", 20);
        dailyPlan.put("readingTime", 25);
        dailyPlan.put("writingTime", 15);
        dailyPlan.put("totalTime", 90);
        dailyPlan.put("questionsPerDay", 20);
        result.put("dailyPlan", dailyPlan);
        ArrayList weakPoints = new ArrayList();
        HashMap<String, Object> weak1 = new HashMap<String, Object>();
        weak1.put("skill", "\u542c\u529b");
        weak1.put("score", 65);
        weak1.put("suggestion", "\u5efa\u8bae\u6bcf\u5929\u589e\u52a015\u5206\u949f\u542c\u529b\u7ec3\u4e60");
        weakPoints.add(weak1);
        HashMap<String, Object> weak2 = new HashMap<String, Object>();
        weak2.put("skill", "\u5199\u4f5c");
        weak2.put("score", 70);
        weak2.put("suggestion", "\u5efa\u8bae\u6bcf\u5468\u5b8c\u62102\u7bc7\u4f5c\u6587\u7ec3\u4e60");
        weakPoints.add(weak2);
        result.put("weakPoints", weakPoints);
        return result;
    }

    @Transactional(rollbackFor={Exception.class})
    public void clearChatHistory(Long userId, String sessionId) {
        LambdaQueryWrapper<AiChatRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatRecord::getUserId, userId);
        if (sessionId != null && !sessionId.isEmpty()) {
            wrapper.eq(AiChatRecord::getSessionId, sessionId);
        }
        this.aiChatRecordMapper.delete(wrapper);
        log.info("\u6e05\u7a7a\u5bf9\u8bdd\u5386\u53f2 - \u7528\u6237: {}, \u4f1a\u8bdd: {}",userId,sessionId);
    }

    public WritingCorrection getWritingDetail(Long id, Long userId) {
        WritingCorrection correction = (WritingCorrection)this.writingCorrectionMapper.selectById(id);
        if (correction == null || !correction.getStudentId().equals(userId)) {
            throw new BusinessException("\u4f5c\u6587\u6279\u6539\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        return correction;
    }

    public PronunciationEvaluation getPronunciationDetail(Long id, Long userId) {
        PronunciationEvaluation evaluation = (PronunciationEvaluation)this.pronunciationEvaluationMapper.selectById(id);
        if (evaluation == null || !evaluation.getStudentId().equals(userId)) {
            throw new BusinessException("\u53d1\u97f3\u8bc4\u4f30\u8bb0\u5f55\u4e0d\u5b58\u5728");
        }
        return evaluation;
    }

    public Map<String, Object> getUsageStats(Long userId, Integer days) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        LambdaQueryWrapper<AiChatRecord> chatWrapper = new LambdaQueryWrapper<>();
        chatWrapper.eq(AiChatRecord::getUserId, userId);
        if (days != null) {
            chatWrapper.ge(AiChatRecord::getCreateTime, LocalDateTime.now().minusDays(days.intValue()));
        }
        long chatCount = this.aiChatRecordMapper.selectCount(chatWrapper);
        result.put("chatCount", chatCount);
        LambdaQueryWrapper<WritingCorrection> writingWrapper = new LambdaQueryWrapper<>();
        writingWrapper.eq(WritingCorrection::getStudentId, userId);
        if (days != null) {
            writingWrapper.ge(WritingCorrection::getCreateTime, LocalDateTime.now().minusDays(days.intValue()));
        }
        long writingCount = this.writingCorrectionMapper.selectCount(writingWrapper);
        result.put("writingCount", writingCount);
        LambdaQueryWrapper<PronunciationEvaluation> pronWrapper = new LambdaQueryWrapper<>();
        pronWrapper.eq(PronunciationEvaluation::getStudentId, userId);
        if (days != null) {
            pronWrapper.ge(PronunciationEvaluation::getCreateTime, LocalDateTime.now().minusDays(days.intValue()));
        }
        long pronunciationCount = this.pronunciationEvaluationMapper.selectCount(pronWrapper);
        result.put("pronunciationCount", pronunciationCount);
        result.put("grammarCount", 0);
        return result;
    }

    public List<Map<String, Object>> getRecentUsage(Long userId, Integer limit) {
        ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<AiChatRecord> chatRecords = this.aiChatRecordMapper.selectList(new LambdaQueryWrapper<AiChatRecord>()
            .eq(AiChatRecord::getUserId, userId)
            .orderByDesc(AiChatRecord::getCreateTime)
            .last("LIMIT " + limit));
        for (AiChatRecord record : chatRecords) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", record.getId());
            item.put("type", "chat");
            String content = record.getMessageContent();
            item.put("title", content != null && content.length() > 20 ? content.substring(0, 20) + "..." : content);
            item.put("summary", content);
            item.put("createTime", record.getCreateTime());
            result.add(item);
        }
        List<WritingCorrection> writingRecords = this.writingCorrectionMapper.selectList(new LambdaQueryWrapper<WritingCorrection>()
            .eq(WritingCorrection::getStudentId, userId)
            .orderByDesc(WritingCorrection::getCreateTime)
            .last("LIMIT " + limit));
        for (WritingCorrection record : writingRecords) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", record.getId());
            item.put("type", "writing");
            item.put("title", record.getTitle() != null ? record.getTitle() : "\u4f5c\u6587\u6279\u6539");
            item.put("summary", "\u5f97\u5206: " + record.getTotalScore());
            item.put("createTime", record.getCreateTime());
            result.add(item);
        }
        result.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime)a.get("createTime");
            LocalDateTime timeB = (LocalDateTime)b.get("createTime");
            return timeB.compareTo(timeA);
        });
        return result.size() > limit ? result.subList(0, limit) : result;
    }

    public Map<String, Object> createSession(Long userId) {
        String sessionId = UUID.randomUUID().toString();
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("sessionId", sessionId);
        result.put("createTime", LocalDateTime.now());
        log.info("Created new chat session: {} for user: {}",sessionId, userId);
        return result;
    }

    public void deleteSession(Long userId, String sessionId) {
        this.aiChatRecordMapper.delete(new LambdaQueryWrapper<AiChatRecord>()
            .eq(AiChatRecord::getUserId, userId)
            .eq(AiChatRecord::getSessionId, sessionId));
        log.info("Deleted chat session: {} for user: {}",sessionId, userId);
    }

    public void clearChatHistory(Long userId) {
        this.aiChatRecordMapper.delete(new LambdaQueryWrapper<AiChatRecord>().eq(AiChatRecord::getUserId, userId));
        log.info("Cleared chat history for user: {}",userId);
    }

    public AIService(AiChatRecordMapper aiChatRecordMapper, WritingCorrectionMapper writingCorrectionMapper, PronunciationEvaluationMapper pronunciationEvaluationMapper, ObjectMapper objectMapper) {
        this.aiChatRecordMapper = aiChatRecordMapper;
        this.writingCorrectionMapper = writingCorrectionMapper;
        this.pronunciationEvaluationMapper = pronunciationEvaluationMapper;
        this.objectMapper = objectMapper;
    }
}

