package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.AnswerRecord;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AnswerRecordMapper
extends BaseMapper<AnswerRecord> {
    @Select(value={"SELECT ar.question_id AS questionId, COUNT(*) AS totalAnswers, SUM(CASE WHEN ar.is_correct = 1 THEN 1 ELSE 0 END) AS correctAnswers FROM answer_record ar INNER JOIN exam_record er ON ar.exam_record_id = er.id WHERE er.exam_id = #{examId} AND er.status = 'COMPLETED' GROUP BY ar.question_id"})
    public List<Map<String, Object>> selectQuestionAccuracy(@Param(value="examId") Long var1);
}

