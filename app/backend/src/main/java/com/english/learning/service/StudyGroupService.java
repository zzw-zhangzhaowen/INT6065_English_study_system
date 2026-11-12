package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.entity.GroupMember;
import com.english.learning.entity.StudyGroup;
import com.english.learning.entity.User;
import com.english.learning.mapper.GroupMemberMapper;
import com.english.learning.mapper.StudyGroupMapper;
import com.english.learning.mapper.UserMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyGroupService {
    private final StudyGroupMapper studyGroupMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final UserMapper userMapper;

    @Transactional(rollbackFor={Exception.class})
    public Long createGroup(Long leaderId, String groupName, String groupIntro, String groupAvatar, Integer maxMembers) {
        StudyGroup group = new StudyGroup();
        group.setGroupName(groupName);
        group.setGroupIntro(groupIntro);
        group.setGroupAvatar(groupAvatar);
        group.setLeaderId(leaderId);
        group.setMaxMembers(Integer.valueOf(maxMembers != null ? maxMembers : 50));
        group.setCurrentMembers(1);
        group.setActivityLevel("NORMAL");
        group.setStatus(1);
        this.studyGroupMapper.insert(group);
        GroupMember member = new GroupMember();
        member.setGroupId(group.getId());
        member.setUserId(leaderId);
        member.setMemberRole("LEADER");
        member.setJoinTime(LocalDateTime.now());
        this.groupMemberMapper.insert(member);
        return group.getId();
    }

    public void updateGroup(Long groupId, Long userId, String groupName, String groupIntro, String groupAvatar, Integer maxMembers) {
        StudyGroup group = (StudyGroup)this.studyGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException("\u5c0f\u7ec4\u4e0d\u5b58\u5728");
        }
        if (!group.getLeaderId().equals(userId)) {
            throw new BusinessException("\u53ea\u6709\u7ec4\u957f\u53ef\u4ee5\u4fee\u6539\u5c0f\u7ec4\u4fe1\u606f");
        }
        if (groupName != null) {
            group.setGroupName(groupName);
        }
        if (groupIntro != null) {
            group.setGroupIntro(groupIntro);
        }
        if (groupAvatar != null) {
            group.setGroupAvatar(groupAvatar);
        }
        if (maxMembers != null) {
            if (maxMembers < group.getCurrentMembers()) {
                throw new BusinessException("\u6700\u5927\u4eba\u6570\u4e0d\u80fd\u5c0f\u4e8e\u5f53\u524d\u6210\u5458\u6570");
            }
            group.setMaxMembers(maxMembers);
        }
        this.studyGroupMapper.updateById(group);
    }

    @Transactional(rollbackFor={Exception.class})
    public void dissolveGroup(Long groupId, Long userId) {
        StudyGroup group = (StudyGroup)this.studyGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException("\u5c0f\u7ec4\u4e0d\u5b58\u5728");
        }
        if (!group.getLeaderId().equals(userId)) {
            throw new BusinessException("\u53ea\u6709\u7ec4\u957f\u53ef\u4ee5\u89e3\u6563\u5c0f\u7ec4");
        }
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<GroupMember>();
        wrapper.eq(GroupMember::getGroupId,groupId);
        this.groupMemberMapper.delete(wrapper);
        this.studyGroupMapper.deleteById(groupId);
    }

    public Page<Map<String, Object>> getGroupPage(int pageNum, int pageSize, String keyword) {
        Page<StudyGroup> page = new Page<StudyGroup>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<StudyGroup> wrapper = new LambdaQueryWrapper<StudyGroup>();
        wrapper.eq(StudyGroup::getStatus,1).like(keyword != null, StudyGroup::getGroupName, keyword).orderByDesc(StudyGroup::getCurrentMembers).orderByDesc(StudyGroup::getCreateTime);
        Page<StudyGroup> groupPage = this.studyGroupMapper.selectPage(page, wrapper);
        List<Long> leaderIds = groupPage.getRecords().stream().map(g -> g.getLeaderId()).distinct().collect(Collectors.toList());
        Map<Long, User> leaderMap = new HashMap<Long, User>();
        if (!leaderIds.isEmpty()) {
            List<User> leaders = this.userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, leaderIds));
            leaderMap = leaders.stream().collect(Collectors.toMap(User::getId, u -> u, (k1, k2) -> k1));
        }
        Page<Map<String, Object>> resultPage = new Page<>((long)pageNum, (long)pageSize, groupPage.getTotal());
        Map<Long, User> finalLeaderMap = leaderMap;
        List<Map<String, Object>> records = groupPage.getRecords().stream().map(group -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", group.getId());
            item.put("groupName", group.getGroupName());
            item.put("groupAvatar", group.getGroupAvatar());
            item.put("groupIntro", group.getGroupIntro());
            item.put("leaderId", group.getLeaderId());
            item.put("maxMembers", group.getMaxMembers());
            item.put("currentMembers", group.getCurrentMembers());
            item.put("activityLevel", group.getActivityLevel());
            item.put("createTime", group.getCreateTime());
            User leader = (User)finalLeaderMap.get(group.getLeaderId());
            if (leader != null) {
                item.put("leaderName", leader.getRealName() != null ? leader.getRealName() : leader.getUsername());
                item.put("leaderAvatar", leader.getAvatar());
            }
            return item;
        }).collect(Collectors.toList());
        resultPage.setRecords(records);
        return resultPage;
    }

    public Map<String, Object> getGroupDetail(Long groupId) {
        StudyGroup group = (StudyGroup)this.studyGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException("\u5c0f\u7ec4\u4e0d\u5b58\u5728");
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("group", group);
        User leader = (User)this.userMapper.selectById(group.getLeaderId());
        if (leader != null) {
            HashMap<String, Object> leaderInfo = new HashMap<String, Object>();
            leaderInfo.put("id", leader.getId());
            leaderInfo.put("username", leader.getUsername());
            leaderInfo.put("realName", leader.getRealName());
            leaderInfo.put("avatar", leader.getAvatar());
            result.put("leader", leaderInfo);
        }
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<GroupMember>();
        wrapper.eq(GroupMember::getGroupId,groupId).orderByAsc(GroupMember::getJoinTime);
        List<GroupMember> members = this.groupMemberMapper.selectList(wrapper);
        List<Long> userIds = members.stream().map(m -> m.getUserId()).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = new HashMap<Long, User>();
        if (!userIds.isEmpty()) {
            List<User> users = this.userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, userIds));
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u, (k1, k2) -> k1));
        }
        Map<Long, User> finalUserMap = userMap;
        List<Map<String, Object>> memberList = members.stream().map(m -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", m.getId());
            item.put("userId", m.getUserId());
            item.put("memberRole", m.getMemberRole());
            item.put("joinTime", m.getJoinTime());
            User user = (User)finalUserMap.get(m.getUserId());
            if (user != null) {
                item.put("username", user.getUsername());
                item.put("realName", user.getRealName());
                item.put("avatar", user.getAvatar());
            }
            return item;
        }).collect(Collectors.toList());
        result.put("members", memberList);
        return result;
    }

    @Transactional(rollbackFor={Exception.class})
    public void joinGroup(Long groupId, Long userId) {
        StudyGroup group = (StudyGroup)this.studyGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException("\u5c0f\u7ec4\u4e0d\u5b58\u5728");
        }
        if (group.getStatus() != 1) {
            throw new BusinessException("\u5c0f\u7ec4\u5df2\u5173\u95ed");
        }
        if (group.getCurrentMembers() >= group.getMaxMembers()) {
            throw new BusinessException("\u5c0f\u7ec4\u4eba\u6570\u5df2\u6ee1");
        }
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<GroupMember>();
        wrapper.eq(GroupMember::getGroupId,groupId).eq(GroupMember::getUserId, userId);
        if (this.groupMemberMapper.selectCount(wrapper) > 0L) {
            throw new BusinessException("\u60a8\u5df2\u662f\u5c0f\u7ec4\u6210\u5458");
        }
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setMemberRole("MEMBER");
        member.setJoinTime(LocalDateTime.now());
        this.groupMemberMapper.insert(member);
        group.setCurrentMembers(Integer.valueOf(group.getCurrentMembers() + 1));
        this.studyGroupMapper.updateById(group);
    }

    @Transactional(rollbackFor={Exception.class})
    public void leaveGroup(Long groupId, Long userId) {
        StudyGroup group = (StudyGroup)this.studyGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException("\u5c0f\u7ec4\u4e0d\u5b58\u5728");
        }
        if (group.getLeaderId().equals(userId)) {
            throw new BusinessException("\u7ec4\u957f\u4e0d\u80fd\u9000\u51fa\u5c0f\u7ec4\uff0c\u8bf7\u5148\u8f6c\u8ba9\u7ec4\u957f\u6216\u89e3\u6563\u5c0f\u7ec4");
        }
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<GroupMember>();
        wrapper.eq(GroupMember::getGroupId,groupId).eq(GroupMember::getUserId, userId);
        GroupMember member = this.groupMemberMapper.selectOne(wrapper);
        if (member == null) {
            throw new BusinessException("\u60a8\u4e0d\u662f\u5c0f\u7ec4\u6210\u5458");
        }
        this.groupMemberMapper.deleteById(member.getId());
        if (group.getCurrentMembers() > 0) {
            group.setCurrentMembers(Integer.valueOf(group.getCurrentMembers() - 1));
            this.studyGroupMapper.updateById(group);
        }
    }

    @Transactional(rollbackFor={Exception.class})
    public void removeMember(Long groupId, Long leaderId, Long memberId) {
        StudyGroup group = (StudyGroup)this.studyGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException("\u5c0f\u7ec4\u4e0d\u5b58\u5728");
        }
        if (!group.getLeaderId().equals(leaderId)) {
            throw new BusinessException("\u53ea\u6709\u7ec4\u957f\u53ef\u4ee5\u79fb\u9664\u6210\u5458");
        }
        if (memberId.equals(leaderId)) {
            throw new BusinessException("\u4e0d\u80fd\u79fb\u9664\u81ea\u5df1");
        }
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<GroupMember>();
        wrapper.eq(GroupMember::getGroupId,groupId).eq(GroupMember::getUserId,memberId);
        GroupMember member = this.groupMemberMapper.selectOne(wrapper);
        if (member == null) {
            throw new BusinessException("\u8be5\u7528\u6237\u4e0d\u662f\u5c0f\u7ec4\u6210\u5458");
        }
        this.groupMemberMapper.deleteById(member.getId());
        if (group.getCurrentMembers() > 0) {
            group.setCurrentMembers(Integer.valueOf(group.getCurrentMembers() - 1));
            this.studyGroupMapper.updateById(group);
        }
    }

    @Transactional(rollbackFor={Exception.class})
    public void transferLeader(Long groupId, Long currentLeaderId, Long newLeaderId) {
        StudyGroup group = (StudyGroup)this.studyGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException("\u5c0f\u7ec4\u4e0d\u5b58\u5728");
        }
        if (!group.getLeaderId().equals(currentLeaderId)) {
            throw new BusinessException("\u53ea\u6709\u7ec4\u957f\u53ef\u4ee5\u8f6c\u8ba9\u7ec4\u957f");
        }
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<GroupMember>();
        wrapper.eq(GroupMember::getGroupId,groupId).eq(GroupMember::getUserId,newLeaderId);
        GroupMember newLeaderMember = this.groupMemberMapper.selectOne(wrapper);
        if (newLeaderMember == null) {
            throw new BusinessException("\u8be5\u7528\u6237\u4e0d\u662f\u5c0f\u7ec4\u6210\u5458");
        }
        LambdaQueryWrapper<GroupMember> oldWrapper = new LambdaQueryWrapper<GroupMember>();
        oldWrapper.eq(GroupMember::getGroupId,groupId).eq(GroupMember::getUserId,currentLeaderId);
        GroupMember oldLeaderMember = this.groupMemberMapper.selectOne(oldWrapper);
        if (oldLeaderMember != null) {
            oldLeaderMember.setMemberRole("MEMBER");
            this.groupMemberMapper.updateById(oldLeaderMember);
        }
        newLeaderMember.setMemberRole("LEADER");
        this.groupMemberMapper.updateById(newLeaderMember);
        group.setLeaderId(newLeaderId);
        this.studyGroupMapper.updateById(group);
    }

    public void setAdmin(Long groupId, Long leaderId, Long userId, boolean isAdmin) {
        StudyGroup group = (StudyGroup)this.studyGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException("\u5c0f\u7ec4\u4e0d\u5b58\u5728");
        }
        if (!group.getLeaderId().equals(leaderId)) {
            throw new BusinessException("\u53ea\u6709\u7ec4\u957f\u53ef\u4ee5\u8bbe\u7f6e\u7ba1\u7406\u5458");
        }
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<GroupMember>();
        wrapper.eq(GroupMember::getGroupId,groupId).eq(GroupMember::getUserId, userId);
        GroupMember member = this.groupMemberMapper.selectOne(wrapper);
        if (member == null) {
            throw new BusinessException("\u8be5\u7528\u6237\u4e0d\u662f\u5c0f\u7ec4\u6210\u5458");
        }
        member.setMemberRole(isAdmin ? "ADMIN" : "MEMBER");
        this.groupMemberMapper.updateById(member);
    }

    public List<Map<String, Object>> getUserGroups(Long userId) {
        LambdaQueryWrapper<GroupMember> memberWrapper = new LambdaQueryWrapper<GroupMember>();
        memberWrapper.eq(GroupMember::getUserId, userId);
        List<GroupMember> memberships = this.groupMemberMapper.selectList(memberWrapper);
        List<Long> groupIds = memberships.stream().map(m -> m.getGroupId()).collect(Collectors.toList());
        if (groupIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<StudyGroup> groupWrapper = new LambdaQueryWrapper<StudyGroup>();
        groupWrapper.in(StudyGroup::getId, groupIds).eq(StudyGroup::getStatus,1);
        List<StudyGroup> groups = this.studyGroupMapper.selectList(groupWrapper);
        Map<Long, String> roleMap = memberships.stream().collect(Collectors.toMap(m -> m.getGroupId(), m -> m.getMemberRole(), (k1, k2) -> k1));
        return groups.stream().map(group -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", group.getId());
            item.put("groupName", group.getGroupName());
            item.put("groupAvatar", group.getGroupAvatar());
            item.put("currentMembers", group.getCurrentMembers());
            item.put("memberRole", roleMap.get(group.getId()));
            return item;
        }).collect(Collectors.toList());
    }

    public boolean isMember(Long groupId, Long userId) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<GroupMember>();
        wrapper.eq(GroupMember::getGroupId,groupId).eq(GroupMember::getUserId, userId);
        return this.groupMemberMapper.selectCount(wrapper) > 0L;
    }

    public List<Map<String, Object>> getRecommendedGroups(int limit) {
        LambdaQueryWrapper<StudyGroup> wrapper = new LambdaQueryWrapper<StudyGroup>();
        wrapper.eq(StudyGroup::getStatus,1).orderByDesc(StudyGroup::getCurrentMembers).last("LIMIT " + limit);
        List<StudyGroup> groups = this.studyGroupMapper.selectList(wrapper);
        List<Long> leaderIds = groups.stream().map(g -> g.getLeaderId()).distinct().collect(Collectors.toList());
        Map<Long, User> leaderMap = new HashMap<Long, User>();
        if (!leaderIds.isEmpty()) {
            List<User> leaders = this.userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, leaderIds));
            leaderMap = leaders.stream().collect(Collectors.toMap(User::getId, u -> u, (k1, k2) -> k1));
        }
        Map<Long, User> finalLeaderMap = leaderMap;
        return groups.stream().map(group -> {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", group.getId());
            item.put("groupName", group.getGroupName());
            item.put("groupAvatar", group.getGroupAvatar());
            item.put("groupIntro", group.getGroupIntro());
            item.put("currentMembers", group.getCurrentMembers());
            item.put("maxMembers", group.getMaxMembers());
            User leader = (User)finalLeaderMap.get(group.getLeaderId());
            if (leader != null) {
                item.put("leaderName", leader.getRealName() != null ? leader.getRealName() : leader.getUsername());
            }
            return item;
        }).collect(Collectors.toList());
    }

    public StudyGroupService(StudyGroupMapper studyGroupMapper, GroupMemberMapper groupMemberMapper, UserMapper userMapper) {
        this.studyGroupMapper = studyGroupMapper;
        this.groupMemberMapper = groupMemberMapper;
        this.userMapper = userMapper;
    }
}

