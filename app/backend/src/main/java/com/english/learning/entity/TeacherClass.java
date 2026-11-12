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
@TableName(value="teacher_class")
public class TeacherClass implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long teacherId;
    private Long classId;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
}
