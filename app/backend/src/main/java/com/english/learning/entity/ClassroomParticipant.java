package com.english.learning.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value="classroom_participant")
public class ClassroomParticipant implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long classroomId;
    private Long studentId;
    private LocalDateTime joinTime;
    private LocalDateTime leaveTime;
    private LocalDateTime signInTime;
    private Integer duration;
    private Integer isSigned;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
}
