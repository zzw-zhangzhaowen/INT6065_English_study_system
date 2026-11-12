package com.english.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value="classroom_poll")
public class ClassroomPoll implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long classroomId;
    private Long creatorId;
    private String title;
    private String pollType;
    private String status;
    private Boolean anonymous;
    private LocalDateTime createTime;
    private LocalDateTime endTime;
}
