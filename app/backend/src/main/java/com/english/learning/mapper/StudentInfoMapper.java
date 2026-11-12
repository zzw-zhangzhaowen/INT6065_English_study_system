package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.StudentInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentInfoMapper
extends BaseMapper<StudentInfo> {
}

