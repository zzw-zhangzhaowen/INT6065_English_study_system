package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.ResourceCategory;
import com.english.learning.mapper.ResourceCategoryMapper;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceCategoryService {
    private static final Logger log = LoggerFactory.getLogger(ResourceCategoryService.class);
    private final ResourceCategoryMapper resourceCategoryMapper;

    public List<CategoryTreeNode> getCategoryTree() {
        LambdaQueryWrapper<ResourceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ResourceCategory::getSortOrder).orderByAsc(ResourceCategory::getId);
        List<ResourceCategory> all = this.resourceCategoryMapper.selectList(wrapper);
        return this.buildTree(all, Long.valueOf(0L));
    }

    public List<ResourceCategory> getAllCategories() {
        LambdaQueryWrapper<ResourceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ResourceCategory::getSortOrder);
        return this.resourceCategoryMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor={Exception.class})
    public void createCategory(ResourceCategory category, Long userId) {
        LambdaQueryWrapper<ResourceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResourceCategory::getParentId,(category.getParentId() != null ? category.getParentId() : 0L)).eq(ResourceCategory::getCategoryName,category.getCategoryName());
        if (this.resourceCategoryMapper.selectCount(wrapper) > 0L) {
            throw new BusinessException("\u540c\u7ea7\u4e0b\u5df2\u5b58\u5728\u540c\u540d\u5206\u7c7b");
        }
        if (category.getParentId() == null) {
            category.setParentId(Long.valueOf(0L));
        }
        category.setCreatorId(userId);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        this.resourceCategoryMapper.insert(category);
        log.info("\u7528\u6237{}\u521b\u5efa\u4e86\u8d44\u6e90\u5206\u7c7b: {}",userId,category.getCategoryName());
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateCategory(Long id, ResourceCategory category, Long userId) {
        ResourceCategory existing = this.resourceCategoryMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("\u5206\u7c7b\u4e0d\u5b58\u5728");
        }
        category.setId(id);
        category.setUpdateTime(LocalDateTime.now());
        this.resourceCategoryMapper.updateById(category);
        log.info("\u7528\u6237{}\u66f4\u65b0\u4e86\u8d44\u6e90\u5206\u7c7b: {}",userId,id);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteCategory(Long id, Long userId) {
        ResourceCategory existing = this.resourceCategoryMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("\u5206\u7c7b\u4e0d\u5b58\u5728");
        }
        LambdaQueryWrapper<ResourceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResourceCategory::getParentId,id);
        if (this.resourceCategoryMapper.selectCount(wrapper) > 0L) {
            throw new BusinessException("\u8be5\u5206\u7c7b\u4e0b\u6709\u5b50\u5206\u7c7b\uff0c\u65e0\u6cd5\u5220\u9664");
        }
        this.resourceCategoryMapper.deleteById(id);
        log.info("\u7528\u6237{}\u5220\u9664\u4e86\u8d44\u6e90\u5206\u7c7b: {}",userId,id);
    }

    private List<CategoryTreeNode> buildTree(List<ResourceCategory> all, Long parentId) {
        Map<Long, List<ResourceCategory>> grouped = all.stream().collect(Collectors.groupingBy(ResourceCategory::getParentId));
        return this.buildChildren(grouped, parentId);
    }

    private List<CategoryTreeNode> buildChildren(Map<Long, List<ResourceCategory>> grouped, Long parentId) {
        List<ResourceCategory> children = grouped.getOrDefault(parentId, new ArrayList<>());
        return children.stream().map((ResourceCategory c) -> {
            CategoryTreeNode node = new CategoryTreeNode();
            node.setId(c.getId());
            node.setCategoryName(c.getCategoryName());
            node.setParentId(c.getParentId());
            node.setSortOrder(c.getSortOrder());
            node.setDescription(c.getDescription());
            node.setChildren(this.buildChildren(grouped, c.getId()));
            return node;
        }).collect(Collectors.toList());
    }

    public ResourceCategoryService(ResourceCategoryMapper resourceCategoryMapper) {
        this.resourceCategoryMapper = resourceCategoryMapper;
    }

    @lombok.Data
    public static class CategoryTreeNode {
        private Long id;
        private String categoryName;
        private Long parentId;
        private Integer sortOrder;
        private String description;
        private List<CategoryTreeNode> children;
    }
}

