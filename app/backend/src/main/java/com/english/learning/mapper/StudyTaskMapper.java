package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.StudyTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudyTaskMapper
extends BaseMapper<StudyTask> {
}

