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
@TableName(value="pronunciation_evaluation")
public class PronunciationEvaluation implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long studentId;
    private String textContent;
    private String audioUrl;
    private BigDecimal totalScore;
    private BigDecimal accuracyScore;
    private BigDecimal fluencyScore;
    private BigDecimal intonationScore;
    private BigDecimal speedScore;
    private String recognizedText;
    private String errorDetails;
    private String suggestions;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
}
