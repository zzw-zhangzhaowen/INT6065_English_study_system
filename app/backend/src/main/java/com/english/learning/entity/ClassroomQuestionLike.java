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
@TableName(value="classroom_question_like")
public class ClassroomQuestionLike implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long questionId;
    private Long userId;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
}
