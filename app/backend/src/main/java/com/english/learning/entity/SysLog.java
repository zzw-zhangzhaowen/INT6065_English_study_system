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
@TableName(value="sys_log")
public class SysLog implements Serializable {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private String operation;
    private String method;
    private String params;
    private String ipAddress;
    private String location;
    private String browser;
    private String os;
    private Integer status;
    private String errorMsg;
    private Integer executeTime;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
}
