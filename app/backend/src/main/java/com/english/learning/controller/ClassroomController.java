package com.english.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.result.Result;
import com.english.learning.dto.ClassroomRecordUrlRequest;
import com.english.learning.dto.ClassroomRequest;
import com.english.learning.entity.Classroom;
import com.english.learning.service.ClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="\u5728\u7ebf\u8bfe\u5802")
@RestController
@RequestMapping(value={"/api/classroom"})
public class ClassroomController {
    private final ClassroomService classroomService;

    @Operation(summary="\u521b\u5efa\u8bfe\u5802")
    @PostMapping
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Long> createClassroom(Authentication authentication, @Valid @RequestBody ClassroomRequest request) {
        Long teacherId = (Long)authentication.getDetails();
        Long classroomId = this.classroomService.createClassroom(teacherId, request.getClassroomName(), request.getClassroomType(), request.getDescription(), request.getCoverImage(), request.getStartTime(), request.getEndTime(), request.getMaxStudents());
        return Result.success(classroomId);
    }

    @Operation(summary="\u66f4\u65b0\u8bfe\u5802")
    @PutMapping(value={"/{id}"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> updateClassroom(Authentication authentication, @PathVariable Long id, @Valid @RequestBody ClassroomRequest request) {
        Long teacherId = (Long)authentication.getDetails();
        this.classroomService.updateClassroom(id, teacherId, request.getClassroomName(), request.getDescription(), request.getCoverImage(), request.getStartTime(), request.getEndTime(), request.getMaxStudents());
        return Result.success();
    }

    @Operation(summary="\u5220\u9664\u8bfe\u5802")
    @DeleteMapping(value={"/{id}"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteClassroom(Authentication authentication, @PathVariable Long id) {
        Long teacherId = (Long)authentication.getDetails();
        this.classroomService.deleteClassroom(id, teacherId);
        return Result.success();
    }

    @Operation(summary="\u5f00\u59cb\u8bfe\u5802")
    @PostMapping(value={"/{id}/start"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> startClassroom(Authentication authentication, @PathVariable Long id) {
        Long teacherId = (Long)authentication.getDetails();
        this.classroomService.startClassroom(id, teacherId);
        return Result.success();
    }

    @Operation(summary="\u7ed3\u675f\u8bfe\u5802")
    @PostMapping(value={"/{id}/end"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> endClassroom(Authentication authentication, @PathVariable Long id) {
        Long teacherId = (Long)authentication.getDetails();
        this.classroomService.endClassroom(id, teacherId);
        return Result.success();
    }

    @Operation(summary="\u4fdd\u5b58\u5f55\u64ad\u5730\u5740")
    @PostMapping(value={"/{id}/record-url"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> saveRecordUrl(Authentication authentication, @PathVariable Long id, @Valid @RequestBody ClassroomRecordUrlRequest request) {
        Long teacherId = (Long)authentication.getDetails();
        this.classroomService.saveRecordUrl(id, teacherId, request.getRecordUrl());
        return Result.success();
    }

    @Operation(summary="\u5206\u9875\u67e5\u8be2\u8bfe\u5802")
    @GetMapping(value={"/page"})
    public Result<Page<Map<String, Object>>> getClassroomPage(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize, @RequestParam(required=false) String status, @RequestParam(required=false) Long teacherId, @RequestParam(required=false) String classroomType, @RequestParam(required=false) Boolean filterByClass) {
        Long studentId = null;
        if (Boolean.TRUE.equals(filterByClass)) {
            studentId = (Long)authentication.getDetails();
        }
        return Result.success(this.classroomService.getClassroomPage(pageNum, pageSize, status, teacherId, classroomType, studentId));
    }

    @Operation(summary="\u83b7\u53d6\u8bfe\u5802\u8be6\u60c5")
    @GetMapping(value={"/{id}"})
    public Result<Map<String, Object>> getClassroomDetail(Authentication authentication, @PathVariable Long id) {
        String role;
        Long userId = (Long)authentication.getDetails();
        if (!this.classroomService.hasClassroomAccess(id, userId, role = ((GrantedAuthority)authentication.getAuthorities().iterator().next()).getAuthority())) {
            return Result.fail((Integer)403, (String)"\u65e0\u6743\u8bbf\u95ee\u6b64\u8bfe\u5802");
        }
        return Result.success(this.classroomService.getClassroomDetail(id));
    }

    @Operation(summary="\u52a0\u5165\u8bfe\u5802")
    @PostMapping(value={"/{id}/join"})
    public Result<Void> joinClassroom(Authentication authentication, @PathVariable Long id) {
        Long studentId = (Long)authentication.getDetails();
        this.classroomService.joinClassroom(id, studentId);
        return Result.success();
    }

    @Operation(summary="\u79bb\u5f00\u8bfe\u5802")
    @PostMapping(value={"/{id}/leave"})
    public Result<Void> leaveClassroom(Authentication authentication, @PathVariable Long id) {
        Long studentId = (Long)authentication.getDetails();
        this.classroomService.leaveClassroom(id, studentId);
        return Result.success();
    }

    @Operation(summary="\u7b7e\u5230")
    @PostMapping(value={"/{id}/sign"})
    public Result<Void> signIn(Authentication authentication, @PathVariable Long id) {
        Long studentId = (Long)authentication.getDetails();
        this.classroomService.signIn(id, studentId);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u6559\u5e08\u7684\u8bfe\u5802\u5217\u8868")
    @GetMapping(value={"/teacher"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<Classroom>> getTeacherClassrooms(Authentication authentication) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.classroomService.getTeacherClassrooms(teacherId));
    }

    @Operation(summary="\u83b7\u53d6\u5b66\u751f\u53c2\u4e0e\u7684\u8bfe\u5802")
    @GetMapping(value={"/student"})
    public Result<Page<Map<String, Object>>> getStudentClassrooms(Authentication authentication, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="10") int pageSize) {
        Long studentId = (Long)authentication.getDetails();
        return Result.success(this.classroomService.getStudentClassrooms(studentId, pageNum, pageSize));
    }

    @Operation(summary="\u83b7\u53d6\u6b63\u5728\u76f4\u64ad\u7684\u8bfe\u5802")
    @GetMapping(value={"/live"})
    public Result<List<Map<String, Object>>> getLiveClassrooms(Authentication authentication) {
        Long studentId = (Long)authentication.getDetails();
        return Result.success(this.classroomService.getLiveClassrooms(studentId));
    }

    @Operation(summary="\u6559\u5e08\u53d1\u8d77\u7b7e\u5230")
    @PostMapping(value={"/{id}/sign/activate"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> activateSignIn(Authentication authentication, @PathVariable Long id) {
        Long teacherId = (Long)authentication.getDetails();
        this.classroomService.activateSignIn(id, teacherId);
        return Result.success();
    }

    @Operation(summary="\u83b7\u53d6\u4eca\u65e5\u8bfe\u7a0b\u5b89\u6392")
    @GetMapping(value={"/today"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<Map<String, Object>>> getTodaySchedule(Authentication authentication) {
        Long teacherId = (Long)authentication.getDetails();
        return Result.success(this.classroomService.getTodaySchedule(teacherId));
    }

    @Operation(summary="\u83b7\u53d6\u7b7e\u5230\u8bb0\u5f55")
    @GetMapping(value={"/{id}/sign/list"})
    @PreAuthorize(value="hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<Map<String, Object>>> getSignInList(@PathVariable Long id) {
        return Result.success(this.classroomService.getSignInList(id));
    }

    @Operation(summary="\u68c0\u67e5\u7b7e\u5230\u72b6\u6001")
    @GetMapping(value={"/{id}/sign/status"})
    public Result<Map<String, Object>> getSignInStatus(Authentication authentication, @PathVariable Long id) {
        Long studentId = (Long)authentication.getDetails();
        return Result.success(this.classroomService.getSignInStatus(id, studentId));
    }

    @Operation(summary="\u83b7\u53d6\u8bfe\u5802\u6d88\u606f\u5386\u53f2")
    @GetMapping(value={"/{id}/messages"})
    public Result<Map<String, Object>> getClassroomMessages(Authentication authentication, @PathVariable Long id, @RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="50") int pageSize) {
        String role;
        Long userId = (Long)authentication.getDetails();
        if (!this.classroomService.hasClassroomAccess(id, userId, role = ((GrantedAuthority)authentication.getAuthorities().iterator().next()).getAuthority())) {
            return Result.fail((Integer)403, (String)"\u65e0\u6743\u8bbf\u95ee\u6b64\u8bfe\u5802\u6d88\u606f");
        }
        return Result.success(this.classroomService.getClassroomMessages(id, pageNum, pageSize));
    }

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }
}

