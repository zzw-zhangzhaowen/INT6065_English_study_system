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
@TableName(value="student_info")
public class StudentInfo implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long userId;
    private String school;
    private String major;
    private String grade;
    private String studentNo;
    private String currentLevel;
    private String targetLevel;
    private Integer studyDays;
    private Integer continuousDays;
    private Integer totalStudyTime;
    private Integer achievementScore;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
