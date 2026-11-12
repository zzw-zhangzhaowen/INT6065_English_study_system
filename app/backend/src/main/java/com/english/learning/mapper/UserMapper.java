package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper
extends BaseMapper<User> {
    @Select(value={"SELECT r.role_code FROM sys_role r INNER JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId} AND r.status = 1 AND r.deleted = 0"})
    public List<String> selectRoleCodesByUserId(Long var1);

    @Select(value={"SELECT DISTINCT ur.user_id FROM sys_user_role ur INNER JOIN sys_role r ON r.id = ur.role_id WHERE r.role_code = #{roleCode} AND r.status = 1 AND r.deleted = 0"})
    public List<Long> selectUserIdsByRoleCode(String var1);
}

