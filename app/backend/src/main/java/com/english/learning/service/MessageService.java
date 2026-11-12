package com.english.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.entity.Message;

public interface MessageService {
    public void sendMessage(Long var1, Long var2, String var3, String var4, String var5);

    public Page<Message> getInbox(Long var1, int var2, int var3, String var4, String var5);

    public Page<Message> getOutbox(Long var1, int var2, int var3, String var4);

    public Message getMessageDetail(Long var1, Long var2);

    public void markAsRead(Long var1, Long var2);

    public void markAllAsRead(Long var1);

    public void deleteMessage(Long var1, Long var2);

    public Long getUnreadCount(Long var1);

    public void broadcastMessage(Long var1, String var2, String var3);
}

