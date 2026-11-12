package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.ClassroomPollOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ClassroomPollOptionMapper
extends BaseMapper<ClassroomPollOption> {
    @Update(value={"UPDATE classroom_poll_option SET vote_count = vote_count + 1 WHERE id = #{optionId}"})
    public int incrementVoteCount(@Param(value="optionId") Long var1);

    @Update(value={"UPDATE classroom_poll_option SET vote_count = GREATEST(vote_count - 1, 0) WHERE id = #{optionId}"})
    public int decrementVoteCount(@Param(value="optionId") Long var1);
}

