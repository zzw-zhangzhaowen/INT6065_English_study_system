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
@TableName(value="answer_record")
public class AnswerRecord implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long examRecordId;
    private Long questionId;
    private String studentAnswer;
    private Integer isCorrect;
    private BigDecimal obtainedScore;
    private Integer answerTime;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
}
