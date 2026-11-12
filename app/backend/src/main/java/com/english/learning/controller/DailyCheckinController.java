package com.english.learning.controller;

import com.english.learning.common.result.Result;
import com.english.learning.entity.DailyCheckin;
import com.english.learning.service.DailyCheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u6bcf\u65e5\u6253\u5361")
@RestController
@RequestMapping(value={"/api/checkin"})
public class DailyCheckinController {
    private final DailyCheckinService dailyCheckinService;

    @Operation(summary="\u6bcf\u65e5\u6253\u5361")
    @PostMapping
    public Result<Map<String, Object>> checkin(Authentication authentication, @RequestBody(required=false) Map<String, String> body) {
        Long userId = (Long)authentication.getDetails();
        String notes = body != null ? body.get("notes") : null;
        String mood = body != null ? body.get("mood") : null;
        return Result.success(this.dailyCheckinService.checkin(userId, notes, mood));
    }

    @Operation(summary="\u68c0\u67e5\u4eca\u65e5\u662f\u5426\u5df2\u6253\u5361")
    @GetMapping(value={"/today"})
    public Result<Map<String, Object>> checkTodayStatus(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        boolean hasCheckedIn = this.dailyCheckinService.hasCheckedInToday(userId);
        DailyCheckin todayCheckin = this.dailyCheckinService.getTodayCheckin(userId);
        return Result.success(Map.of("hasCheckedIn", hasCheckedIn, "checkin", todayCheckin != null ? todayCheckin : Map.of()));
    }

    @Operation(summary="\u83b7\u53d6\u6253\u5361\u65e5\u5386")
    @GetMapping(value={"/calendar"})
    public Result<List<DailyCheckin>> getCheckinCalendar(Authentication authentication, @RequestParam(required=false) Integer year, @RequestParam(required=false) Integer month) {
        Long userId = (Long)authentication.getDetails();
        LocalDate now = LocalDate.now();
        int y = year != null ? year.intValue() : now.getYear();
        int m = month != null ? month.intValue() : now.getMonthValue();
        return Result.success(this.dailyCheckinService.getCheckinCalendar(userId, y, m));
    }

    @Operation(summary="\u83b7\u53d6\u6253\u5361\u7edf\u8ba1")
    @GetMapping(value={"/stats"})
    public Result<Map<String, Object>> getCheckinStats(Authentication authentication) {
        Long userId = (Long)authentication.getDetails();
        return Result.success(this.dailyCheckinService.getCheckinStats(userId));
    }

    @Operation(summary="\u83b7\u53d6\u6253\u5361\u6392\u884c\u699c")
    @GetMapping(value={"/ranking"})
    public Result<List<Map<String, Object>>> getCheckinRanking(@RequestParam(defaultValue="10") int limit) {
        return Result.success(this.dailyCheckinService.getCheckinRanking(limit));
    }

    @Operation(summary="\u66f4\u65b0\u4eca\u65e5\u5b66\u4e60\u6570\u636e")
    @PutMapping(value={"/study-data"})
    public Result<Void> updateStudyData(Authentication authentication, @RequestBody Map<String, Integer> body) {
        Long userId = (Long)authentication.getDetails();
        this.dailyCheckinService.updateCheckinData(userId, body.get("studyDuration"), body.get("wordCount"), body.get("questionCount"));
        return Result.success();
    }

    public DailyCheckinController(DailyCheckinService dailyCheckinService) {
        this.dailyCheckinService = dailyCheckinService;
    }
}

