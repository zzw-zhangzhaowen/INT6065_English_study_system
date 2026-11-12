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
@TableName(value="question")
public class Question implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String questionType;
    private String title;
    private String content;
    private Long passageId;
    private String options;
    private String answer;
    private String analysis;
    private Integer difficulty;
    private BigDecimal score;
    private String audioUrl;
    private String videoUrl;
    private String imageUrl;
    private String keywords;
    private Integer usageCount;
    private BigDecimal correctRate;
    private Long creatorId;
    private Integer status;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
