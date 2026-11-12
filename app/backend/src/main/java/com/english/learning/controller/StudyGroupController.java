package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.service.StudyGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u5b66\u4e60\u5c0f\u7ec4")
@RestController
@RequestMapping(value={"/api/group"})
public class StudyGroupController {
    private final StudyGroupService studyGroupService;

    @Operation(summary="\u521b\u5efa\u5c0f\u7ec4")
    @PostMapping
    public Result<Long> createGroup(Authentication authentication, @RequestBody Map<String, Object> body) {
        Long userId = (Long)authentication.getDetails();
        String groupName = (String)body.get("groupName");
        String groupIntro = (String)body.get("groupIntro");
        String groupAvatar = (String)body.get("groupAvatar");
        Integer maxMembers = body.get("maxMembers") != null ? Integer.valueOf(Integer.parseInt(body.get("maxMembers").toString())) : null;
        Long groupId = this.studyGroupService.createGroup(userId, groupName, groupIntro, groupAvatar, maxMembers);
        return Result.success(groupId);
    }

    @Operation(summary="\u66f4\u65b0\u5c0f\u7ec4")
    @PutMapping(value={"/{id}"})
    public Result<Void> updateGroup(Authentication authentication, @PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = (Long)authentication.getDetails();
        String groupName = (String)body.get("groupName");
        String groupIntro = (String)body.get("groupIntro");
        String groupAvatar = (String)body.get("groupAvatar");
        Integer maxMembers = body.get("maxMembers") != null ? Integer.valueOf(Integer.parseInt(body.get("maxMembers").toString())) : null;
        this.studyGroupService.updateGroup(id, userId, groupName, groupIntro, groupAvatar, maxMembers);
        return Result.success();
    }

    @Operation(summary="\u89e3\u6563\u5c0f\u7ec4")
    @DeleteMapping(value={"/{id}"})
    public Result<Void> dissolveGroup(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.studyGroupService.dissolveGroup(id, userId);
        return Result.success();
    }

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u5c0f\u7ec4")
    @GetMapping(value={"/page"})
    public Result<Page<Map<String, Object>>> getGroupPage(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String keyword) {
        return Result.success(this.studyGroupService.getGroupPage(pageNum, pageSize, keyword));
    }

    @Operation(summary="\u83b7\u53d6\u5c0f\u7ec4\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<Map<String, Object>> getGroupDetail(@PathVariable Long id) {
        return Result.success(this.studyGroupService.getGroupDetail(id));
    }

    @Operation(summary="\u52a0\u5165\u5c0f\u7ec4")
    @PostMapping(value={"/{id}/join"})
    public Result<Void> joinGroup(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.studyGroupService.joinGroup(id, userId);
        return Result.success();
    }

    @Operation(summary="\u9000\u51fa\u5c0f\u7ec4")
    @PostMapping(value={"/{id}/leave"})
    public Result<Void> leaveGroup(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        this.studyGroupService.leaveGroup(id, userId);
        return Result.success();
    }

    @Operation(summary="\u79fb\u9664\u6210\u5458")
    @DeleteMapping(value={"/{id}/member/{memberId}"})
    public Result<Void> removeMember(Authentication authentication, @PathVariable Long id, @PathVariable Long memberId) {
        Long userId = (Long)authentication.getDetails();
        this.studyGroupService.removeMember(id, userId, memberId);
        return Result.success();
    }

    @Operation(summary="\u8f6c\u8ba9\u7ec4\u957f")
    @PostMapping(value={"/{id}/transfer/{newLeaderId}"})
    public Result<Void> transferLeader(Authentication authentication, @PathVariable Long id, @PathVariable Long newLeaderId) {
        Long userId = (Long)authentication.getDetails();
        this.studyGroupService.transferLeader(id, userId, newLeaderId);
        return Result.success();
    }

    @Operation(summary="\u8bbe\u7f6e\u7ba1\u7406\u5458")
    @PostMapping(value={"/{id}/admin/{memberId}"})
    public Result<Void> setAdmin(Authentication authentication, @PathVariable Long id, @PathVariable Long memberId, @RequestParam boolean isAdmin) {
        Long userId = (Long)authentication.getDetails();
        this.studyGroupService.setAdmin(id, userId, memberId, isAdmin);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u6211\u7684\u5c0f\u7ec4")
    @GetMapping(value={"/my"})
    public Result<List<Map<String, Object>>> getMyGroups(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.studyGroupService.getUserGroups(userId));
    }

    @Operation(summary="\u68c0\u67e5\u662f\u5426\u662f\u6210\u5458")
    @GetMapping(value={"/{id}/check"})
    public Result<Boolean> checkMembership(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.studyGroupService.isMember(id, userId));
    }

    @Operation(summary="\u83b7\u53d6\u63a8\u8350\u5c0f\u7ec4")
    @GetMapping(value={"/recommend"})
    public Result<List<Map<String, Object>>> getRecommendedGroups(@RequestParam(defaultValue="10") int limit) {
        return Result.success(this.studyGroupService.getRecommendedGroups(limit));
    }

    public StudyGroupController(StudyGroupService studyGroupService) {
        this.studyGroupService = studyGroupService;
    }
}

