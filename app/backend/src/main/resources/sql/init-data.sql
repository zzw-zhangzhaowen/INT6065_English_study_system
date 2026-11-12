-- 初始数据插入脚本

USE english_learning;

-- 插入角色数据（忽略重复）
INSERT IGNORE INTO sys_role (role_name, role_code, description, status) VALUES
('学生', 'ROLE_STUDENT', '学生角色', 1),
('教师', 'ROLE_TEACHER', '教师角色', 1),
('管理员', 'ROLE_ADMIN', '系统管理员', 1);

-- 插入默认管理员账号 (密码: 123456)
INSERT IGNORE INTO sys_user (id, username, password, email, real_name, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@example.com', '系统管理员', 1);

-- 关联管理员角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT 1, id FROM sys_role WHERE role_code = 'ROLE_ADMIN' LIMIT 1;

-- 插入测试学生账号 (密码: 123456)
INSERT IGNORE INTO sys_user (id, username, password, email, real_name, status) VALUES
(2, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'student1@example.com', '张三', 1);

-- 关联学生角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT 2, id FROM sys_role WHERE role_code = 'ROLE_STUDENT' LIMIT 1;

-- 插入学生信息（包含学习统计数据）
INSERT IGNORE INTO student_info (
    user_id, 
    school, 
    major, 
    grade, 
    student_no,
    current_level, 
    target_level,
    study_days,
    continuous_days,
    total_study_time,
    achievement_score
) VALUES (
    2,                     -- student1 用户ID
    '北京大学', 
    '计算机科学', 
    '大二', 
    'S2024002',
    'CET4', 
    'CET6',
    45,                    -- 学习45天
    7,                     -- 连续学习7天
    7680,                  -- 总学习128小时（7680分钟）
    1850                   -- 成就积分
);

-- 插入测试教师账号 (密码: 123456)
INSERT IGNORE INTO sys_user (id, username, password, email, real_name, status) VALUES
(3, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'teacher1@example.com', '李老师', 1);

-- 关联教师角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT 3, id FROM sys_role WHERE role_code = 'ROLE_TEACHER' LIMIT 1;

-- 插入教师信息
INSERT IGNORE INTO teacher_info (user_id, institution, title, specialization) VALUES
(3, '北京大学外语学院', '副教授', '英语听力,口语教学');

-- 插入题目分类
INSERT IGNORE INTO question_category (parent_id, category_name, category_code, level, skill_type, sort_order) VALUES
(0, '四级听力', 'CET4_LISTENING', 'CET4', '听力', 1),
(0, '四级阅读', 'CET4_READING', 'CET4', '阅读', 2),
(0, '六级听力', 'CET6_LISTENING', 'CET6', '听力', 3),
(0, '六级阅读', 'CET6_READING', 'CET6', '阅读', 4);

-- 插入示例题目
INSERT IGNORE INTO question (category_id, question_type, title, content, options, answer, analysis, difficulty, score, status, creator_id) VALUES
(1, '单选', '听力题目示例', 'What does the man mean?', '["A. He likes coffee", "B. He prefers tea", "C. He wants water", "D. He is not thirsty"]', 'B', '根据对话内容，男士表示更喜欢茶', 1, 1.00, 1, 3),
(2, '单选', '阅读理解示例', 'What is the main idea of the passage?', '["A. Option A", "B. Option B", "C. Option C", "D. Option D"]', 'C', '文章主要讨论了...', 2, 2.00, 1, 3);

-- 插入示例考试
INSERT IGNORE INTO exam (exam_name, exam_type, level, description, total_score, pass_score, duration, max_attempts, status, creator_id) VALUES
('四级模拟考试1', '模拟', 'CET4', '四级全真模拟试题', 100.00, 60.00, 120, 3, 1, 3),
('六级每日一练', '每日', 'CET6', '每日练习题', 50.00, 30.00, 30, 999, 1, 3);

-- 插入学习资源
INSERT IGNORE INTO learning_resource (resource_name, resource_type, level, description, duration, view_count, like_count, status, creator_id) VALUES
('四级词汇精讲', '视频', 'CET4', '四级核心词汇详解', 45, 0, 0, 1, 3),
('听力技巧提升', '音频', 'CET6', '提升听力理解能力', 30, 0, 0, 1, 3);

-- 插入考试记录（学习报告数据）
INSERT IGNORE INTO exam_record (student_id, exam_id, start_time, end_time, submit_time, duration, obtained_score, total_score, correct_count, wrong_count, status, create_time) VALUES
(2, 1, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY) + INTERVAL 120 MINUTE, DATE_SUB(NOW(), INTERVAL 6 DAY) + INTERVAL 120 MINUTE, 120, 85.00, 100.00, 17, 3, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(2, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY) + INTERVAL 110 MINUTE, DATE_SUB(NOW(), INTERVAL 5 DAY) + INTERVAL 110 MINUTE, 110, 78.00, 100.00, 15, 5, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 2, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY) + INTERVAL 30 MINUTE, DATE_SUB(NOW(), INTERVAL 4 DAY) + INTERVAL 30 MINUTE, 30, 42.00, 50.00, 21, 9, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 125 MINUTE, DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 125 MINUTE, 125, 92.00, 100.00, 18, 2, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 2, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 28 MINUTE, DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 28 MINUTE, 28, 45.00, 50.00, 22, 8, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 115 MINUTE, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 115 MINUTE, 115, 88.00, 100.00, 17, 3, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 2, NOW(), NOW() + INTERVAL 32 MINUTE, NOW() + INTERVAL 32 MINUTE, 32, 48.00, 50.00, 24, 6, 'COMPLETED', NOW());

-- 插入社区板块分类
INSERT IGNORE INTO community_post (category_id, author_id, title, content, view_count, like_count, reply_count, status) VALUES
(1, 2, '四级备考经验分享', '我的四级高分经验，希望对大家有帮助...', 0, 0, 0, 1);
