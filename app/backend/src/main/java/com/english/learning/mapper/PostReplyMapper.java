package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.PostReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostReplyMapper
extends BaseMapper<PostReply> {
    @Update(value={"UPDATE post_reply SET like_count = like_count + #{delta} WHERE id = #{id} AND like_count + #{delta} >= 0"})
    public int incrementLikeCount(@Param(value="id") Long var1, @Param(value="delta") int var2);
}

