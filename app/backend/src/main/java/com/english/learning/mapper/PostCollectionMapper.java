package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.PostCollection;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostCollectionMapper
extends BaseMapper<PostCollection> {
}

