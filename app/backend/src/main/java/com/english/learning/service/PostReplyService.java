package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.CommunityPost;
import com.english.learning.entity.PostReply;
import com.english.learning.entity.ReplyLike;
import com.english.learning.entity.User;
import com.english.learning.mapper.CommunityPostMapper;
import com.english.learning.mapper.PostReplyMapper;
import com.english.learning.mapper.ReplyLikeMapper;
import com.english.learning.mapper.UserMapper;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostReplyService {
    private static final Logger log = LoggerFactory.getLogger(PostReplyService.class);
    private final PostReplyMapper postReplyMapper;
    private final CommunityPostMapper communityPostMapper;
    private final UserMapper userMapper;
    private final ReplyLikeMapper replyLikeMapper;

    public Page<Map<String, Object>> getReplyPage(int pageNum, int pageSize, Long postId) {
        Page<PostReply> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<PostReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostReply::getPostId,postId).eq(PostReply::getParentId,0).orderByDesc(PostReply::getIsBestAnswer).orderByAsc(PostReply::getCreateTime);
        Page<PostReply> replyPage = (Page<PostReply>)this.postReplyMapper.selectPage((IPage<PostReply>)page, wrapper);
        List<Long> userIds = replyPage.getRecords().stream().map(PostReply::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = this.userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, userIds));
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        }
        Page<Map<String, Object>> resultPage = new Page<>((long)pageNum, (long)pageSize, replyPage.getTotal());
        Map<Long, User> finalUserMap = userMap;
        List<Map<String, Object>> records = replyPage.getRecords().stream().map(reply -> {
            HashMap<String, Object> item = new HashMap<>();
            item.put("id", reply.getId());
            item.put("postId", reply.getPostId());
            item.put("userId", reply.getUserId());
            item.put("content", reply.getContent());
            item.put("likeCount", reply.getLikeCount());
            item.put("isBestAnswer", reply.getIsBestAnswer());
            item.put("createTime", reply.getCreateTime());
            User user = finalUserMap.get(reply.getUserId());
            if (user != null) {
                item.put("username", user.getUsername());
                item.put("avatar", user.getAvatar());
            }
            List<PostReply> childReplies = this.postReplyMapper.selectList(new LambdaQueryWrapper<PostReply>().eq(PostReply::getParentId,reply.getId()).orderByAsc(PostReply::getCreateTime));
            item.put("childReplies", childReplies);
            return item;
        }).collect(Collectors.toList());
        resultPage.setRecords(records);
        return resultPage;
    }

    @Transactional(rollbackFor={Exception.class})
    public void createReply(Long postId, Long userId, String content, Long parentId) {
        CommunityPost post = (CommunityPost)this.communityPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("\u5e16\u5b50\u4e0d\u5b58\u5728");
        }
        PostReply reply = new PostReply();
        reply.setPostId(postId);
        reply.setUserId(userId);
        reply.setContent(content);
        reply.setParentId(Long.valueOf(parentId != null ? parentId : 0L));
        reply.setLikeCount(0);
        reply.setIsBestAnswer(0);
        this.postReplyMapper.insert(reply);
        this.communityPostMapper.incrementReplyCount(postId, 1);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteReply(Long replyId, Long userId) {
        PostReply reply = (PostReply)this.postReplyMapper.selectById(replyId);
        if (reply == null) {
            throw new BusinessException("\u56de\u590d\u4e0d\u5b58\u5728");
        }
        if (!reply.getUserId().equals(userId)) {
            throw new BusinessException("\u65e0\u6743\u5220\u9664\u6b64\u56de\u590d");
        }
        this.postReplyMapper.deleteById(replyId);
        this.communityPostMapper.incrementReplyCount(reply.getPostId(), -1);
    }

    @Transactional
    public boolean toggleLikeReply(Long replyId, Long userId) {
        PostReply reply = (PostReply)this.postReplyMapper.selectById(replyId);
        if (reply == null) {
            throw new BusinessException("\u56de\u590d\u4e0d\u5b58\u5728");
        }
        LambdaQueryWrapper<ReplyLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReplyLike::getReplyId,replyId).eq(ReplyLike::getUserId, userId);
        ReplyLike existingLike = this.replyLikeMapper.selectOne(wrapper);
        if (existingLike != null) {
            this.replyLikeMapper.deleteById(existingLike.getId());
            this.postReplyMapper.incrementLikeCount(replyId, -1);
            return false;
        }
        ReplyLike like = new ReplyLike();
        like.setReplyId(replyId);
        like.setUserId(userId);
        this.replyLikeMapper.insert(like);
        this.postReplyMapper.incrementLikeCount(replyId, 1);
        return true;
    }

    public void setBestAnswer(Long replyId, Long postOwnerId) {
        PostReply reply = (PostReply)this.postReplyMapper.selectById(replyId);
        if (reply == null) {
            throw new BusinessException("\u56de\u590d\u4e0d\u5b58\u5728");
        }
        CommunityPost post = (CommunityPost)this.communityPostMapper.selectById(reply.getPostId());
        if (post == null) {
            throw new BusinessException("\u5e16\u5b50\u4e0d\u5b58\u5728");
        }
        if (!post.getUserId().equals(postOwnerId)) {
            throw new BusinessException("\u53ea\u6709\u5e16\u5b50\u4f5c\u8005\u53ef\u4ee5\u8bbe\u7f6e\u6700\u4f73\u7b54\u6848");
        }
        this.postReplyMapper.selectList(new LambdaQueryWrapper<PostReply>().eq(PostReply::getPostId,reply.getPostId()).eq(PostReply::getIsBestAnswer,1)).forEach(r -> {
            r.setIsBestAnswer(0);
            this.postReplyMapper.updateById(r);
        });
        reply.setIsBestAnswer(1);
        this.postReplyMapper.updateById(reply);
    }

    public boolean isLiked(Long replyId, Long userId) {
        LambdaQueryWrapper<ReplyLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReplyLike::getReplyId,replyId).eq(ReplyLike::getUserId, userId);
        return this.replyLikeMapper.selectCount(wrapper) > 0L;
    }

    public PostReplyService(PostReplyMapper postReplyMapper, CommunityPostMapper communityPostMapper, UserMapper userMapper, ReplyLikeMapper replyLikeMapper) {
        this.postReplyMapper = postReplyMapper;
        this.communityPostMapper = communityPostMapper;
        this.userMapper = userMapper;
        this.replyLikeMapper = replyLikeMapper;
    }
}

