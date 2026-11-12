package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.Classroom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ClassroomMapper
extends BaseMapper<Classroom> {
    @Update(value={"UPDATE classroom SET current_students = current_students + 1 WHERE id = #{classroomId} AND deleted = 0"})
    public int incrementStudents(@Param(value="classroomId") Long var1);

    @Update(value={"UPDATE classroom SET current_students = GREATEST(current_students - 1, 0) WHERE id = #{classroomId} AND deleted = 0"})
    public int decrementStudents(@Param(value="classroomId") Long var1);
}

