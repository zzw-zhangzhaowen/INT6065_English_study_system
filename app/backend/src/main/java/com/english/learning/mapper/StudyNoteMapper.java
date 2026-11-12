package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.StudyNote;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudyNoteMapper
extends BaseMapper<StudyNote> {
}

