package com.english.learning.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value="daily_checkin")
public class DailyCheckin implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate checkinDate;
    private LocalDateTime checkinTime;
    private Integer studyDuration;
    private Integer wordCount;
    private Integer questionCount;
    private String notes;
    private String mood;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
