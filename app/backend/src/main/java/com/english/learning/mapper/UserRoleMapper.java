package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper
extends BaseMapper<UserRole> {
}

