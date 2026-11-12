package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.ClassroomParticipant;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ClassroomParticipantMapper
extends BaseMapper<ClassroomParticipant> {
    @Update(value={"UPDATE classroom_participant SET leave_time = #{leaveTime}, duration = TIMESTAMPDIFF(MINUTE, join_time, #{leaveTime}) WHERE classroom_id = #{classroomId} AND leave_time IS NULL AND deleted = 0"})
    public int batchSetLeaveTime(@Param(value="classroomId") Long var1, @Param(value="leaveTime") LocalDateTime var2);
}

