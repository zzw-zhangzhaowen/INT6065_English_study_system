package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.english.learning.entity.ExamRecord;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExamRecordMapper
extends BaseMapper<ExamRecord> {
    @Select(value={"SELECT AVG(obtained_score) FROM exam_record WHERE exam_id = #{examId} AND status = 'COMPLETED'"})
    public BigDecimal selectAvgScore(@Param(value="examId") Long var1);

    @Select(value={"SELECT COUNT(*) FROM exam_record WHERE exam_id = #{examId} AND status = 'COMPLETED' AND obtained_score >= #{passScore}"})
    public Long selectPassCount(@Param(value="examId") Long var1, @Param(value="passScore") BigDecimal var2);

    @Select(value={"SELECT CASE WHEN obtained_score >= 90 THEN '90-100' WHEN obtained_score >= 80 THEN '80-89' WHEN obtained_score >= 70 THEN '70-79' WHEN obtained_score >= 60 THEN '60-69' ELSE '0-59' END AS scoreRange, COUNT(*) AS count FROM exam_record WHERE exam_id = #{examId} AND status = 'COMPLETED' GROUP BY scoreRange ORDER BY scoreRange DESC"})
    public List<Map<String, Object>> selectScoreDistribution(@Param(value="examId") Long var1);
}

