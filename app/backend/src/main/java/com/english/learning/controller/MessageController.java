package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.dto.MessageBroadcastRequest;
import com.english.learning.dto.MessageSendRequest;
import com.english.learning.entity.Message;
import com.english.learning.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u6d88\u606f\u7ba1\u7406")
@RestController
@RequestMapping(value={"/api/message"})
public class MessageController {
    private final MessageService messageService;

    @Operation(summary="\u53d1\u9001\u6d88\u606f\u7ed9\u5b66\u751f")
    @PostMapping(value={"/send/{studentId}"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> sendMessage(Authentication authentication, @PathVariable Long studentId, @Valid @RequestBody MessageSendRequest request) {
        Long senderId = (Long)authentication.getDetails();
        String type = request.getType() == null || request.getType().isBlank() ? "NOTIFICATION" : request.getType();
        this.messageService.sendMessage(senderId, studentId, request.getTitle(), request.getContent(), type);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u6536\u4ef6\u7bb1\u6d88\u606f")
    @GetMapping(value={"/inbox"})
    public Result<Page<Message>> getInbox(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String type, @RequestParam(required=false) String keyword) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.messageService.getInbox(userId, pageNum, pageSize, type, keyword));
    }

    @Operation(summary="\u83b7\u53d6\u53d1\u4ef6\u7bb1\u6d88\u606f")
    @GetMapping(value={"/outbox"})
    public Result<Page<Message>> getOutbox(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String keyword) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.messageService.getOutbox(userId, pageNum, pageSize, keyword));
    }

    @Operation(summary="\u83b7\u53d6\u6d88\u606f\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<Message> getMessageDetail(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.messageService.getMessageDetail(id, userId));
    }

    @Operation(summary="\u6807\u8bb0\u6d88\u606f\u4e3a\u5df2\u8bfb")
    @PutMapping(value={"/{id}/read"})
    public Result<Void> markAsRead(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.messageService.markAsRead(id, userId);
        return Result.success();
    }

    @Operation(summary="\u6279\u91cf\u6807\u8bb0\u4e3a\u5df2\u8bfb")
    @PutMapping(value={"/read/batch"})
    public Result<Void> markAllAsRead(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        this.messageService.markAllAsRead(userId);
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u6d88\u606f")
    @DeleteMapping(value={"/{id}"})
    public Result<Void> deleteMessage(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.messageService.deleteMessage(id, userId);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u672a\u8bfb\u6d88\u606f\u6570\u91cf")
    @GetMapping(value={"/unread/count"})
    public Result<Long> getUnreadCount(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.messageService.getUnreadCount(userId));
    }

    @Operation(summary="\u5e7f\u64ad\u6d88\u606f\u7ed9\u6240\u6709\u5b66\u751f")
    @PostMapping(value={"/broadcast"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> broadcastMessage(Authentication authentication, @Valid @RequestBody MessageBroadcastRequest request) {
        Long senderId = (Long)authentication.getDetails();
        this.messageService.broadcastMessage(senderId, request.getTitle(), request.getContent());
        return Result.success();
    }

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
}

