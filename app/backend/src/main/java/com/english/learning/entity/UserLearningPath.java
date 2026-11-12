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
@TableName(value="user_learning_path")
public class UserLearningPath implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long userId;
    private Long pathId;
    private Long currentNodeId;
    private Integer progress;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private String status;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
