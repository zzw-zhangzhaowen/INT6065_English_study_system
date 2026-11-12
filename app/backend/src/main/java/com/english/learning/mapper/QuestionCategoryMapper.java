package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.QuestionCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionCategoryMapper
extends BaseMapper<QuestionCategory> {
}

