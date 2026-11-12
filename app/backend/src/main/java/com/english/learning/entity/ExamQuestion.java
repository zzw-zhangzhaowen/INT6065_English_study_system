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
@TableName(value="exam_question")
public class ExamQuestion implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long examId;
    private Long questionId;
    private Integer questionOrder;
    private BigDecimal questionScore;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
}
