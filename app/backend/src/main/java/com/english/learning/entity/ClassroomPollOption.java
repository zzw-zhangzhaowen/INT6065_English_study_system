package com.english.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@Data
@TableName(value="classroom_poll_option")
public class ClassroomPollOption implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long pollId;
    private String content;
    private Integer voteCount;
    private Integer sortOrder;
}
