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
@TableName(value="writing_correction")
public class WritingCorrection implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long studentId;
    private String title;
    private String content;
    private Integer wordCount;
    private BigDecimal totalScore;
    private BigDecimal contentScore;
    private BigDecimal structureScore;
    private BigDecimal languageScore;
    private BigDecimal vocabularyScore;
    private String errors;
    private String suggestions;
    private String correctedContent;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
}
