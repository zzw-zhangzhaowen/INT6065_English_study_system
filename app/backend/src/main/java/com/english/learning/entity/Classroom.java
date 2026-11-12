package com.english.learning.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value="classroom")
public class Classroom implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long teacherId;
    private String classroomName;
    private String classroomType;
    private String description;
    private String coverImage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private Integer maxStudents;
    private Integer currentStudents;
    private String roomUrl;
    private String recordUrl;
    private String status;
    private Integer signInActive;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
