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
@TableName(value="community_post")
public class CommunityPost implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    @TableField(value="author_id")
    private Long userId;
    @TableField(value="category_id")
    private Long categoryId;
    private String title;
    private String content;
    private String postType;
    private Integer isTopped;
    private Integer isFeatured;
    private Integer viewCount;
    private Integer likeCount;
    private Integer replyCount;
    private Integer collectCount;
    private LocalDateTime lastReplyTime;
    private Integer status;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
