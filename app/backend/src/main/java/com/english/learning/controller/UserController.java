package com.english.learning.controller;

import com.english.learning.common.result.Result;
import com.english.learning.dto.ChangePasswordRequest;
import com.english.learning.dto.UserProfileRequest;
import com.english.learning.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u7528\u6237\u7ba1\u7406", description="\u7528\u6237\u4fe1\u606f\u7ba1\u7406\u63a5\u53e3")
@RestController
@RequestMapping(value={"/api/user"})
public class UserController {
    private final UserService userService;

    @Operation(summary="\u83b7\u53d6\u5f53\u524d\u7528\u6237\u4fe1\u606f")
    @GetMapping(value={"/info"})
    public Result<Map<String, Object>> getUserInfo(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.userService.getUserInfo(userId));
    }

    @Operation(summary="\u66f4\u65b0\u7528\u6237\u4fe1\u606f")
    @PutMapping(value={"/profile"})
    public Result<Void> updateProfile(Authentication authentication, @RequestBody UserProfileRequest request) {
        Long userId = (Long)authentication.getDetails();
        this.userService.updateProfile(userId, request);
        return Result.success();
    }

    @Operation(summary="\u4fee\u6539\u5bc6\u7801")
    @PutMapping(value={"/password"})
    public Result<Void> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {
        Long userId = (Long)authentication.getDetails();
        this.userService.changePassword(userId, request);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u5b66\u4e60\u7edf\u8ba1")
    @GetMapping(value={"/statistics"})
    public Result<Map<String, Object>> getStudyStatistics(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.userService.getStudyStatistics(userId));
    }

    @Operation(summary="\u83b7\u53d6\u7528\u6237\u6210\u5c31\u5217\u8868")
    @GetMapping(value={"/achievements"})
    public Result<List<Map<String, Object>>> getAchievements(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.userService.getAchievements(userId));
    }

    @Operation(summary="\u5173\u6ce8\u7528\u6237")
    @PostMapping(value={"/{targetUserId}/follow"})
    public Result<Map<String, Object>> followUser(Authentication authentication, @PathVariable Long targetUserId) {
        Long userId = (Long)authentication.getDetails();
        boolean isFollowed = this.userService.toggleFollow(userId, targetUserId);
        return Result.success(Map.of("isFollowed", isFollowed));
    }

    @Operation(summary="\u53d6\u6d88\u5173\u6ce8\u7528\u6237")
    @DeleteMapping(value={"/{targetUserId}/follow"})
    public Result<Void> unfollowUser(Authentication authentication, @PathVariable Long targetUserId) {
        Long userId = (Long)authentication.getDetails();
        this.userService.unfollow(userId, targetUserId);
        return Result.success();
    }

    @Operation(summary="\u68c0\u67e5\u662f\u5426\u5df2\u5173\u6ce8")
    @GetMapping(value={"/{targetUserId}/is-followed"})
    public Result<Boolean> isFollowed(Authentication authentication, @PathVariable Long targetUserId) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.userService.isFollowed(userId, targetUserId));
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }
}

