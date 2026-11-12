package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.ClassroomPoll;
import com.english.learning.entity.ClassroomPollOption;
import com.english.learning.entity.ClassroomPollVote;
import com.english.learning.mapper.ClassroomPollMapper;
import com.english.learning.mapper.ClassroomPollOptionMapper;
import com.english.learning.mapper.ClassroomPollVoteMapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClassroomPollService {
    private static final Logger log = LoggerFactory.getLogger(ClassroomPollService.class);
    private final ClassroomPollMapper pollMapper;
    private final ClassroomPollOptionMapper optionMapper;
    private final ClassroomPollVoteMapper voteMapper;

    @Transactional(rollbackFor={Exception.class})
    public Map<String, Object> createPoll(Long classroomId, Long creatorId, String title, String pollType, Boolean anonymous, List<String> options) {
        if (options == null || options.size() < 2) {
            throw new BusinessException("\u6295\u7968\u81f3\u5c11\u9700\u89812\u4e2a\u9009\u9879");
        }
        ClassroomPoll poll = new ClassroomPoll();
        poll.setClassroomId(classroomId);
        poll.setCreatorId(creatorId);
        poll.setTitle(title);
        poll.setPollType(pollType != null ? pollType : "SINGLE");
        poll.setStatus("ACTIVE");
        poll.setAnonymous(Boolean.valueOf(anonymous != null && anonymous != false));
        this.pollMapper.insert(poll);
        ArrayList optionList = new ArrayList();
        for (int i = 0; i < options.size(); ++i) {
            ClassroomPollOption option = new ClassroomPollOption();
            option.setPollId(poll.getId());
            option.setContent(options.get(i));
            option.setVoteCount(0);
            option.setSortOrder(Integer.valueOf(i));
            this.optionMapper.insert(option);
            HashMap<String, Object> optMap = new HashMap<String, Object>();
            optMap.put("id", option.getId());
            optMap.put("content", option.getContent());
            optMap.put("voteCount", 0);
            optionList.add(optMap);
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("id", poll.getId());
        result.put("classroomId", classroomId);
        result.put("title", title);
        result.put("pollType", poll.getPollType());
        result.put("status", "ACTIVE");
        result.put("anonymous", poll.getAnonymous());
        result.put("createTime", poll.getCreateTime());
        result.put("options", optionList);
        result.put("totalVotes", 0);
        return result;
    }

    public List<Map<String, Object>> getPolls(Long classroomId) {
        LambdaQueryWrapper<ClassroomPoll> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassroomPoll::getClassroomId, classroomId)
               .orderByDesc(ClassroomPoll::getCreateTime);
        List<ClassroomPoll> polls = this.pollMapper.selectList(wrapper);
        return polls.stream().map(this::buildPollDetail).collect(Collectors.toList());
    }

    public Map<String, Object> getPollDetail(Long classroomId, Long pollId) {
        ClassroomPoll poll = (ClassroomPoll)this.pollMapper.selectById(pollId);
        if (poll == null || !poll.getClassroomId().equals(classroomId)) {
            throw new BusinessException("\u6295\u7968\u4e0d\u5b58\u5728");
        }
        return this.buildPollDetail(poll);
    }

    @Transactional(rollbackFor={Exception.class})
    public Map<String, Object> vote(Long classroomId, Long pollId, Long userId, List<Long> optionIds) {
        ClassroomPoll poll = (ClassroomPoll)this.pollMapper.selectById(pollId);
        if (poll == null || !poll.getClassroomId().equals(classroomId)) {
            throw new BusinessException("\u6295\u7968\u4e0d\u5b58\u5728");
        }
        if (!"ACTIVE".equals(poll.getStatus())) {
            throw new BusinessException("\u6295\u7968\u5df2\u7ed3\u675f");
        }
        Long existingVotes = this.voteMapper.selectCount(new LambdaQueryWrapper<ClassroomPollVote>()
            .eq(ClassroomPollVote::getPollId, pollId)
            .eq(ClassroomPollVote::getUserId, userId));
        if (existingVotes > 0L) {
            throw new BusinessException("\u60a8\u5df2\u7ecf\u6295\u8fc7\u7968\u4e86");
        }
        if ("SINGLE".equals(poll.getPollType()) && optionIds.size() > 1) {
            throw new BusinessException("\u5355\u9009\u6295\u7968\u53ea\u80fd\u9009\u62e9\u4e00\u4e2a\u9009\u9879");
        }
        for (Long optionId : optionIds) {
            ClassroomPollOption option = (ClassroomPollOption)this.optionMapper.selectById(optionId);
            if (option == null || !option.getPollId().equals(pollId)) {
                throw new BusinessException("\u65e0\u6548\u7684\u9009\u9879");
            }
            ClassroomPollVote vote = new ClassroomPollVote();
            vote.setPollId(pollId);
            vote.setOptionId(optionId);
            vote.setUserId(userId);
            this.voteMapper.insert(vote);
            this.optionMapper.incrementVoteCount(optionId);
        }
        return this.buildPollDetail(poll);
    }

    public Map<String, Object> closePoll(Long classroomId, Long pollId, Long userId, boolean isAdmin) {
        ClassroomPoll poll = (ClassroomPoll)this.pollMapper.selectById(pollId);
        if (poll == null || !poll.getClassroomId().equals(classroomId)) {
            throw new BusinessException("\u6295\u7968\u4e0d\u5b58\u5728");
        }
        if (!isAdmin && !Objects.equals(poll.getCreatorId(), userId)) {
            throw new BusinessException("\u65e0\u6743\u5173\u95ed\u6295\u7968");
        }
        poll.setStatus("CLOSED");
        this.pollMapper.updateById(poll);
        return this.buildPollDetail(poll);
    }

    public boolean hasVoted(Long classroomId, Long pollId, Long userId) {
        ClassroomPoll poll = (ClassroomPoll)this.pollMapper.selectById(pollId);
        if (poll == null || !poll.getClassroomId().equals(classroomId)) {
            throw new BusinessException("\u6295\u7968\u4e0d\u5b58\u5728");
        }
        return this.voteMapper.selectCount(new LambdaQueryWrapper<ClassroomPollVote>()
            .eq(ClassroomPollVote::getPollId, pollId)
            .eq(ClassroomPollVote::getUserId, userId)) > 0L;
    }

    private Map<String, Object> buildPollDetail(ClassroomPoll poll) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id", poll.getId());
        map.put("classroomId", poll.getClassroomId());
        map.put("creatorId", poll.getCreatorId());
        map.put("title", poll.getTitle());
        map.put("pollType", poll.getPollType());
        map.put("status", poll.getStatus());
        map.put("anonymous", poll.getAnonymous());
        map.put("createTime", poll.getCreateTime());
        LambdaQueryWrapper<ClassroomPollOption> optWrapper = new LambdaQueryWrapper<>();
        optWrapper.eq(ClassroomPollOption::getPollId, poll.getId())
                  .orderByAsc(ClassroomPollOption::getSortOrder);
        List<ClassroomPollOption> options = this.optionMapper.selectList(optWrapper);
        int totalVotes = 0;
        ArrayList<Map<String, Object>> optionList = new ArrayList<>();
        for (ClassroomPollOption opt : options) {
            HashMap<String, Object> optMap = new HashMap<String, Object>();
            optMap.put("id", opt.getId());
            optMap.put("content", opt.getContent());
            optMap.put("voteCount", opt.getVoteCount());
            optionList.add(optMap);
            totalVotes += opt.getVoteCount().intValue();
        }
        map.put("options", optionList);
        map.put("totalVotes", totalVotes);
        return map;
    }

    public ClassroomPollService(ClassroomPollMapper pollMapper, ClassroomPollOptionMapper optionMapper, ClassroomPollVoteMapper voteMapper) {
        this.pollMapper = pollMapper;
        this.optionMapper = optionMapper;
        this.voteMapper = voteMapper;
    }
}

