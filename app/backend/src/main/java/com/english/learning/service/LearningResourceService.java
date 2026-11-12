package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.LearningResource;
import com.english.learning.entity.ResourceCollection;
import com.english.learning.entity.ResourceLike;
import com.english.learning.mapper.LearningResourceMapper;
import com.english.learning.mapper.ResourceCollectionMapper;
import com.english.learning.mapper.ResourceLikeMapper;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LearningResourceService {
    private static final Logger log = LoggerFactory.getLogger(LearningResourceService.class);
    private final LearningResourceMapper learningResourceMapper;
    private final ResourceCollectionMapper resourceCollectionMapper;
    private final ResourceLikeMapper resourceLikeMapper;

    public Page<LearningResource> getResourcePage(int pageNum, int pageSize, String resourceType, String level, String keyword, Long categoryId, Integer status, boolean includeOffline) {
        Page<LearningResource> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<LearningResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText((String)resourceType), LearningResource::getResourceType,resourceType).eq(StringUtils.hasText((String)level), LearningResource::getLevel,level).eq(categoryId != null, LearningResource::getCategoryId,categoryId).and(StringUtils.hasText((String)keyword), w -> w.like(LearningResource::getResourceName, keyword).or().like(LearningResource::getDescription, keyword).or().like(LearningResource::getTags, keyword)).eq(status != null, LearningResource::getStatus, status).eq(status == null && !includeOffline, LearningResource::getStatus,1).orderByDesc(LearningResource::getCreateTime);
        return (Page<LearningResource>)this.learningResourceMapper.selectPage(page, wrapper);
    }

    public LearningResource getResourceDetail(Long id) {
        LearningResource resource = (LearningResource)this.learningResourceMapper.selectById(id);
        if (resource == null) {
            throw new BusinessException("\u8d44\u6e90\u4e0d\u5b58\u5728");
        }
        LambdaUpdateWrapper<LearningResource> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LearningResource::getId,id).setSql("view_count = view_count + 1", new Object[0]);
        this.learningResourceMapper.update(null, updateWrapper);
        resource.setViewCount(Long.valueOf(resource.getViewCount() + 1L));
        return resource;
    }

    @Transactional(rollbackFor={Exception.class})
    public void createResource(LearningResource resource, Long userId) {
        resource.setCreatorId(userId);
        resource.setViewCount(Long.valueOf(0L));
        resource.setLikeCount(Long.valueOf(0L));
        resource.setDownloadCount(Long.valueOf(0L));
        resource.setStatus(1);
        resource.setCreateTime(LocalDateTime.now());
        resource.setUpdateTime(LocalDateTime.now());
        int result = this.learningResourceMapper.insert(resource);
        if (result <= 0) {
            throw new BusinessException("\u521b\u5efa\u8d44\u6e90\u5931\u8d25");
        }
        log.info("\u7528\u6237{}\u521b\u5efa\u4e86\u5b66\u4e60\u8d44\u6e90: {}",userId,resource.getResourceName());
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateResource(Long id, LearningResource resource, Long userId) {
        LearningResource existing = (LearningResource)this.learningResourceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("\u8d44\u6e90\u4e0d\u5b58\u5728");
        }
        if (!existing.getCreatorId().equals(userId)) {
            throw new BusinessException("\u65e0\u6743\u9650\u4fee\u6539\u6b64\u8d44\u6e90");
        }
        resource.setId(id);
        resource.setUpdateTime(LocalDateTime.now());
        int result = this.learningResourceMapper.updateById(resource);
        if (result <= 0) {
            throw new BusinessException("\u66f4\u65b0\u8d44\u6e90\u5931\u8d25");
        }
        log.info("\u7528\u6237{}\u66f4\u65b0\u4e86\u5b66\u4e60\u8d44\u6e90: {}",userId,id);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteResource(Long id, Long userId) {
        LearningResource existing = (LearningResource)this.learningResourceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("\u8d44\u6e90\u4e0d\u5b58\u5728");
        }
        if (!existing.getCreatorId().equals(userId)) {
            throw new BusinessException("\u65e0\u6743\u9650\u5220\u9664\u6b64\u8d44\u6e90");
        }
        int result = this.learningResourceMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("\u5220\u9664\u8d44\u6e90\u5931\u8d25");
        }
        log.info("\u7528\u6237{}\u5220\u9664\u4e86\u5b66\u4e60\u8d44\u6e90: {}",userId,id);
    }

    @Transactional(rollbackFor={Exception.class})
    public void batchDeleteResource(List<Long> ids, Long userId) {
        for (Long id : ids) {
            this.deleteResource(id, userId);
        }
    }

    @Transactional(rollbackFor={Exception.class})
    public boolean toggleLike(Long id, Long userId) {
        LearningResource resource = (LearningResource)this.learningResourceMapper.selectById(id);
        if (resource == null) {
            throw new BusinessException("\u8d44\u6e90\u4e0d\u5b58\u5728");
        }
        LambdaQueryWrapper<ResourceLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResourceLike::getUserId, userId).eq(ResourceLike::getResourceId,id);
        ResourceLike existing = (ResourceLike)this.resourceLikeMapper.selectOne(wrapper);
        LambdaUpdateWrapper<LearningResource> countUpdate = new LambdaUpdateWrapper<>();
        countUpdate.eq(LearningResource::getId,id);
        if (existing != null) {
            this.resourceLikeMapper.deleteById(existing.getId());
            countUpdate.setSql("like_count = GREATEST(like_count - 1, 0)", new Object[0]);
            this.learningResourceMapper.update(null, countUpdate);
            log.info("\u7528\u6237{}\u53d6\u6d88\u70b9\u8d5e\u8d44\u6e90: {}",userId,id);
            return false;
        }
        ResourceLike like = new ResourceLike();
        like.setUserId(userId);
        like.setResourceId(id);
        like.setCreateTime(LocalDateTime.now());
        this.resourceLikeMapper.insert(like);
        countUpdate.setSql("like_count = like_count + 1", new Object[0]);
        this.learningResourceMapper.update(null, countUpdate);
        log.info("\u7528\u6237{}\u70b9\u8d5e\u8d44\u6e90: {}",userId,id);
        return true;
    }

    public Page<LearningResource> getPopularResources(int pageNum, int pageSize) {
        Page<LearningResource> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<LearningResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningResource::getStatus,1).orderByDesc(LearningResource::getViewCount).orderByDesc(LearningResource::getLikeCount);
        return (Page<LearningResource>)this.learningResourceMapper.selectPage(page, wrapper);
    }

    @Transactional(rollbackFor={Exception.class})
    public void collectResource(Long userId, Long resourceId) {
        LearningResource resource = (LearningResource)this.learningResourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("\u8d44\u6e90\u4e0d\u5b58\u5728");
        }
        LambdaQueryWrapper<ResourceCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResourceCollection::getUserId, userId).eq(ResourceCollection::getResourceId,resourceId);
        ResourceCollection existing = (ResourceCollection)this.resourceCollectionMapper.selectOne(wrapper);
        if (existing != null) {
            throw new BusinessException("\u5df2\u7ecf\u6536\u85cf\u8fc7\u8be5\u8d44\u6e90");
        }
        ResourceCollection collection = new ResourceCollection();
        collection.setUserId(userId);
        collection.setResourceId(resourceId);
        collection.setCreateTime(LocalDateTime.now());
        this.resourceCollectionMapper.insert(collection);
        log.info("\u7528\u6237{}\u6536\u85cf\u4e86\u8d44\u6e90: {}",userId,resourceId);
    }

    @Transactional(rollbackFor={Exception.class})
    public void uncollectResource(Long userId, Long resourceId) {
        LambdaQueryWrapper<ResourceCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResourceCollection::getUserId, userId).eq(ResourceCollection::getResourceId,resourceId);
        int result = this.resourceCollectionMapper.delete(wrapper);
        if (result <= 0) {
            throw new BusinessException("\u672a\u6536\u85cf\u8be5\u8d44\u6e90");
        }
        log.info("\u7528\u6237{}\u53d6\u6d88\u6536\u85cf\u8d44\u6e90: {}",userId,resourceId);
    }

    public Page<LearningResource> getCollectedResources(Long userId, int pageNum, int pageSize) {
        Page<ResourceCollection> collectionPage = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<ResourceCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResourceCollection::getUserId, userId).orderByDesc(ResourceCollection::getCreateTime);
        this.resourceCollectionMapper.selectPage(collectionPage, wrapper);
        List<ResourceCollection> collections = collectionPage.getRecords();
        Page<LearningResource> resultPage = new Page<>((long)pageNum, (long)pageSize);
        resultPage.setTotal(collectionPage.getTotal());
        if (collections.isEmpty()) {
            return resultPage;
        }
        List<Long> resourceIds = collections.stream().map(ResourceCollection::getResourceId).collect(Collectors.toList());
        LambdaQueryWrapper<LearningResource> resourceWrapper = new LambdaQueryWrapper<>();
        resourceWrapper.in(LearningResource::getId, resourceIds).eq(LearningResource::getStatus,1);
        List<LearningResource> resources = this.learningResourceMapper.selectList(resourceWrapper);
        Map<Long, LearningResource> resourceMap = resources.stream().collect(Collectors.toMap(LearningResource::getId, r -> r));
        List<LearningResource> ordered = resourceIds.stream().map(resourceMap::get).filter(Objects::nonNull).collect(Collectors.toList());
        resultPage.setRecords(ordered);
        return resultPage;
    }

    public boolean isCollected(Long userId, Long resourceId) {
        LambdaQueryWrapper<ResourceCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResourceCollection::getUserId, userId).eq(ResourceCollection::getResourceId,resourceId);
        return this.resourceCollectionMapper.selectCount(wrapper) > 0L;
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateStatus(Long id, Integer status, Long userId) {
        LearningResource existing = (LearningResource)this.learningResourceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("\u8d44\u6e90\u4e0d\u5b58\u5728");
        }
        if (!existing.getCreatorId().equals(userId)) {
            throw new BusinessException("\u65e0\u6743\u9650\u64cd\u4f5c\u6b64\u8d44\u6e90");
        }
        LambdaUpdateWrapper<LearningResource> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LearningResource::getId,id).set(LearningResource::getStatus, status).set(LearningResource::getUpdateTime,LocalDateTime.now());
        this.learningResourceMapper.update(null, updateWrapper);
        log.info("\u7528\u6237{}\u66f4\u65b0\u8d44\u6e90{}\u72b6\u6001\u4e3a: {}", new Object[]{userId, id, status});
    }

    public LearningResource downloadResource(Long id) {
        LearningResource resource = (LearningResource)this.learningResourceMapper.selectById(id);
        if (resource == null) {
            throw new BusinessException("\u8d44\u6e90\u4e0d\u5b58\u5728");
        }
        LambdaUpdateWrapper<LearningResource> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LearningResource::getId,id).setSql("download_count = download_count + 1", new Object[0]);
        this.learningResourceMapper.update(null, updateWrapper);
        return resource;
    }

    public LearningResourceService(LearningResourceMapper learningResourceMapper, ResourceCollectionMapper resourceCollectionMapper, ResourceLikeMapper resourceLikeMapper) {
        this.learningResourceMapper = learningResourceMapper;
        this.resourceCollectionMapper = resourceCollectionMapper;
        this.resourceLikeMapper = resourceLikeMapper;
    }
}

