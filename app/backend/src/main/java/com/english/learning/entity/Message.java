package com.english.learning.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value="sys_message")
public class Message {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String title;
    private String content;
    private String type;
    private Integer isRead;
    private LocalDateTime readTime;
    @TableField(value="sender_deleted")
    private Integer senderDeleted = 0;
    @TableField(value="receiver_deleted")
    private Integer receiverDeleted = 0;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
}
