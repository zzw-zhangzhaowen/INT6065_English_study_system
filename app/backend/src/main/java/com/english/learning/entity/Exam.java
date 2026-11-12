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
@TableName(value="exam")
public class Exam implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private String examName;
    private String examType;
    private String level;
    private String description;
    private BigDecimal totalScore;
    private BigDecimal passScore;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxAttempts;
    private Integer enableAntiCheat;
    private Integer randomQuestion;
    private Integer randomOption;
    private Integer showResult;
    private Long creatorId;
    private Integer status;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
    @TableField(exist=false)
    private Integer questionCount;
}
