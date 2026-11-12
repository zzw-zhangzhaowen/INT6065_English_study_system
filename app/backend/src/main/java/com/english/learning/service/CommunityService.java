package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.CommunityPost;
import com.english.learning.entity.PostCollection;
import com.english.learning.entity.PostLike;
import com.english.learning.entity.User;
import com.english.learning.mapper.CommunityPostMapper;
import com.english.learning.mapper.PostCollectionMapper;
import com.english.learning.mapper.PostLikeMapper;
import com.english.learning.mapper.UserMapper;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityService {
    private static final Logger log = LoggerFactory.getLogger(CommunityService.class);
    private final CommunityPostMapper communityPostMapper;
    private final PostLikeMapper postLikeMapper;
    private final PostCollectionMapper postCollectionMapper;
    private final UserMapper userMapper;

    public Page<Map<String, Object>> getPostPage(int pageNum, int pageSize, Long categoryId, String postType, Long userId) {
        Page<CommunityPost> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<CommunityPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, CommunityPost::getCategoryId,categoryId).eq(postType != null, CommunityPost::getPostType,postType).eq(CommunityPost::getStatus,1).orderByDesc(CommunityPost::getIsTopped).orderByDesc(CommunityPost::getCreateTime);
        Page<CommunityPost> postPage = (Page<CommunityPost>)this.communityPostMapper.selectPage((IPage<CommunityPost>)page, wrapper);
        List<Long> userIds = postPage.getRecords().stream().map(CommunityPost::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = this.userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, userIds));
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        }
        List<Long> postIds = postPage.getRecords().stream().map(CommunityPost::getId).collect(Collectors.toList());
        Set<Long> likedPostIds = new HashSet<>();
        if (userId != null && !postIds.isEmpty()) {
            List<PostLike> likes = this.postLikeMapper.selectList(new LambdaQueryWrapper<PostLike>().eq(PostLike::getUserId, userId).in(PostLike::getPostId, postIds));
            likedPostIds = likes.stream().map(PostLike::getPostId).collect(Collectors.toSet());
        }
        Page<Map<String, Object>> resultPage = new Page<>((long)pageNum, (long)pageSize, postPage.getTotal());
        Map<Long, User> finalUserMap = userMap;
        Set<Long> finalLikedPostIds = likedPostIds;
        List<Map<String, Object>> records = postPage.getRecords().stream().map(post -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", post.getId());
            item.put("userId", post.getUserId());
            item.put("categoryId", post.getCategoryId());
            item.put("title", post.getTitle());
            item.put("content", post.getContent());
            item.put("postType", post.getPostType());
            item.put("isTopped", post.getIsTopped());
            item.put("isFeatured", post.getIsFeatured());
            item.put("viewCount", post.getViewCount());
            item.put("likeCount", post.getLikeCount());
            item.put("replyCount", post.getReplyCount());
            item.put("collectCount", post.getCollectCount());
            item.put("lastReplyTime", post.getLastReplyTime());
            item.put("createTime", post.getCreateTime());
            item.put("isLiked", finalLikedPostIds.contains(post.getId()));
            User user = (User)finalUserMap.get(post.getUserId());
            if (user != null) {
                item.put("username", user.getUsername());
                item.put("avatar", user.getAvatar());
            }
            return item;
        }).collect(Collectors.toList());
        resultPage.setRecords(records);
        return resultPage;
    }

    @Transactional
    public Map<String, Object> getPostDetail(Long postId) {
        CommunityPost post = (CommunityPost)this.communityPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("\u5e16\u5b50\u4e0d\u5b58\u5728");
        }
        this.communityPostMapper.incrementViewCount(postId);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("id", post.getId());
        result.put("userId", post.getUserId());
        result.put("categoryId", post.getCategoryId());
        result.put("title", post.getTitle());
        result.put("content", post.getContent());
        result.put("postType", post.getPostType());
        result.put("isTopped", post.getIsTopped());
        result.put("isFeatured", post.getIsFeatured());
        result.put("viewCount", post.getViewCount());
        result.put("likeCount", post.getLikeCount());
        result.put("replyCount", post.getReplyCount());
        result.put("collectCount", post.getCollectCount());
        result.put("lastReplyTime", post.getLastReplyTime());
        result.put("createTime", post.getCreateTime());
        User user = (User)this.userMapper.selectById(post.getUserId());
        if (user != null) {
            result.put("username", user.getUsername());
            result.put("avatar", user.getAvatar());
        }
        return result;
    }

    @Transactional(rollbackFor={Exception.class})
    public void createPost(Long userId, Long categoryId, String title, String content, String postType) {
        CommunityPost post = new CommunityPost();
        post.setUserId(userId);
        post.setCategoryId(categoryId);
        post.setTitle(title);
        post.setContent(content);
        post.setPostType(postType != null ? postType : "DISCUSSION");
        post.setIsTopped(0);
        post.setIsFeatured(0);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setReplyCount(0);
        post.setCollectCount(0);
        post.setStatus(1);
        this.communityPostMapper.insert(post);
    }

    public void updatePost(Long postId, Long userId, String title, String content) {
        CommunityPost post = (CommunityPost)this.communityPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("\u5e16\u5b50\u4e0d\u5b58\u5728");
        }
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("\u65e0\u6743\u4fee\u6539\u6b64\u5e16\u5b50");
        }
        if (title != null) {
            post.setTitle(title);
        }
        if (content != null) {
            post.setContent(content);
        }
        this.communityPostMapper.updateById(post);
    }

    public void deletePost(Long postId, Long userId) {
        CommunityPost post = (CommunityPost)this.communityPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("\u5e16\u5b50\u4e0d\u5b58\u5728");
        }
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("\u65e0\u6743\u5220\u9664\u6b64\u5e16\u5b50");
        }
        this.communityPostMapper.deleteById(postId);
    }

    @Transactional
    public boolean toggleLikePost(Long postId, Long userId) {
        CommunityPost post = (CommunityPost)this.communityPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("\u5e16\u5b50\u4e0d\u5b58\u5728");
        }
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId,postId).eq(PostLike::getUserId, userId);
        PostLike existingLike = this.postLikeMapper.selectOne(wrapper);
        if (existingLike != null) {
            this.postLikeMapper.deleteById(existingLike.getId());
            this.communityPostMapper.incrementLikeCount(postId, -1);
            return false;
        }
        PostLike like = new PostLike();
        like.setPostId(postId);
        like.setUserId(userId);
        this.postLikeMapper.insert(like);
        this.communityPostMapper.incrementLikeCount(postId, 1);
        return true;
    }

    @Transactional
    public boolean toggleCollectPost(Long postId, Long userId) {
        CommunityPost post = (CommunityPost)this.communityPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("\u5e16\u5b50\u4e0d\u5b58\u5728");
        }
        LambdaQueryWrapper<PostCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostCollection::getPostId,postId).eq(PostCollection::getUserId, userId);
        PostCollection existingCollection = this.postCollectionMapper.selectOne(wrapper);
        if (existingCollection != null) {
            this.postCollectionMapper.deleteById(existingCollection.getId());
            this.communityPostMapper.incrementCollectCount(postId, -1);
            return false;
        }
        PostCollection collection = new PostCollection();
        collection.setPostId(postId);
        collection.setUserId(userId);
        this.postCollectionMapper.insert(collection);
        this.communityPostMapper.incrementCollectCount(postId, 1);
        return true;
    }

    public boolean isLiked(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId,postId).eq(PostLike::getUserId, userId);
        return this.postLikeMapper.selectCount(wrapper) > 0L;
    }

    public boolean isCollected(Long postId, Long userId) {
        LambdaQueryWrapper<PostCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostCollection::getPostId,postId).eq(PostCollection::getUserId, userId);
        return this.postCollectionMapper.selectCount(wrapper) > 0L;
    }

    public List<Map<String, Object>> getHotTopics(int limit) {
        LambdaQueryWrapper<CommunityPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityPost::getStatus,1).orderByDesc(CommunityPost::getViewCount).last("LIMIT " + limit);
        List<CommunityPost> posts = this.communityPostMapper.selectList(wrapper);
        return posts.stream().map(post -> {
            HashMap<String, Object> topic = new HashMap<String, Object>();
            topic.put("id", post.getId());
            String title = post.getTitle() != null ? post.getTitle() : "\u65e0\u6807\u9898";
            topic.put("name", title.length() > 20 ? title.substring(0, 20) + "..." : title);
            topic.put("count", post.getReplyCount());
            return topic;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getActiveUsers(int limit) {
        LambdaQueryWrapper<CommunityPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityPost::getStatus,1).select(CommunityPost::getUserId).groupBy(CommunityPost::getUserId).last("ORDER BY COUNT(*) DESC LIMIT " + limit);
        List<CommunityPost> posts = this.communityPostMapper.selectList(wrapper);
        List<Long> userIds = posts.stream().map(CommunityPost::getUserId).distinct().collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return List.of();
        }
        List<User> users = this.userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, userIds));
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        LambdaQueryWrapper<CommunityPost> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(CommunityPost::getStatus,1).in(CommunityPost::getUserId, userIds).select(CommunityPost::getUserId).groupBy(CommunityPost::getUserId);
        List<Map<String, Object>> countResults = this.communityPostMapper.selectMaps(countWrapper);
        Map<Long, Long> postCountMap = new HashMap<>();
        for (Map<String, Object> row : countResults) {
            Object uid2 = row.get("author_id");
            if (uid2 == null) continue;
            postCountMap.put(Long.valueOf(uid2.toString()), (Long)row.getOrDefault("__count", 0L));
        }
        if (postCountMap.isEmpty()) {
            LambdaQueryWrapper<CommunityPost> batchCountWrapper = new LambdaQueryWrapper<>();
            batchCountWrapper.eq(CommunityPost::getStatus,1).in(CommunityPost::getUserId, userIds);
            List<CommunityPost> allPosts = this.communityPostMapper.selectList(batchCountWrapper);
            postCountMap = allPosts.stream().collect(Collectors.groupingBy(CommunityPost::getUserId, Collectors.counting()));
        }
        Map<Long, Long> finalPostCountMap = postCountMap;
        return userIds.stream().map(uid -> {
            User user = userMap.get(uid);
            if (user == null) {
                return null;
            }
            HashMap<String, Object> item = new HashMap<>();
            item.put("id", user.getId());
            item.put("username", user.getUsername());
            item.put("nickname", user.getRealName() != null ? user.getRealName() : user.getUsername());
            item.put("avatar", user.getAvatar());
            item.put("postCount", finalPostCountMap.getOrDefault(uid, 0L));
            return item;
        }).filter(item -> item != null).collect(Collectors.toList());
    }

    public void topPost(Long postId, boolean isTop) {
        CommunityPost post = (CommunityPost)this.communityPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("\u5e16\u5b50\u4e0d\u5b58\u5728");
        }
        post.setIsTopped(Integer.valueOf(isTop ? 1 : 0));
        this.communityPostMapper.updateById(post);
    }

    public void featurePost(Long postId, boolean isFeatured) {
        CommunityPost post = (CommunityPost)this.communityPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("\u5e16\u5b50\u4e0d\u5b58\u5728");
        }
        post.setIsFeatured(Integer.valueOf(isFeatured ? 1 : 0));
        this.communityPostMapper.updateById(post);
    }

    public CommunityService(CommunityPostMapper communityPostMapper, PostLikeMapper postLikeMapper, PostCollectionMapper postCollectionMapper, UserMapper userMapper) {
        this.communityPostMapper = communityPostMapper;
        this.postLikeMapper = postLikeMapper;
        this.postCollectionMapper = postCollectionMapper;
        this.userMapper = userMapper;
    }
}

