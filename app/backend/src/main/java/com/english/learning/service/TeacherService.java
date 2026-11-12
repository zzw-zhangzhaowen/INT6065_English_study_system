package com.english.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Map;

public interface TeacherService {
    public Page<Map<String, Object>> getStudentPage(Long var1, int var2, int var3, String var4, Long var5);

    public Map<String, Object> getStudentDetail(Long var1, Long var2);

    public List<Map<String, Object>> getLearningAlerts(Long var1, int var2);

    public Map<String, Object> getScoreTrend(Long var1, Long var2, int var3);

    public List<Map<String, Object>> getClassList(Long var1);

    public Map<String, Object> getStudentReport(Long var1, Long var2);

    public Map<String, Object> getClassStats(Long var1, Long var2);
}

