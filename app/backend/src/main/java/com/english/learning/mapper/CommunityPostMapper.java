package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.CommunityPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommunityPostMapper
extends BaseMapper<CommunityPost> {
    @Update(value={"UPDATE community_post SET view_count = view_count + 1 WHERE id = #{id}"})
    public int incrementViewCount(@Param(value="id") Long var1);

    @Update(value={"UPDATE community_post SET like_count = like_count + #{delta} WHERE id = #{id} AND like_count + #{delta} >= 0"})
    public int incrementLikeCount(@Param(value="id") Long var1, @Param(value="delta") int var2);

    @Update(value={"UPDATE community_post SET collect_count = collect_count + #{delta} WHERE id = #{id} AND collect_count + #{delta} >= 0"})
    public int incrementCollectCount(@Param(value="id") Long var1, @Param(value="delta") int var2);

    @Update(value={"UPDATE community_post SET reply_count = reply_count + #{delta} WHERE id = #{id} AND reply_count + #{delta} >= 0"})
    public int incrementReplyCount(@Param(value="id") Long var1, @Param(value="delta") int var2);
}

