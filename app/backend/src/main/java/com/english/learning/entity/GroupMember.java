package com.english.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value="group_member")
public class GroupMember implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long groupId;
    private Long userId;
    private String memberRole;
    private LocalDateTime joinTime;
}
