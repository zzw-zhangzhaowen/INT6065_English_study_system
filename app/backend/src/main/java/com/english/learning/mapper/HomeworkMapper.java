package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.Homework;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HomeworkMapper
extends BaseMapper<Homework> {
}

