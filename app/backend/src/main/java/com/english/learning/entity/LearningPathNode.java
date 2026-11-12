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
@TableName(value="learning_path_node")
public class LearningPathNode implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long pathId;
    private String nodeName;
    private String nodeType;
    private String content;
    private Long resourceId;
    private Integer sortOrder;
    private Integer estimatedMinutes;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
}
