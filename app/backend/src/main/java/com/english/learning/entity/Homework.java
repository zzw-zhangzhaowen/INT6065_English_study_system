package com.english.learning.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value="homework")
public class Homework implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Long teacherId;
    private String level;
    private String homeworkType;
    private LocalDateTime deadline;
    private BigDecimal totalScore;
    private String attachmentUrl;
    private Integer status;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
    @TableField(exist=false)
    private Boolean submitted;
    @TableField(exist=false)
    private String submissionStatus;
    @TableField(exist=false)
    private BigDecimal score;
    @TableField(exist=false)
    private String teacherName;
}
