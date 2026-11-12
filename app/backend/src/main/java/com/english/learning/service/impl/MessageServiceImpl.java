package com.english.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.entity.Message;
import com.english.learning.entity.Role;
import com.english.learning.entity.User;
import com.english.learning.entity.UserRole;
import com.english.learning.mapper.MessageMapper;
import com.english.learning.mapper.RoleMapper;
import com.english.learning.mapper.UserMapper;
import com.english.learning.mapper.UserRoleMapper;
import com.english.learning.service.MessageService;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageServiceImpl
implements MessageService {
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    @Transactional
    public void sendMessage(Long senderId, Long receiverId, String title, String content, String type) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        this.messageMapper.insert(message);
    }

    public Page<Message> getInbox(Long userId, int pageNum, int pageSize, String type, String keyword) {
        Page<Message> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId).eq(Message::getReceiverDeleted,0);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Message::getType,type);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Message::getTitle, keyword).or().like(Message::getContent, keyword));
        }
        wrapper.orderByDesc(Message::getCreateTime);
        return (Page<Message>)this.messageMapper.selectPage((IPage<Message>)page, wrapper);
    }

    public Page<Message> getOutbox(Long userId, int pageNum, int pageSize, String keyword) {
        Page<Message> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getSenderId, userId).eq(Message::getSenderDeleted,0);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Message::getTitle, keyword).or().like(Message::getContent, keyword));
        }
        wrapper.orderByDesc(Message::getCreateTime);
        return (Page<Message>)this.messageMapper.selectPage((IPage<Message>)page, wrapper);
    }

    public Message getMessageDetail(Long messageId, Long userId) {
        Message message = this.messageMapper.selectById(messageId);
        if (message == null) {
            return null;
        }
        boolean isSender = message.getSenderId().equals(userId);
        boolean isReceiver = message.getReceiverId().equals(userId);
        if (!isSender && !isReceiver) {
            return null;
        }
        if (isReceiver && message.getIsRead() == 0) {
            message.setIsRead(1);
            message.setReadTime(LocalDateTime.now());
            this.messageMapper.updateById(message);
        }
        return message;
    }

    @Transactional
    public void markAsRead(Long messageId, Long userId) {
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getId,messageId).eq(Message::getReceiverId, userId).set(Message::getIsRead,1).set(Message::getReadTime,LocalDateTime.now());
        this.messageMapper.update(null, wrapper);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getReceiverId, userId).eq(Message::getIsRead,0).eq(Message::getReceiverDeleted,0).set(Message::getIsRead,1).set(Message::getReadTime,LocalDateTime.now());
        this.messageMapper.update(null, wrapper);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = this.messageMapper.selectById(messageId);
        if (message == null) {
            return;
        }
        if (message.getSenderId().equals(userId)) {
            message.setSenderDeleted(1);
        }
        if (message.getReceiverId().equals(userId)) {
            message.setReceiverDeleted(1);
        }
        if (message.getSenderDeleted() == 1 && message.getReceiverDeleted() == 1) {
            this.messageMapper.deleteById(messageId);
        } else {
            this.messageMapper.updateById(message);
        }
    }

    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId).eq(Message::getIsRead,0).eq(Message::getReceiverDeleted,0);
        return this.messageMapper.selectCount(wrapper);
    }

    @Transactional
    public void broadcastMessage(Long senderId, String title, String content) {
        Role studentRole = this.roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, "ROLE_STUDENT"));
        if (studentRole == null) {
            return;
        }
        List<UserRole> userRoles = this.userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId,studentRole.getId()));
        List<Long> studentIds = userRoles.stream().map(UserRole::getUserId).collect(Collectors.toList());
        if (studentIds.isEmpty()) {
            return;
        }
        List<User> students = this.userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, studentIds).eq(User::getStatus,1));
        LocalDateTime now = LocalDateTime.now();
        ArrayList<Message> messageList = new ArrayList<Message>();
        for (User student : students) {
            Message message = new Message();
            message.setSenderId(senderId);
            message.setReceiverId(student.getId());
            message.setTitle(title);
            message.setContent(content);
            message.setType("BROADCAST");
            message.setIsRead(0);
            message.setCreateTime(now);
            messageList.add(message);
        }
        int batchSize = 500;
        for (int i = 0; i < messageList.size(); i += batchSize) {
            List<Message> batch = messageList.subList(i, Math.min(i + batchSize, messageList.size()));
            for (Message msg : batch) {
                this.messageMapper.insert(msg);
            }
        }
    }

    public MessageServiceImpl(MessageMapper messageMapper, UserMapper userMapper, UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }
}

