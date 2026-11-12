package com.english.learning.controller;

import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.Classroom;
import com.english.learning.entity.ClassroomAnswer;
import com.english.learning.entity.ClassroomMessage;
import com.english.learning.entity.ClassroomQuestion;
import com.english.learning.entity.User;
import com.english.learning.mapper.ClassroomMapper;
import com.english.learning.mapper.ClassroomMessageMapper;
import com.english.learning.mapper.UserMapper;
import com.english.learning.service.ClassroomPollService;
import com.english.learning.service.ClassroomQaService;
import com.english.learning.service.ClassroomService;
import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;

@Controller
public class ClassroomWsController {
    private static final Logger log = LoggerFactory.getLogger(ClassroomWsController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ClassroomMessageMapper messageMapper;
    private final UserMapper userMapper;
    private final ClassroomMapper classroomMapper;
    private final ClassroomQaService qaService;
    private final ClassroomPollService pollService;
    private final ClassroomService classroomService;

    @MessageMapping(value={"/classroom/{classroomId}/chat"})
    public void sendMessage(@DestinationVariable Long classroomId, @Payload Map<String, String> payload, Principal principal) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken)principal;
        Long userId = (Long)auth.getDetails();
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return;
        }
        String messageType = payload.getOrDefault("messageType", "CHAT");
        if (!"CHAT".equals(messageType) && !"SYSTEM".equals(messageType)) {
            messageType = "CHAT";
        }
        ClassroomMessage msg = new ClassroomMessage();
        msg.setClassroomId(classroomId);
        msg.setUserId(userId);
        msg.setContent(content.trim());
        msg.setMessageType(messageType);
        this.messageMapper.insert(msg);
        User user = (User)this.userMapper.selectById(userId);
        HashMap<String, Object> broadcast = new HashMap<String, Object>();
        broadcast.put("id", msg.getId());
        broadcast.put("userId", userId);
        broadcast.put("username", "SYSTEM".equals(messageType) ? "\u7cfb\u7edf\u901a\u77e5" : (user != null ? (user.getRealName() != null ? user.getRealName() : user.getUsername()) : "\u672a\u77e5"));
        broadcast.put("avatar", "SYSTEM".equals(messageType) ? null : (user != null ? user.getAvatar() : null));
        broadcast.put("content", content.trim());
        broadcast.put("messageType", messageType);
        broadcast.put("createTime", LocalDateTime.now().toString());
        this.messagingTemplate.convertAndSend("/topic/classroom/" + classroomId, broadcast);
    }

    @MessageMapping(value={"/classroom/{classroomId}/whiteboard"})
    public void handleWhiteboard(@DestinationVariable Long classroomId, @Payload Map<String, Object> payload, Principal principal) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken)principal;
        Long userId = (Long)auth.getDetails();
        Classroom classroom = (Classroom)this.classroomMapper.selectById(classroomId);
        if (classroom == null || !classroom.getTeacherId().equals(userId)) {
            log.warn("\u975e\u6559\u5e08\u7528\u6237 {} \u5c1d\u8bd5\u64cd\u4f5c\u767d\u677f\uff0c\u5df2\u62d2\u7edd",userId);
            return;
        }
        User user = (User)this.userMapper.selectById(userId);
        payload.put("userId", userId);
        payload.put("username", user != null ? (user.getRealName() != null ? user.getRealName() : user.getUsername()) : "\u672a\u77e5");
        payload.put("timestamp", System.currentTimeMillis());
        this.messagingTemplate.convertAndSend("/topic/classroom/" + classroomId + "/whiteboard", payload);
    }

    @MessageMapping(value={"/classroom/{classroomId}/qa"})
    public void handleQa(@DestinationVariable Long classroomId, @Payload Map<String, Object> payload, Principal principal) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken)principal;
        Long userId = (Long)auth.getDetails();
        this.ensureClassroomAccess(classroomId, userId, auth);
        String action = (String)payload.get("action");
        HashMap<String, Object> broadcast = new HashMap<String, Object>();
        broadcast.put("action", action);
        broadcast.put("classroomId", classroomId);
        broadcast.put("userId", userId);
        broadcast.put("timestamp", LocalDateTime.now().toString());
        try {
            switch (action) {
                case "CREATE_QUESTION": {
                    String content = (String)payload.get("content");
                    Boolean isAnonymous = (Boolean)payload.get("isAnonymous");
                    ClassroomQuestion question = this.qaService.createQuestion(classroomId, userId, content, isAnonymous);
                    broadcast.put("data", this.qaService.getQuestions(classroomId).stream().filter(q -> q.get("id").equals(question.getId())).findFirst().orElse(null));
                    break;
                }
                case "CREATE_ANSWER": {
                    Long questionId = Long.valueOf(payload.get("questionId").toString());
                    String content = (String)payload.get("content");
                    ClassroomAnswer answer = this.qaService.createAnswer(questionId, classroomId, userId, content);
                    User user = (User)this.userMapper.selectById(userId);
                    HashMap<String, Object> answerData = new HashMap<String, Object>();
                    answerData.put("id", answer.getId());
                    answerData.put("questionId", answer.getQuestionId());
                    answerData.put("userId", userId);
                    answerData.put("username", user != null ? (user.getRealName() != null ? user.getRealName() : user.getUsername()) : "\u672a\u77e5");
                    answerData.put("content", answer.getContent());
                    answerData.put("isBest", false);
                    answerData.put("createTime", answer.getCreateTime() != null ? answer.getCreateTime().toString() : LocalDateTime.now().toString());
                    broadcast.put("data", answerData);
                    break;
                }
                case "LIKE_QUESTION": {
                    Long questionId = Long.valueOf(payload.get("questionId").toString());
                    int likeCount = this.qaService.likeQuestion(classroomId, questionId, userId);
                    broadcast.put("questionId", questionId);
                    broadcast.put("likeCount", likeCount);
                    break;
                }
                case "CLOSE_QUESTION": {
                    this.ensureTeacherOrAdmin(auth);
                    Long questionId = Long.valueOf(payload.get("questionId").toString());
                    this.qaService.closeQuestion(classroomId, questionId, userId, this.isAdmin(auth));
                    broadcast.put("questionId", questionId);
                    break;
                }
                default: {
                    return;
                }
            }
        }
        catch (Exception e) {
            log.error("\u5904\u7406\u95ee\u7b54\u6d88\u606f\u5931\u8d25: {}",e.getMessage());
            HashMap<String, String> errorMsg = new HashMap<String, String>();
            errorMsg.put("action", "ERROR");
            errorMsg.put("originalAction", action);
            errorMsg.put("message", e.getMessage());
            this.messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/classroom/" + classroomId + "/qa/errors", errorMsg);
            return;
        }
        this.messagingTemplate.convertAndSend("/topic/classroom/" + classroomId + "/qa", broadcast);
    }

    @MessageMapping(value={"/classroom/{classroomId}/poll"})
    public void handlePoll(@DestinationVariable Long classroomId, @Payload Map<String, Object> payload, Principal principal) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken)principal;
        Long userId = (Long)auth.getDetails();
        this.ensureClassroomAccess(classroomId, userId, auth);
        String action = (String)payload.get("action");
        HashMap<String, Object> broadcast = new HashMap<String, Object>();
        broadcast.put("action", action);
        broadcast.put("classroomId", classroomId);
        broadcast.put("userId", userId);
        broadcast.put("timestamp", LocalDateTime.now().toString());
        try {
            switch (action) {
                case "CREATE_POLL": {
                    this.ensureTeacherOrAdmin(auth);
                    String title = (String)payload.get("title");
                    String pollType = (String)payload.get("pollType");
                    Boolean anonymous = (Boolean)payload.get("anonymous");
                    List<String> options = (List<String>)payload.get("options");
                    Map<String, Object> pollData = this.pollService.createPoll(classroomId, userId, title, pollType, anonymous, options);
                    broadcast.put("data", pollData);
                    break;
                }
                case "VOTE": {
                    Long pollId = Long.valueOf(payload.get("pollId").toString());
                    List<?> optionIds = (List<?>)payload.get("optionIds");
                    List<Long> ids = optionIds.stream().map(obj -> ((Number)obj).longValue()).toList();
                    Map<String, Object> pollData = this.pollService.vote(classroomId, pollId, userId, ids);
                    broadcast.put("data", pollData);
                    break;
                }
                case "CLOSE_POLL": {
                    this.ensureTeacherOrAdmin(auth);
                    Long pollId = Long.valueOf(payload.get("pollId").toString());
                    Map<String, Object> pollData = this.pollService.closePoll(classroomId, pollId, userId, this.isAdmin(auth));
                    broadcast.put("data", pollData);
                    break;
                }
                default: {
                    return;
                }
            }
        }
        catch (Exception e) {
            log.error("\u5904\u7406\u6295\u7968\u6d88\u606f\u5931\u8d25: {}",e.getMessage());
            HashMap<String, String> errorMsg = new HashMap<String, String>();
            errorMsg.put("action", "ERROR");
            errorMsg.put("originalAction", action);
            errorMsg.put("message", e.getMessage());
            this.messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/classroom/" + classroomId + "/poll/errors", errorMsg);
            return;
        }
        this.messagingTemplate.convertAndSend("/topic/classroom/" + classroomId + "/poll", broadcast);
    }

    private boolean isAdmin(UsernamePasswordAuthenticationToken auth) {
        return auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private void ensureTeacherOrAdmin(UsernamePasswordAuthenticationToken auth) {
        boolean teacherOrAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_TEACHER".equals(a.getAuthority()) || "ROLE_ADMIN".equals(a.getAuthority()));
        if (!teacherOrAdmin) {
            throw new BusinessException("\u65e0\u6743\u6267\u884c\u8be5\u64cd\u4f5c");
        }
    }

    private void ensureClassroomAccess(Long classroomId, Long userId, UsernamePasswordAuthenticationToken auth) {
        String role = ((GrantedAuthority)auth.getAuthorities().iterator().next()).getAuthority();
        if (!this.classroomService.hasClassroomAccess(classroomId, userId, role)) {
            throw new BusinessException("\u65e0\u6743\u8bbf\u95ee\u6b64\u8bfe\u5802");
        }
    }

    public void sendSystemMessage(Long classroomId, String content) {
        ClassroomMessage msg = new ClassroomMessage();
        msg.setClassroomId(classroomId);
        msg.setUserId(Long.valueOf(0L));
        msg.setContent(content);
        msg.setMessageType("SYSTEM");
        this.messageMapper.insert(msg);
        HashMap<String, Object> broadcast = new HashMap<String, Object>();
        broadcast.put("id", msg.getId());
        broadcast.put("userId", 0L);
        broadcast.put("username", "\u7cfb\u7edf\u901a\u77e5");
        broadcast.put("content", content);
        broadcast.put("messageType", "SYSTEM");
        broadcast.put("createTime", LocalDateTime.now().toString());
        this.messagingTemplate.convertAndSend("/topic/classroom/" + classroomId, broadcast);
    }

    public ClassroomWsController(SimpMessagingTemplate messagingTemplate, ClassroomMessageMapper messageMapper, UserMapper userMapper, ClassroomMapper classroomMapper, ClassroomQaService qaService, ClassroomPollService pollService, ClassroomService classroomService) {
        this.messagingTemplate = messagingTemplate;
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
        this.classroomMapper = classroomMapper;
        this.qaService = qaService;
        this.pollService = pollService;
        this.classroomService = classroomService;
    }
}

