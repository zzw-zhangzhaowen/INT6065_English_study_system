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
@TableName(value="homework_submission")
public class HomeworkSubmission implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long homeworkId;
    private Long studentId;
    private String content;
    private String attachmentUrl;
    private LocalDateTime submitTime;
    private BigDecimal score;
    private String feedback;
    private String status;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
