package com.english.learning.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value="learning_resource")
public class LearningResource implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private String resourceName;
    private String resourceType;
    private String level;
    private String skillType;
    private Long categoryId;
    private String description;
    private String coverUrl;
    private String fileUrl;
    private Long fileSize;
    private Integer duration;
    private Long viewCount;
    private Long downloadCount;
    private Long likeCount;
    private String tags;
    private Long creatorId;
    private Integer status;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
