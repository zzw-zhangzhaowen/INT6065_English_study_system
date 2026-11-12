package com.english.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.entity.Question;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface QuestionMapper
extends BaseMapper<Question> {
    public Page<Question> selectPageWithCategory(Page<Question> var1, @Param(value="categoryId") Long var2, @Param(value="questionType") String var3, @Param(value="difficulty") Integer var4, @Param(value="keyword") String var5);

    @Select(value={"SELECT question_type AS questionType, COUNT(*) AS cnt FROM question WHERE status = 1 AND deleted = 0 GROUP BY question_type"})
    public List<Map<String, Object>> countGroupByType();

    @Select(value={"SELECT difficulty, COUNT(*) AS cnt FROM question WHERE status = 1 AND deleted = 0 GROUP BY difficulty"})
    public List<Map<String, Object>> countGroupByDifficulty();

    @Update(value={"UPDATE question SET usage_count = usage_count + 1 WHERE id = #{questionId}"})
    public void incrementUsageCount(@Param(value="questionId") Long var1);

    @Update(value={"UPDATE question SET correct_rate = CASE   WHEN usage_count = 0 THEN #{correctRate}   ELSE (correct_rate * usage_count + #{correctRate}) / (usage_count + 1) END WHERE id = #{questionId}"})
    public void updateCorrectRate(@Param(value="questionId") Long var1, @Param(value="correctRate") Integer var2);
}

