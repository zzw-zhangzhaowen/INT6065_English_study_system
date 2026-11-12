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
@TableName(value="class_student")
public class ClassStudent implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long classId;
    private Long studentId;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
}
