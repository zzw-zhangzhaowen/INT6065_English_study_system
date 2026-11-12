package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.ClassroomMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClassroomMessageMapper
extends BaseMapper<ClassroomMessage> {
}

