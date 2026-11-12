package com.english.learning.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value="study_plan")
public class StudyPlan implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long studentId;
    private String planName;
    private String targetLevel;
    private BigDecimal targetScore;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer dailyDuration;
    private Integer dailyQuestions;
    private Integer progress;
    private String status;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
