package com.english.learning.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value="exam_record")
public class ExamRecord implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long examId;
    private Long studentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime submitTime;
    private Integer duration;
    private BigDecimal totalScore;
    private BigDecimal obtainedScore;
    private Integer correctCount;
    private Integer wrongCount;
    private Integer rankPosition;
    private String status;
    private String ipAddress;
    private String browserInfo;
    private Integer cheatWarningCount;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
