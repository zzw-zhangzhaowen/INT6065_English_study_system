package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.Question;
import com.english.learning.entity.QuestionCategory;
import com.english.learning.mapper.QuestionCategoryMapper;
import com.english.learning.mapper.QuestionMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuestionCategoryService {
    private final QuestionCategoryMapper questionCategoryMapper;
    private final QuestionMapper questionMapper;

    public List<QuestionCategory> getCategoryList(String level, String skillType) {
        LambdaQueryWrapper<QuestionCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(level != null, QuestionCategory::getLevel,level).eq(skillType != null, QuestionCategory::getSkillType,skillType).eq(QuestionCategory::getStatus,1).orderByAsc(QuestionCategory::getSortOrder);
        return this.questionCategoryMapper.selectList(wrapper);
    }

    public List<Map<String, Object>> getCategoryTree() {
        List<QuestionCategory> allCategories = this.questionCategoryMapper.selectList(new LambdaQueryWrapper<QuestionCategory>().eq(QuestionCategory::getStatus,1).orderByAsc(QuestionCategory::getSortOrder));
        HashMap<Long, Long> countMap = new HashMap<Long, Long>();
        for (QuestionCategory cat : allCategories) {
            long count = this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getCategoryId,cat.getId()).eq(Question::getStatus,1));
            countMap.put(cat.getId(), count);
        }
        Map<Long, List<QuestionCategory>> childrenMap = allCategories.stream().filter(c -> c.getParentId() != null && c.getParentId() > 0L).collect(Collectors.groupingBy(QuestionCategory::getParentId));
        return allCategories.stream().filter(c -> c.getParentId() == null || c.getParentId() == 0L).map(c -> this.buildCategoryNode(c, childrenMap, countMap)).collect(Collectors.toList());
    }

    private Map<String, Object> buildCategoryNode(QuestionCategory category, Map<Long, List<QuestionCategory>> childrenMap, Map<Long, Long> countMap) {
        HashMap<String, Object> node = new HashMap<String, Object>();
        node.put("id", category.getId());
        node.put("name", category.getCategoryName());
        node.put("categoryName", category.getCategoryName());
        node.put("categoryCode", category.getCategoryCode());
        node.put("level", category.getLevel());
        node.put("skillType", category.getSkillType());
        node.put("sortOrder", category.getSortOrder());
        List<QuestionCategory> children = childrenMap.get(category.getId());
        if (children != null && !children.isEmpty()) {
            List<Map<String, Object>> childNodes = children.stream().map(c -> this.buildCategoryNode(c, childrenMap, countMap)).collect(Collectors.toList());
            node.put("children", childNodes);
            long childCount = childNodes.stream().mapToLong(n -> ((Number)n.getOrDefault("count", 0L)).longValue()).sum();
            node.put("count", countMap.getOrDefault(category.getId(), 0L) + childCount);
        } else {
            node.put("children", new ArrayList<>());
            node.put("count", countMap.getOrDefault(category.getId(), 0L));
        }
        return node;
    }

    public QuestionCategory getCategoryDetail(Long categoryId) {
        QuestionCategory category = (QuestionCategory)this.questionCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("\u5206\u7c7b\u4e0d\u5b58\u5728");
        }
        return category;
    }

    public void createCategory(QuestionCategory category) {
        if (category.getCategoryCode() != null && !category.getCategoryCode().isEmpty()) {
            long count = this.questionCategoryMapper.selectCount(new LambdaQueryWrapper<QuestionCategory>().eq(QuestionCategory::getCategoryCode,category.getCategoryCode()));
            if (count > 0L) {
                throw new BusinessException("\u5206\u7c7b\u7f16\u7801\u5df2\u5b58\u5728");
            }
        } else {
            category.setCategoryCode("CAT_" + System.currentTimeMillis());
        }
        if (category.getParentId() == null) {
            category.setParentId(Long.valueOf(0L));
        }
        category.setStatus(1);
        this.questionCategoryMapper.insert(category);
    }

    public void updateCategory(Long categoryId, QuestionCategory category) {
        QuestionCategory existing = (QuestionCategory)this.questionCategoryMapper.selectById(categoryId);
        if (existing == null) {
            throw new BusinessException("\u5206\u7c7b\u4e0d\u5b58\u5728");
        }
        if (category.getCategoryCode() != null && !category.getCategoryCode().equals(existing.getCategoryCode())) {
            long count = this.questionCategoryMapper.selectCount(new LambdaQueryWrapper<QuestionCategory>().eq(QuestionCategory::getCategoryCode,category.getCategoryCode()));
            if (count > 0L) {
            throw new BusinessException("\u5206\u7c7b\u7f16\u7801\u5df2\u5b58\u5728");
            }
        }
        if (category.getCategoryName() != null) {
            existing.setCategoryName(category.getCategoryName());
        }
        if (category.getCategoryCode() != null) {
            existing.setCategoryCode(category.getCategoryCode());
        }
        if (category.getLevel() != null) {
            existing.setLevel(category.getLevel());
        }
        if (category.getSkillType() != null) {
            existing.setSkillType(category.getSkillType());
        }
        if (category.getSortOrder() != null) {
            existing.setSortOrder(category.getSortOrder());
        }
        if (category.getParentId() != null) {
            existing.setParentId(category.getParentId());
        }
        this.questionCategoryMapper.updateById(existing);
    }

    public void deleteCategory(Long categoryId) {
        long childCount = this.questionCategoryMapper.selectCount(new LambdaQueryWrapper<QuestionCategory>().eq(QuestionCategory::getParentId,categoryId));
        if (childCount > 0L) {
            throw new BusinessException("\u5b58\u5728\u5b50\u5206\u7c7b\uff0c\u65e0\u6cd5\u5220\u9664");
        }
        long questionCount = this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getCategoryId,categoryId).eq(Question::getStatus,1));
        if (questionCount > 0L) {
            throw new BusinessException("\u8be5\u5206\u7c7b\u4e0b\u5b58\u5728 " + questionCount + " \u9053\u9898\u76ee\uff0c\u65e0\u6cd5\u5220\u9664");
        }
        this.questionCategoryMapper.deleteById(categoryId);
    }

    public QuestionCategoryService(QuestionCategoryMapper questionCategoryMapper, QuestionMapper questionMapper) {
        this.questionCategoryMapper = questionCategoryMapper;
        this.questionMapper = questionMapper;
    }
}

