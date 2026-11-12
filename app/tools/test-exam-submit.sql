-- 测试考试提交评分功能
-- 清理测试数据
DELETE FROM answer_record WHERE exam_record_id >= 10;
DELETE FROM exam_record WHERE id >= 10;

-- 创建一个简单的测试考试
INSERT IGNORE INTO exam (id, exam_name, exam_type, level, total_score, pass_score, duration, max_attempts, status, creator_id, create_time, update_time)
VALUES (999, '评分测试考试', 'CET4', '初级', 100, 60, 30, 3, 1, 1, NOW(), NOW());

-- 插入3道选择题到题目关联表
INSERT IGNORE INTO exam_question (exam_id, question_id, question_order, question_score)
VALUES 
(999, 216, 1, 10),
(999, 217, 2, 10),
(999, 218, 3, 10);

-- 创建一条测试考试记录
INSERT INTO exam_record (id, exam_id, student_id, start_time, total_score, obtained_score, status, cheat_warning_count, create_time, update_time)
VALUES (999, 999, 2, NOW(), 100, 0, 'IN_PROGRESS', 0, NOW(), NOW());

-- 查看题目和正确答案
SELECT id, question_type, content, answer, options 
FROM question 
WHERE id IN (216, 217, 218);

SELECT '========== 测试数据准备完成 ==========' AS status;
SELECT '现在可以使用 recordId=999 开始考试并提交答案' AS instruction;
