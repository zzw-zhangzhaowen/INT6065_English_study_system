/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80013 (8.0.13)
 Source Host           : localhost:3306
 Source Schema         : english_learning

 Target Server Type    : MySQL
 Target Server Version : 80013 (8.0.13)
 File Encoding         : 65001

 Date: 11/04/2026 01:42:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_record
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_record`;
CREATE TABLE `ai_chat_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `session_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话ID',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息类型:USER/AI',
  `message_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI对话记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_chat_record
-- ----------------------------
INSERT INTO `ai_chat_record` VALUES (1, 2, 'session_20260221_001', 'USER', '你好，我想提高英语听力，有什么建议吗？', '2026-02-22 04:10:46');
INSERT INTO `ai_chat_record` VALUES (2, 2, 'session_20260221_001', 'AI', '你好！很高兴能帮助你提高听力。以下是一些建议：\n\n1. **每日精听**：选择适合自己水平的材料，反复听直到完全理解\n2. **泛听积累**：通过英语播客、新闻等增加语感\n3. **听写练习**：边听边写，提高捕捉关键信息的能力\n4. **跟读模仿**：模仿语音语调，提升听说结合\n\n你目前是什么水平呢？我可以推荐更适合的材料。', '2026-02-22 04:10:46');
INSERT INTO `ai_chat_record` VALUES (3, 2, 'session_20260221_001', 'USER', '我在准备四级考试，感觉听力短对话最难', '2026-02-22 04:10:46');
INSERT INTO `ai_chat_record` VALUES (4, 2, 'session_20260221_001', 'AI', '四级听力短对话确实是难点！针对性建议：\n\n**技巧方面**：\n1. 预读选项，预测对话内容\n2. 注意第二个说话人的态度（答案往往在此）\n3. 听清转折词：but, however, actually\n\n**练习方法**：\n1. 做完题后精听，找出没听懂的地方\n2. 总结常考场景：打电话、约会、看病等\n3. 每天至少练习20道短对话\n\n坚持一个月，一定会有明显提升！💪', '2026-02-22 04:10:46');
INSERT INTO `ai_chat_record` VALUES (5, 2, '81275b12-cc2e-473a-8701-ab8653360af8', 'USER', '虚拟语气的用法是什么？', '2026-02-26 22:00:37');
INSERT INTO `ai_chat_record` VALUES (6, 2, '81275b12-cc2e-473a-8701-ab8653360af8', 'AI', 'Thank you for your question: \"虚拟语气的用法是什么？\"\n\n作为您的AI英语学习助手，我会尽力为您提供帮助。�?\n\n请问您想了解：\n1. **语法知识**：如虚拟语气、定语从句、时态等\n2. **词汇学习**：如何高效记忆单词、词汇积累方法\n3. **听力训练**：提高听力理解能力的技巧\n4. **口语练习**：发音、流利度提升方法\n5. **阅读技巧**：快速阅读和理解文章的方法\n6. **写作提升**：作文结构和表达方式\n7. **考试备考**：四六级、雅思等考试技巧\n\n请详细说明您的问题，我会为您提供更具体的建议和讲解。', '2026-02-26 22:00:37');
INSERT INTO `ai_chat_record` VALUES (7, 2, '07c0bb73-ab56-4742-85dd-84535235c68b', 'USER', '四六级、雅思等考试技巧', '2026-02-26 22:00:46');
INSERT INTO `ai_chat_record` VALUES (8, 2, '07c0bb73-ab56-4742-85dd-84535235c68b', 'AI', '英语考试备考技巧！�?\n\n**备考策略：**\n1. **了解考试格式**：熟悉题型和分值分布\n2. **制定复习计划**：按模块进行系统复习\n3. **做真题**：近年5-10年真题至少做2遍\n4. **模拟考试**：严格按考试时间进行模拟\n5. **弱项突破**：针对薄弱环节重点练习\n\n**考试当天注意事项：**\n• 提前15分钟到达考场\n• 带齐准考证、身份证、2B铳笔、橡皮\n• 先易后难，合理分配时间\n• 保持平常心，不要紧张\n\nGood luck with your exam! �?', '2026-02-26 22:00:46');
INSERT INTO `ai_chat_record` VALUES (9, 2, 'dfdd3d0b-510a-4392-8305-86d2d60c929c', 'USER', '快速阅读和理解文章的方法', '2026-02-26 22:00:50');
INSERT INTO `ai_chat_record` VALUES (10, 2, 'dfdd3d0b-510a-4392-8305-86d2d60c929c', 'AI', '阅读能力的提升需要系统的训练！�?\n\n**阅读技巧：**\n1. **Skimming(略读)**：快速掌握文章主旨\n2. **Scanning(扫读)**：寻找特定信息\n3. **Intensive Reading(精读)**：细致阅读，学习词汇和语法\n4. **Extensive Reading(泛读)**：大量阅读，提高阅读速度\n\n**阅读材料推荐：**\n• 初级：Graded Readers（分级读物）\n• 中级：The Economist, National Geographic\n• 高级：英文原版小说\n\n建议每天阅读至少30分钟，通过阅读扩大词汇量！', '2026-02-26 22:00:50');
INSERT INTO `ai_chat_record` VALUES (11, 2, 'aa3224c7-f73e-4763-bef8-5623b2e91b2c', 'USER', '如何提高英语听力水平？', '2026-02-27 03:24:06');
INSERT INTO `ai_chat_record` VALUES (12, 2, 'aa3224c7-f73e-4763-bef8-5623b2e91b2c', 'AI', 'To improve listening skills, here are some effective strategies: �?\n\n**1. 精听与泛听结合**\n   • 精听：选择适合自己水平的材料，反复听直到完全听懂\n   • 泛听：大量接触英语音频，培养语感\n\n**2. 选择合适的材料**\n   • 初级：VOA慢速英语、BBC Learning English\n   • 中级：TED演讲、英语新闻\n   • 高级：英美剧、原版电影\n\n**3. 注意听力技巧**\n   • 抓关键词和主旨\n   • 注意连读、弱读现象\n   • 边听边做笔记\n\n�? 建议每天至少练习30分钟听力！', '2026-02-27 03:24:06');
INSERT INTO `ai_chat_record` VALUES (13, 2, 'aa3224c7-f73e-4763-bef8-5623b2e91b2c', 'USER', 'nih ', '2026-02-27 03:24:13');
INSERT INTO `ai_chat_record` VALUES (14, 2, 'aa3224c7-f73e-4763-bef8-5623b2e91b2c', 'AI', 'Thank you for your question! 👋\n\n您好！我是您的AI英语学习助手。请问您想咨询哪方面的内容？\n\n您可以问我：\n• 语法知识点（如虚拟语气、定语从句）\n• 词汇学习方法\n• 听力/口语/阅读/写作技巧\n• 考试备考建议', '2026-02-27 03:24:13');
INSERT INTO `ai_chat_record` VALUES (15, 2, 'aa3224c7-f73e-4763-bef8-5623b2e91b2c', 'USER', '你好', '2026-02-27 03:24:15');
INSERT INTO `ai_chat_record` VALUES (16, 2, 'aa3224c7-f73e-4763-bef8-5623b2e91b2c', 'AI', 'Hello! 很高兴能帮助你学习英语👋 \n\n作为您的AI英语学习助手，我可以帮助您：\n• 解答语法问题\n• 讲解词汇用法\n• 分享学习方法\n• 提供考试技巧\n\n请告诉我你想了解什么？', '2026-02-27 03:24:15');
INSERT INTO `ai_chat_record` VALUES (17, 2, 'aa3224c7-f73e-4763-bef8-5623b2e91b2c', 'USER', '你是谁', '2026-02-27 03:24:19');
INSERT INTO `ai_chat_record` VALUES (18, 2, 'aa3224c7-f73e-4763-bef8-5623b2e91b2c', 'AI', 'Thank you for your question! 👋\n\n您好！我是您的AI英语学习助手。请问您想咨询哪方面的内容？\n\n您可以问我：\n• 语法知识点（如虚拟语气、定语从句）\n• 词汇学习方法\n• 听力/口语/阅读/写作技巧\n• 考试备考建议', '2026-02-27 03:24:19');
INSERT INTO `ai_chat_record` VALUES (19, 2, 'aa3224c7-f73e-4763-bef8-5623b2e91b2c', 'USER', '不要重复', '2026-02-27 03:24:25');
INSERT INTO `ai_chat_record` VALUES (20, 2, 'aa3224c7-f73e-4763-bef8-5623b2e91b2c', 'AI', 'Thank you for your question! 👋\n\n您好！我是您的AI英语学习助手。请问您想咨询哪方面的内容？\n\n您可以问我：\n• 语法知识点（如虚拟语气、定语从句）\n• 词汇学习方法\n• 听力/口语/阅读/写作技巧\n• 考试备考建议', '2026-02-27 03:24:25');
INSERT INTO `ai_chat_record` VALUES (21, 2, '3909acd4-5d71-48a3-88e4-025e3c68ed12', 'USER', '1', '2026-02-27 03:24:34');
INSERT INTO `ai_chat_record` VALUES (22, 2, '3909acd4-5d71-48a3-88e4-025e3c68ed12', 'AI', 'Thank you for your question! 👋\n\n您好！我是您的AI英语学习助手。请问您想咨询哪方面的内容？\n\n您可以问我：\n• 语法知识点（如虚拟语气、定语从句）\n• 词汇学习方法\n• 听力/口语/阅读/写作技巧\n• 考试备考建议', '2026-02-27 03:24:34');

-- ----------------------------
-- Table structure for answer_record
-- ----------------------------
DROP TABLE IF EXISTS `answer_record`;
CREATE TABLE `answer_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_record_id` bigint(20) NOT NULL COMMENT '考试记录ID',
  `question_id` bigint(20) NOT NULL COMMENT '题目ID',
  `student_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学生答案',
  `is_correct` tinyint(4) NULL DEFAULT NULL COMMENT '是否正确:0错误1正确',
  `obtained_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '得分',
  `answer_time` int(11) NULL DEFAULT NULL COMMENT '答题用时(秒)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_record_id`(`exam_record_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_is_correct`(`is_correct` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 66 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '答题记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of answer_record
-- ----------------------------
INSERT INTO `answer_record` VALUES (1, 1, 1, 'B', 1, 2.50, 45, '2026-02-22 04:10:46');
INSERT INTO `answer_record` VALUES (2, 1, 2, 'C', 1, 2.50, 67, '2026-02-22 04:10:46');
INSERT INTO `answer_record` VALUES (3, 2, 1, 'A', 0, 0.00, 38, '2026-02-22 04:10:46');
INSERT INTO `answer_record` VALUES (4, 2, 2, 'C', 1, 2.00, 52, '2026-02-22 04:10:46');
INSERT INTO `answer_record` VALUES (5, 3, 1, 'B', 1, 2.50, 51, '2026-02-22 04:10:46');
INSERT INTO `answer_record` VALUES (6, 3, 2, 'A', 0, 0.00, 43, '2026-02-22 04:10:46');
INSERT INTO `answer_record` VALUES (22, 9, 118, '', 0, 0.00, 0, '2026-02-23 02:36:36');
INSERT INTO `answer_record` VALUES (23, 9, 119, '', 0, 0.00, 0, '2026-02-23 02:36:36');
INSERT INTO `answer_record` VALUES (24, 9, 108, '', 0, 0.00, 0, '2026-02-23 02:36:36');
INSERT INTO `answer_record` VALUES (25, 9, 109, '', 0, 0.00, 0, '2026-02-23 02:36:36');
INSERT INTO `answer_record` VALUES (26, 9, 115, '', 0, 0.00, 0, '2026-02-23 02:36:36');
INSERT INTO `answer_record` VALUES (27, 9, 116, '', 0, 0.00, 0, '2026-02-23 02:36:36');
INSERT INTO `answer_record` VALUES (28, 9, 117, '', 0, 0.00, 0, '2026-02-23 02:36:36');
INSERT INTO `answer_record` VALUES (29, 9, 120, '1', 0, 0.00, 0, '2026-02-23 02:36:36');
INSERT INTO `answer_record` VALUES (30, 9, 114, '1', 0, 0.00, 0, '2026-02-23 02:36:36');
INSERT INTO `answer_record` VALUES (46, 999, 216, '1', 1, 10.00, 0, '2026-02-23 19:42:11');
INSERT INTO `answer_record` VALUES (47, 999, 217, '2', 1, 10.00, 0, '2026-02-23 19:42:11');
INSERT INTO `answer_record` VALUES (48, 999, 218, '0', 0, 0.00, 0, '2026-02-23 19:42:11');
INSERT INTO `answer_record` VALUES (49, 1001, 101, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (50, 1001, 102, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (51, 1001, 103, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (52, 1001, 104, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (53, 1001, 105, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (54, 1001, 106, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (55, 1001, 107, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (56, 1001, 108, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (57, 1001, 109, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (58, 1001, 110, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (59, 1001, 115, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (60, 1001, 116, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (61, 1001, 117, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (62, 1001, 112, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (63, 1001, 114, '', 0, 0.00, 0, '2026-02-26 21:58:58');
INSERT INTO `answer_record` VALUES (64, 1002, 101, '0', 0, 0.00, NULL, '2026-02-27 00:28:43');
INSERT INTO `answer_record` VALUES (65, 1006, 118, '0', 0, 0.00, NULL, '2026-03-02 01:52:20');

-- ----------------------------
-- Table structure for class_info
-- ----------------------------
DROP TABLE IF EXISTS `class_info`;
CREATE TABLE `class_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级名称',
  `grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级描述',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态:0禁用1正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教学班级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of class_info
-- ----------------------------
INSERT INTO `class_info` VALUES (1, '计算机2201班', '大二', '北京大学计算机专业CET4考试准备班级', 1, '2026-02-28 14:44:42', '2026-02-28 14:44:42', 0);
INSERT INTO `class_info` VALUES (2, '英语1901班', '大三', '英语专业CET6强化训练班', 1, '2026-02-28 14:44:42', '2026-02-28 14:44:42', 0);
INSERT INTO `class_info` VALUES (3, '软件工程2302班', '大一', '软件工程专业基础英语课程', 1, '2026-02-28 14:44:42', '2026-02-28 14:44:42', 0);

-- ----------------------------
-- Table structure for class_student
-- ----------------------------
DROP TABLE IF EXISTS `class_student`;
CREATE TABLE `class_student`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `class_id` bigint(20) NOT NULL COMMENT '班级ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_class_student`(`class_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级学生关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of class_student
-- ----------------------------
INSERT INTO `class_student` VALUES (1, 1, 2, '2026-02-28 14:44:42');
INSERT INTO `class_student` VALUES (2, 1, 4, '2026-02-28 14:44:42');
INSERT INTO `class_student` VALUES (3, 1, 5, '2026-02-28 14:44:42');
INSERT INTO `class_student` VALUES (4, 2, 6, '2026-02-28 14:44:42');
INSERT INTO `class_student` VALUES (5, 2, 7, '2026-02-28 14:44:42');
INSERT INTO `class_student` VALUES (6, 3, 8, '2026-02-28 14:44:42');

-- ----------------------------
-- Table structure for classroom
-- ----------------------------
DROP TABLE IF EXISTS `classroom`;
CREATE TABLE `classroom`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '课堂ID',
  `teacher_id` bigint(20) NOT NULL COMMENT '教师ID',
  `classroom_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课堂名称',
  `classroom_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'LIVE' COMMENT '类型:LIVE/RECORDED',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课堂封面图片URL',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `duration` int(11) NULL DEFAULT NULL COMMENT '时长(分钟)',
  `max_students` int(11) NULL DEFAULT 100 COMMENT '最大学生数',
  `current_students` int(11) NULL DEFAULT 0 COMMENT '当前学生数',
  `room_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课堂链接',
  `record_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '录播链接',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'SCHEDULED' COMMENT '状态:SCHEDULED/ONGOING/FINISHED/CANCELLED',
  `sign_in_active` tinyint(1) NOT NULL DEFAULT 0 COMMENT '签到是否激活',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课堂表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classroom
-- ----------------------------
INSERT INTO `classroom` VALUES (1, 3, '四级听力专项突破', 'LIVE', '针对四级听力常见问题的专项训练课程', NULL, '2026-02-25 19:00:00', '2026-02-25 20:30:00', 90, 100, 45, 'https://live.example.com/room/cet4_listening', NULL, 'SCHEDULED', 0, '2026-02-22 04:10:46', '2026-02-28 01:07:37', 1);
INSERT INTO `classroom` VALUES (2, 3, '英语写作技巧分享', 'LIVE', '教你如何写出高分作文', NULL, '2026-02-22 20:00:00', '2026-02-22 21:00:00', 60, 80, 32, 'https://live.example.com/room/writing_tips', NULL, 'SCHEDULED', 0, '2026-02-22 04:10:46', '2026-02-28 01:07:38', 1);
INSERT INTO `classroom` VALUES (3, 3, '111', 'LIVE', '111', NULL, '2026-02-28 00:38:53', '2026-02-28 00:42:06', 3, 100, 0, NULL, NULL, 'ENDED', 0, '2026-02-28 00:38:50', '2026-02-28 02:29:55', 1);
INSERT INTO `classroom` VALUES (4, 3, '22测试', 'LIVE', '222', NULL, '2026-02-28 00:45:30', '2026-02-28 01:07:18', 21, 100, 0, NULL, NULL, 'ENDED', 0, '2026-02-28 00:45:27', '2026-02-28 02:29:53', 1);
INSERT INTO `classroom` VALUES (5, 3, '33', 'LIVE', '33', NULL, '2026-02-28 01:21:45', '2026-02-28 01:23:54', 2, 100, 0, NULL, NULL, 'ENDED', 0, '2026-02-28 01:21:43', '2026-02-28 02:29:54', 1);
INSERT INTO `classroom` VALUES (6, 3, '添加封面测试', 'LIVE', '1111', '/uploads/images/2026/02/28/ae00eaa28b774ea4b7b02b41610e5af4.png', '2026-02-28 01:41:29', '2026-02-28 01:44:01', 2, 100, 0, NULL, NULL, 'ENDED', 0, '2026-02-28 01:41:28', '2026-02-28 01:41:28', 0);
INSERT INTO `classroom` VALUES (7, 3, '录播功能测试', 'LIVE', '222', '/uploads/images/2026/02/28/66c8600a836c4cdba64a023c8a69a4fe.png', '2026-02-28 01:51:08', '2026-02-28 01:52:22', 1, 100, 0, NULL, NULL, 'ENDED', 0, '2026-02-28 01:51:07', '2026-02-28 01:51:07', 0);
INSERT INTO `classroom` VALUES (8, 3, '3333', 'LIVE', '333', '/uploads/images/2026/02/28/26166ea4f74b49b1a4cae1f4fdb23a46.png', '2026-02-28 02:01:44', '2026-02-28 02:02:13', 0, 100, 0, NULL, NULL, 'ENDED', 0, '2026-02-28 02:01:43', '2026-02-28 02:01:43', 0);
INSERT INTO `classroom` VALUES (9, 3, '666', 'LIVE', '6666', '/uploads/images/2026/02/28/48c489f65a144547a78a721963a6c938.png', '2026-02-28 02:04:48', '2026-02-28 02:05:26', 0, 100, 0, NULL, NULL, 'ENDED', 0, '2026-02-28 02:04:46', '2026-02-28 02:04:46', 0);
INSERT INTO `classroom` VALUES (10, 3, '8888', 'LIVE', '8888', '/uploads/images/2026/02/28/b705a583406a422cbc73cba41bce2a48.png', '2026-02-28 02:10:55', '2026-02-28 02:12:31', 1, 100, 0, NULL, NULL, 'ENDED', 0, '2026-02-28 02:10:55', '2026-02-28 02:10:55', 0);
INSERT INTO `classroom` VALUES (11, 3, '999', 'LIVE', '999', '/uploads/images/2026/02/28/02b5bbddcaaf445782446907600a3a37.png', '2026-02-28 02:14:46', '2026-02-28 02:15:43', 0, 100, 0, NULL, '/uploads/video/2026/02/28/383eae9b319645389d1db1910db113c8.webm', 'ENDED', 0, '2026-02-28 02:14:45', '2026-02-28 02:14:45', 0);
INSERT INTO `classroom` VALUES (12, 3, '688878', 'LIVE', '78787', '/uploads/images/2026/02/28/83d0b8f945aa4f4db73e43ff99314575.png', '2026-02-28 20:46:50', '2026-02-28 20:47:32', 0, 100, 0, NULL, '/uploads/video/2026/02/28/378f45c643724e7db45e5f262045352c.webm', 'ENDED', 0, '2026-02-28 20:46:47', '2026-02-28 20:46:47', 0);
INSERT INTO `classroom` VALUES (13, 3, '123', 'LIVE', '123', '/uploads/images/2026/03/01/1ac87385c13347a585b4a1ec62d0173e.png', '2026-03-01 05:12:56', '2026-03-01 05:15:21', 2, 100, 0, NULL, '/uploads/video/2026/03/01/50479dc9a611463a9767d856ef5631f5.webm', 'ENDED', 0, '2026-03-01 05:12:55', '2026-03-01 05:15:15', 0);
INSERT INTO `classroom` VALUES (14, 3, '111', 'LIVE', '11', '/uploads/images/2026/04/10/8f417f2a68404765b653e3134c99ed7e.jpg', '2026-04-10 20:13:35', '2026-04-10 20:14:08', 0, 100, 0, NULL, '/uploads/video/2026/04/10/ba5d5f7ef2ee42a7bcc0e3247361f9a8.webm', 'ENDED', 1, '2026-04-10 20:13:34', '2026-04-10 20:14:44', 1);
INSERT INTO `classroom` VALUES (15, 3, '1233', 'LIVE', '1233', '/uploads/images/2026/04/10/9f5a38bf79db4fe2970ec2487db9cba7.jpg', '2026-04-10 20:14:57', '2026-04-10 20:15:12', 0, 100, 0, NULL, '/uploads/video/2026/04/10/1e7ddb0b873148f4b5672ae14263fb23.webm', 'ENDED', 1, '2026-04-10 20:14:55', '2026-04-10 20:27:34', 1);
INSERT INTO `classroom` VALUES (16, 3, '1111', 'LIVE', '1111', '/uploads/images/2026/04/10/e8edb46c542b464d9c9ef528d878b31f.png', '2026-04-10 20:21:20', '2026-04-10 20:21:32', 0, 100, 0, NULL, '/uploads/video/2026/04/10/6a6c86f003534d2192d57b839b6f67ed.webm', 'ENDED', 1, '2026-04-10 20:21:19', '2026-04-10 20:27:33', 1);
INSERT INTO `classroom` VALUES (17, 3, '1231111', 'LIVE', '111111', '/uploads/images/2026/04/10/e077560cad78491b8e84de7e0169f526.png', '2026-04-10 20:27:45', '2026-04-10 20:28:01', 0, 100, 0, NULL, '/uploads/video/2026/04/10/952ef5fa110c49849411c3afb36bcaa4.webm', 'ENDED', 0, '2026-04-10 20:27:44', '2026-04-10 20:27:44', 0);
INSERT INTO `classroom` VALUES (18, 3, '2222', 'LIVE', '22222', '/uploads/images/2026/04/10/c6b21743155f40da970b4aff25c4cb19.png', '2026-04-10 20:30:57', '2026-04-10 20:31:21', 0, 100, 0, NULL, '/uploads/video/2026/04/10/c0f0cd56413946e1814836f1cfd73001.webm', 'ENDED', 1, '2026-04-10 20:30:55', '2026-04-10 20:30:55', 0);

-- ----------------------------
-- Table structure for classroom_answer
-- ----------------------------
DROP TABLE IF EXISTS `classroom_answer`;
CREATE TABLE `classroom_answer`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(20) NOT NULL COMMENT '问题ID',
  `classroom_id` bigint(20) NOT NULL COMMENT '课堂ID',
  `user_id` bigint(20) NOT NULL COMMENT '回答者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回答内容',
  `is_best` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否最佳回答',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_classroom_id`(`classroom_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课堂回答' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classroom_answer
-- ----------------------------
INSERT INTO `classroom_answer` VALUES (1, 2, 5, 3, '1111', 0, '2026-02-28 01:22:47');
INSERT INTO `classroom_answer` VALUES (2, 1, 5, 3, '1111', 0, '2026-02-28 01:22:52');
INSERT INTO `classroom_answer` VALUES (3, 3, 5, 3, '111', 0, '2026-02-28 01:23:04');
INSERT INTO `classroom_answer` VALUES (4, 5, 6, 3, '22', 0, '2026-02-28 01:42:21');
INSERT INTO `classroom_answer` VALUES (5, 4, 6, 3, '333', 0, '2026-02-28 01:42:23');
INSERT INTO `classroom_answer` VALUES (6, 4, 6, 3, '2223', 1, '2026-02-28 01:42:29');
INSERT INTO `classroom_answer` VALUES (7, 7, 7, 3, '11', 0, '2026-02-28 01:51:33');
INSERT INTO `classroom_answer` VALUES (8, 6, 7, 3, '22', 1, '2026-02-28 01:51:36');
INSERT INTO `classroom_answer` VALUES (9, 6, 7, 3, '333', 0, '2026-02-28 01:51:41');
INSERT INTO `classroom_answer` VALUES (10, 9, 9, 3, '666', 1, '2026-02-28 02:05:14');
INSERT INTO `classroom_answer` VALUES (11, 12, 10, 3, '888', 1, '2026-02-28 02:11:22');
INSERT INTO `classroom_answer` VALUES (12, 14, 11, 3, '4444', 1, '2026-02-28 02:15:28');
INSERT INTO `classroom_answer` VALUES (13, 16, 12, 3, '2222', 0, '2026-02-28 20:47:20');
INSERT INTO `classroom_answer` VALUES (14, 18, 13, 2, '3333', 0, '2026-03-01 05:13:58');

-- ----------------------------
-- Table structure for classroom_message
-- ----------------------------
DROP TABLE IF EXISTS `classroom_message`;
CREATE TABLE `classroom_message`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `classroom_id` bigint(20) NOT NULL COMMENT '课堂ID',
  `user_id` bigint(20) NOT NULL COMMENT '发送者ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'CHAT' COMMENT '类型:CHAT/SYSTEM/SIGN_IN',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_classroom_id`(`classroom_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课堂消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classroom_message
-- ----------------------------
INSERT INTO `classroom_message` VALUES (1, 4, 3, '📋 教师已发起签到，请及时签到！', 'CHAT', '2026-02-28 00:45:46');
INSERT INTO `classroom_message` VALUES (2, 5, 3, '📋 教师已发起签到，请及时签到！', 'CHAT', '2026-02-28 01:23:49');
INSERT INTO `classroom_message` VALUES (3, 6, 3, '📋 教师已发起签到，请及时签到！', 'CHAT', '2026-02-28 01:43:34');
INSERT INTO `classroom_message` VALUES (4, 7, 3, '📋 教师已发起签到，请及时签到！', 'CHAT', '2026-02-28 01:51:53');
INSERT INTO `classroom_message` VALUES (5, 8, 3, '📋 教师已发起签到，请及时签到！', 'CHAT', '2026-02-28 02:02:08');
INSERT INTO `classroom_message` VALUES (6, 9, 3, '📋 教师已发起签到，请及时签到！', 'CHAT', '2026-02-28 02:05:21');
INSERT INTO `classroom_message` VALUES (7, 11, 3, '📋 教师已发起签到，请及时签到！', 'CHAT', '2026-02-28 02:15:35');
INSERT INTO `classroom_message` VALUES (8, 12, 3, '📋 教师已发起签到，请及时签到！', 'CHAT', '2026-02-28 20:47:09');
INSERT INTO `classroom_message` VALUES (9, 13, 3, '📋 教师已发起签到，请及时签到！', 'SYSTEM', '2026-03-01 05:14:37');
INSERT INTO `classroom_message` VALUES (10, 14, 3, '📋 教师已发起签到，请及时签到！', 'SYSTEM', '2026-04-10 20:14:01');
INSERT INTO `classroom_message` VALUES (11, 15, 3, '📋 教师已发起签到，请及时签到！', 'SYSTEM', '2026-04-10 20:15:09');
INSERT INTO `classroom_message` VALUES (12, 16, 3, '📋 教师已发起签到，请及时签到！', 'SYSTEM', '2026-04-10 20:21:29');
INSERT INTO `classroom_message` VALUES (13, 18, 3, '📋 教师已发起签到，请及时签到！', 'SYSTEM', '2026-04-10 20:31:17');

-- ----------------------------
-- Table structure for classroom_participant
-- ----------------------------
DROP TABLE IF EXISTS `classroom_participant`;
CREATE TABLE `classroom_participant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `classroom_id` bigint(20) NOT NULL COMMENT '课堂ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `join_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
  `leave_time` datetime NULL DEFAULT NULL COMMENT '离开时间',
  `sign_in_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  `duration` int(11) NULL DEFAULT NULL COMMENT '参与时长(分钟)',
  `is_signed` tinyint(4) NULL DEFAULT 0 COMMENT '是否签到',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_classroom_id`(`classroom_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课堂参与记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classroom_participant
-- ----------------------------
INSERT INTO `classroom_participant` VALUES (1, 1, 2, '2026-02-25 19:00:00', NULL, NULL, 90, 1, '2026-02-22 04:10:46');
INSERT INTO `classroom_participant` VALUES (2, 2, 2, '2026-02-22 20:00:00', NULL, NULL, 60, 1, '2026-02-22 04:10:46');
INSERT INTO `classroom_participant` VALUES (3, 13, 2, '2026-03-01 05:13:35', '2026-03-01 05:14:06', '2026-03-01 05:13:45', 0, 0, '2026-03-01 05:13:35');
INSERT INTO `classroom_participant` VALUES (4, 13, 2, '2026-03-01 05:14:11', '2026-03-01 05:15:15', '2026-03-01 05:15:04', 1, 0, '2026-03-01 05:14:11');

-- ----------------------------
-- Table structure for classroom_poll
-- ----------------------------
DROP TABLE IF EXISTS `classroom_poll`;
CREATE TABLE `classroom_poll`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `classroom_id` bigint(20) NOT NULL COMMENT '课堂ID',
  `creator_id` bigint(20) NOT NULL COMMENT '创建者ID',
  `title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投票标题',
  `poll_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SINGLE' COMMENT '类型: SINGLE/MULTIPLE',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/CLOSED',
  `anonymous` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否匿名投票',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_time` datetime NULL DEFAULT NULL COMMENT '截止时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_classroom_id`(`classroom_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课堂投票' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classroom_poll
-- ----------------------------
INSERT INTO `classroom_poll` VALUES (1, 5, 3, '11', 'SINGLE', 'CLOSED', 0, '2026-02-28 01:23:19', NULL);
INSERT INTO `classroom_poll` VALUES (2, 8, 3, '333', 'SINGLE', 'ACTIVE', 0, '2026-02-28 02:02:02', NULL);
INSERT INTO `classroom_poll` VALUES (3, 9, 3, '6666', 'SINGLE', 'ACTIVE', 0, '2026-02-28 02:05:06', NULL);
INSERT INTO `classroom_poll` VALUES (4, 11, 3, '333', 'SINGLE', 'CLOSED', 0, '2026-02-28 02:15:14', NULL);
INSERT INTO `classroom_poll` VALUES (5, 12, 3, '222', 'SINGLE', 'ACTIVE', 0, '2026-02-28 20:47:27', NULL);

-- ----------------------------
-- Table structure for classroom_poll_option
-- ----------------------------
DROP TABLE IF EXISTS `classroom_poll_option`;
CREATE TABLE `classroom_poll_option`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `poll_id` bigint(20) NOT NULL COMMENT '投票ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项内容',
  `vote_count` int(11) NOT NULL DEFAULT 0 COMMENT '票数',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_poll_id`(`poll_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '投票选项' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classroom_poll_option
-- ----------------------------
INSERT INTO `classroom_poll_option` VALUES (1, 1, '11110', 0, 0);
INSERT INTO `classroom_poll_option` VALUES (2, 1, '2220', 0, 1);
INSERT INTO `classroom_poll_option` VALUES (3, 2, '1', 0, 0);
INSERT INTO `classroom_poll_option` VALUES (4, 2, '2', 0, 1);
INSERT INTO `classroom_poll_option` VALUES (5, 3, '66', 0, 0);
INSERT INTO `classroom_poll_option` VALUES (6, 3, '66', 0, 1);
INSERT INTO `classroom_poll_option` VALUES (7, 4, '44', 0, 0);
INSERT INTO `classroom_poll_option` VALUES (8, 4, '4445', 0, 1);
INSERT INTO `classroom_poll_option` VALUES (9, 5, '233', 0, 0);
INSERT INTO `classroom_poll_option` VALUES (10, 5, '32323', 0, 1);

-- ----------------------------
-- Table structure for classroom_poll_vote
-- ----------------------------
DROP TABLE IF EXISTS `classroom_poll_vote`;
CREATE TABLE `classroom_poll_vote`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `poll_id` bigint(20) NOT NULL COMMENT '投票ID',
  `option_id` bigint(20) NOT NULL COMMENT '选项ID',
  `user_id` bigint(20) NOT NULL COMMENT '投票者ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_poll_user_option`(`poll_id` ASC, `user_id` ASC, `option_id` ASC) USING BTREE,
  INDEX `idx_poll_id`(`poll_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '投票记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classroom_poll_vote
-- ----------------------------

-- ----------------------------
-- Table structure for classroom_question
-- ----------------------------
DROP TABLE IF EXISTS `classroom_question`;
CREATE TABLE `classroom_question`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `classroom_id` bigint(20) NOT NULL COMMENT '课堂ID',
  `user_id` bigint(20) NOT NULL COMMENT '提问者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题内容',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'OPEN' COMMENT '状态: OPEN/ANSWERED/CLOSED',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否匿名',
  `like_count` int(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_classroom_id`(`classroom_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课堂问答' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classroom_question
-- ----------------------------
INSERT INTO `classroom_question` VALUES (1, 5, 3, '1111', 'ANSWERED', 0, 1, '2026-02-28 01:22:36', '2026-02-28 01:22:36');
INSERT INTO `classroom_question` VALUES (2, 5, 3, '大家会了吗', 'CLOSED', 0, 0, '2026-02-28 01:22:44', '2026-02-28 01:22:44');
INSERT INTO `classroom_question` VALUES (3, 5, 3, '111', 'ANSWERED', 1, 0, '2026-02-28 01:23:00', '2026-02-28 01:23:00');
INSERT INTO `classroom_question` VALUES (4, 6, 3, '1111', 'ANSWERED', 0, 0, '2026-02-28 01:42:14', '2026-02-28 01:42:14');
INSERT INTO `classroom_question` VALUES (5, 6, 3, '1111', 'ANSWERED', 1, 0, '2026-02-28 01:42:18', '2026-02-28 01:42:18');
INSERT INTO `classroom_question` VALUES (6, 7, 3, '111', 'ANSWERED', 0, 1, '2026-02-28 01:51:21', '2026-02-28 01:51:21');
INSERT INTO `classroom_question` VALUES (7, 7, 3, '22', 'ANSWERED', 1, 1, '2026-02-28 01:51:29', '2026-02-28 01:51:29');
INSERT INTO `classroom_question` VALUES (8, 8, 3, '333', 'OPEN', 0, 0, '2026-02-28 02:01:57', '2026-02-28 02:01:57');
INSERT INTO `classroom_question` VALUES (9, 9, 3, '6666', 'ANSWERED', 0, 0, '2026-02-28 02:05:12', '2026-02-28 02:05:12');
INSERT INTO `classroom_question` VALUES (10, 9, 3, '666', 'OPEN', 1, 0, '2026-02-28 02:05:19', '2026-02-28 02:05:19');
INSERT INTO `classroom_question` VALUES (11, 10, 3, '888', 'OPEN', 1, 0, '2026-02-28 02:11:17', '2026-02-28 02:11:17');
INSERT INTO `classroom_question` VALUES (12, 10, 3, '888', 'ANSWERED', 0, 0, '2026-02-28 02:11:19', '2026-02-28 02:11:19');
INSERT INTO `classroom_question` VALUES (13, 11, 3, '111', 'OPEN', 0, 0, '2026-02-28 02:15:07', '2026-02-28 02:15:07');
INSERT INTO `classroom_question` VALUES (14, 11, 3, '44455555', 'ANSWERED', 0, 0, '2026-02-28 02:15:25', '2026-02-28 02:15:25');
INSERT INTO `classroom_question` VALUES (15, 12, 3, '222', 'OPEN', 1, 0, '2026-02-28 20:47:12', '2026-02-28 20:47:12');
INSERT INTO `classroom_question` VALUES (16, 12, 3, '2222', 'ANSWERED', 0, 0, '2026-02-28 20:47:15', '2026-02-28 20:47:15');
INSERT INTO `classroom_question` VALUES (17, 13, 2, '11111', 'OPEN', 0, 0, '2026-03-01 05:13:50', '2026-03-01 05:13:50');
INSERT INTO `classroom_question` VALUES (18, 13, 2, '22222', 'ANSWERED', 0, 0, '2026-03-01 05:13:54', '2026-03-01 05:13:54');
INSERT INTO `classroom_question` VALUES (19, 13, 2, '2223', 'OPEN', 0, 0, '2026-03-01 05:14:02', '2026-03-01 05:14:02');

-- ----------------------------
-- Table structure for community_post
-- ----------------------------
DROP TABLE IF EXISTS `community_post`;
CREATE TABLE `community_post`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '分类ID',
  `author_id` bigint(20) NOT NULL COMMENT '作者ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `post_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'DISCUSSION' COMMENT '类型:DISCUSSION/QUESTION/SHARE/ANNOUNCEMENT',
  `is_topped` tinyint(4) NULL DEFAULT 0 COMMENT '是否置顶',
  `is_featured` tinyint(4) NULL DEFAULT 0 COMMENT '是否精华',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞数',
  `reply_count` int(11) NULL DEFAULT 0 COMMENT '回复数',
  `collect_count` int(11) NULL DEFAULT 0 COMMENT '收藏数',
  `last_reply_time` datetime NULL DEFAULT NULL COMMENT '最后回复时间',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_author_id`(`author_id` ASC) USING BTREE,
  INDEX `idx_post_type`(`post_type` ASC) USING BTREE,
  FULLTEXT INDEX `idx_title_content`(`title`, `content`) WITH PARSER `ngram`
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社区帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_post
-- ----------------------------
INSERT INTO `community_post` VALUES (1, 1, 2, '四级备考经验分享', '我的四级高分经验，希望对大家有帮助...', 'DISCUSSION', 0, 0, 0, 0, 0, 0, NULL, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `community_post` VALUES (2, 1, 2, '我的四级备考经验分享', '经过两个多月的努力，我终于在上次模拟考试中取得了87.5分的好成绩！在这里分享一下我的学习心得：\n\n1. **词汇积累**：每天坚持背100个单词，用APP打卡\n2. **听力训练**：每天早上听30分钟英语新闻\n3. **阅读理解**：刷题+精读相结合\n4. **写作练习**：每周至少写2篇作文\n\n希望对大家有帮助！一起加油！💪', 'SHARE', 0, 0, 156, 23, 8, 0, NULL, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `community_post` VALUES (3, 1, 2, '听力短对话如何快速抓住关键信息？', '最近在练习听力短对话，总是抓不住重点，导致题目做错。想请教一下大家有什么好的技巧吗？\n\n我目前的问题是：\n1. 对话速度快，来不及反应\n2. 关键词听不清楚\n3. 选项干扰性强\n\n求大神指点！🙏', 'QUESTION', 0, 0, 89, 12, 15, 0, NULL, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `community_post` VALUES (4, 1, 2, '推荐一个超好用的单词记忆方法', '分享一个我最近发现的单词记忆法——\"词根词缀联想法\"！\n\n比如：\n- un + happy = unhappy（不开心的）\n- dis + agree = disagree（不同意）\n- re + write = rewrite（重写）\n\n通过拆解词根词缀，记忆效率提升了好多！大家试试看~', 'SHARE', 0, 0, 234, 45, 12, 0, NULL, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `community_post` VALUES (5, NULL, 2, '222222222222', '1111111111111111', 'DISCUSSION', 0, 0, 3, 0, 1, 0, NULL, 1, '2026-02-23 03:24:19', '2026-03-02 01:52:37', 0);
INSERT INTO `community_post` VALUES (6, 1, 2, '四级备考经验分享', '我的四级高分经验，希望对大家有帮助...', 'DISCUSSION', 0, 0, 0, 0, 0, 0, NULL, 1, '2026-03-02 03:05:44', '2026-03-02 03:05:44', 0);

-- ----------------------------
-- Table structure for daily_checkin
-- ----------------------------
DROP TABLE IF EXISTS `daily_checkin`;
CREATE TABLE `daily_checkin`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `checkin_date` date NOT NULL COMMENT '打卡日期',
  `checkin_time` datetime NULL DEFAULT NULL COMMENT '打卡时间',
  `study_duration` int(11) NULL DEFAULT 0 COMMENT '学习时长(分钟)',
  `word_count` int(11) NULL DEFAULT 0 COMMENT '学习单词数',
  `question_count` int(11) NULL DEFAULT 0 COMMENT '做题数量',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学习心得',
  `mood` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '心情:HAPPY/NORMAL/SAD',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id` ASC, `checkin_date` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_checkin_date`(`checkin_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '每日打卡表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of daily_checkin
-- ----------------------------
INSERT INTO `daily_checkin` VALUES (1, 2, '2026-02-21', '2026-02-21 08:30:00', 145, 85, 42, '今天效率很高，完成了既定目标！', 'HAPPY', '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `daily_checkin` VALUES (2, 2, '2026-02-20', '2026-02-20 09:00:00', 180, 100, 50, '考试结束，感觉还不错', 'NORMAL', '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `daily_checkin` VALUES (3, 2, '2026-02-19', '2026-02-19 08:45:00', 120, 80, 35, '做题正确率提升了', 'HAPPY', '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `daily_checkin` VALUES (4, 2, '2026-02-18', '2026-02-18 08:30:00', 150, 95, 45, '模拟考试发挥很好', 'HAPPY', '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `daily_checkin` VALUES (5, 2, '2026-02-17', '2026-02-17 09:15:00', 90, 70, 30, '状态一般，需要调整', 'NORMAL', '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `daily_checkin` VALUES (6, 2, '2026-02-16', '2026-02-16 08:00:00', 110, 90, 38, '听力练习有进步', 'HAPPY', '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `daily_checkin` VALUES (7, 2, '2026-02-15', '2026-02-15 08:30:00', 135, 88, 40, '新的一周，继续加油！', 'HAPPY', '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `daily_checkin` VALUES (8, 2, '2026-02-22', '2026-02-22 21:47:30', 0, 0, 0, NULL, 'NORMAL', '2026-02-22 21:47:30', '2026-02-22 21:47:30');
INSERT INTO `daily_checkin` VALUES (9, 2, '2026-02-23', '2026-02-23 03:27:44', 0, 0, 0, NULL, 'NORMAL', '2026-02-23 03:27:44', '2026-02-23 03:27:44');
INSERT INTO `daily_checkin` VALUES (10, 2, '2026-02-26', '2026-02-26 22:02:14', 0, 0, 0, NULL, 'NORMAL', '2026-02-26 22:02:14', '2026-02-26 22:02:14');
INSERT INTO `daily_checkin` VALUES (11, 2, '2026-02-27', '2026-02-27 00:28:48', 0, 0, 0, NULL, 'NORMAL', '2026-02-27 00:28:48', '2026-02-27 00:28:48');
INSERT INTO `daily_checkin` VALUES (12, 2, '2026-03-01', '2026-03-01 04:16:18', 0, 0, 0, NULL, 'NORMAL', '2026-03-01 04:16:18', '2026-03-01 04:16:18');
INSERT INTO `daily_checkin` VALUES (13, 2, '2026-03-02', '2026-03-02 01:52:57', 0, 0, 0, NULL, 'NORMAL', '2026-03-02 01:52:57', '2026-03-02 01:52:57');
INSERT INTO `daily_checkin` VALUES (14, 2, '2026-04-09', '2026-04-09 02:02:09', 0, 0, 0, NULL, 'NORMAL', '2026-04-09 02:02:09', '2026-04-09 02:02:09');

-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '考试ID',
  `exam_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试名称',
  `exam_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试类型:模拟/正式/章节/每日',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '等级',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '考试说明',
  `total_score` decimal(10, 2) NULL DEFAULT 100.00 COMMENT '总分',
  `pass_score` decimal(10, 2) NULL DEFAULT 60.00 COMMENT '及格分',
  `duration` int(11) NOT NULL COMMENT '考试时长(分钟)',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `max_attempts` int(11) NULL DEFAULT 1 COMMENT '最大尝试次数',
  `enable_anti_cheat` tinyint(4) NULL DEFAULT 1 COMMENT '是否启用防作弊',
  `random_question` tinyint(4) NULL DEFAULT 0 COMMENT '是否随机题目',
  `random_option` tinyint(4) NULL DEFAULT 0 COMMENT '是否随机选项',
  `show_result` tinyint(4) NULL DEFAULT 1 COMMENT '考后是否显示结果',
  `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态:0禁用1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_type`(`exam_type` ASC) USING BTREE,
  INDEX `idx_level`(`level` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_end_time`(`end_time` ASC) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1004 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam
-- ----------------------------
INSERT INTO `exam` VALUES (1, '四级模拟考试1', '模拟', 'CET4', '四级全真模拟试题', 100.00, 60.00, 120, '2026-02-23 00:35:57', '2026-02-23 02:35:57', 3, 1, 0, 0, 1, 3, 1, '2026-02-22 04:10:46', '2026-02-23 01:35:57', 0);
INSERT INTO `exam` VALUES (2, '六级每日一练', '每日', 'CET6', '每日练习题', 50.00, 30.00, 30, NULL, NULL, 999, 1, 0, 0, 1, 3, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `exam` VALUES (41, '四级全真模拟考试-第二套', '模拟', 'CET4', '完全按照真实考试流程，含听力、阅读、写作、翻译', 710.00, 425.00, 130, '2026-02-23 00:35:57', '2026-02-23 02:35:57', 5, 1, 0, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:35:57', 0);
INSERT INTO `exam` VALUES (42, '四级全真模拟考试-第三套', '模拟', 'CET4', '2024年最新题型模拟', 710.00, 425.00, 130, '2026-02-22 09:00:00', '2026-03-22 18:00:00', 5, 1, 0, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (43, '六级全真模拟考试-第一套', '模拟', 'CET6', '六级全真模拟，检验复习效果', 710.00, 425.00, 140, '2026-02-22 09:00:00', '2026-03-22 18:00:00', 5, 1, 0, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (44, '六级全真模拟考试-第二套', '模拟', 'CET6', '高质量模拟试题', 710.00, 425.00, 140, '2026-02-22 09:00:00', '2026-03-22 18:00:00', 5, 1, 0, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (45, '四级听力专项训练-基础篇', '专项', 'CET4', '听力基础训练，短对话+长对话', 100.00, 60.00, 30, '2026-02-23 00:35:57', '2026-02-23 02:35:57', 999, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:35:57', 0);
INSERT INTO `exam` VALUES (46, '四级听力专项训练-进阶篇', '专项', 'CET4', '听力进阶训练，新闻听力+讲座', 100.00, 60.00, 30, NULL, NULL, 999, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (47, '四级阅读专项训练-快速阅读', '专项', 'CET4', '快速阅读技巧训练', 100.00, 60.00, 40, NULL, NULL, 999, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (48, '四级阅读专项训练-仔细阅读', '专项', 'CET4', '仔细阅读理解训练', 100.00, 60.00, 40, NULL, NULL, 999, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (49, '六级听力专项训练', '专项', 'CET6', '六级听力难点突破', 100.00, 60.00, 35, NULL, NULL, 999, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (50, '六级阅读专项训练', '专项', 'CET6', '六级阅读长难句分析', 100.00, 60.00, 45, NULL, NULL, 999, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-27 23:46:37', 1);
INSERT INTO `exam` VALUES (51, '四级每日一练-Day1', '每日', 'CET4', '每日精选题目，保持做题手感', 50.00, 30.00, 20, NULL, NULL, 999, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (52, '四级每日一练-Day2', '每日', 'CET4', '每日精选题目，保持做题手感', 50.00, 30.00, 20, NULL, NULL, 999, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (53, '四级每日一练-Day3', '每日', 'CET4', '每日精选题目，保持做题手感', 50.00, 30.00, 20, NULL, NULL, 999, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (54, '六级每日一练-Day1', '每日', 'CET6', '每日精选六级题目', 50.00, 30.00, 25, NULL, NULL, 999, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (55, '六级每日一练-Day2', '每日', 'CET6', '每日精选六级题目', 50.00, 30.00, 25, NULL, NULL, 999, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (56, '2026年6月大学英语四级考试', '正式', 'CET4', '全国大学英语四级统一考试', 710.00, 425.00, 130, '2026-06-15 09:00:00', '2026-06-15 11:20:00', 1, 1, 0, 0, 0, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (57, '2026年6月大学英语六级考试', '正式', 'CET6', '全国大学英语六级统一考试', 710.00, 425.00, 140, '2026-06-15 15:00:00', '2026-06-15 17:25:00', 1, 1, 0, 0, 0, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (58, '周测验-第1周', '模拟', 'CET4', '本周学习效果检测', 100.00, 60.00, 60, '2026-02-17 00:00:00', '2026-02-23 23:59:59', 3, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (59, '周测验-第2周', '模拟', 'CET4', '本周学习效果检测', 100.00, 60.00, 60, '2026-02-24 00:00:00', '2026-03-02 23:59:59', 3, 0, 1, 1, 1, 3, 1, '2026-02-23 01:16:23', '2026-02-23 01:16:23', 0);
INSERT INTO `exam` VALUES (1000, '11', 'MOCK', 'CET4', '111', 100.00, 60.00, 120, '2026-02-27 16:00:00', NULL, 1, 1, 0, 0, 1, NULL, 0, '2026-02-27 23:06:56', '2026-02-27 23:06:56', 0);
INSERT INTO `exam` VALUES (1001, '1111', 'MOCK', 'CET6', '11111', 100.00, 60.00, 120, '2026-02-26 16:00:00', '2026-02-26 18:00:00', 1, 1, 0, 0, 1, 3, 1, '2026-02-27 23:46:51', '2026-02-27 23:46:51', 0);
INSERT INTO `exam` VALUES (1002, '四级模拟考试1', '模拟', 'CET4', '四级全真模拟试题', 100.00, 60.00, 120, NULL, NULL, 3, 1, 0, 0, 1, 3, 1, '2026-03-02 03:05:44', '2026-03-02 03:05:44', 0);
INSERT INTO `exam` VALUES (1003, '六级每日一练', '每日', 'CET6', '每日练习题', 50.00, 30.00, 30, NULL, NULL, 999, 1, 0, 0, 1, 3, 1, '2026-03-02 03:05:44', '2026-03-02 03:05:44', 0);

-- ----------------------------
-- Table structure for exam_question
-- ----------------------------
DROP TABLE IF EXISTS `exam_question`;
CREATE TABLE `exam_question`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `exam_id` bigint(20) NOT NULL COMMENT '考试ID',
  `question_id` bigint(20) NOT NULL COMMENT '题目ID',
  `question_order` int(11) NULL DEFAULT 0 COMMENT '题目顺序',
  `question_score` decimal(5, 2) NULL DEFAULT 1.00 COMMENT '该题分数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_exam_question`(`exam_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_exam_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 638 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试题目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_question
-- ----------------------------
INSERT INTO `exam_question` VALUES (1, 1, 1, 1, 2.50, '2026-02-22 04:10:46');
INSERT INTO `exam_question` VALUES (2, 1, 2, 2, 2.50, '2026-02-22 04:10:46');
INSERT INTO `exam_question` VALUES (3, 2, 1, 1, 2.00, '2026-02-22 04:10:46');
INSERT INTO `exam_question` VALUES (4, 2, 2, 2, 2.00, '2026-02-22 04:10:46');
INSERT INTO `exam_question` VALUES (259, 3, 1, 1, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (260, 3, 3, 2, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (261, 3, 4, 3, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (262, 3, 5, 4, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (263, 3, 2, 5, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (264, 3, 6, 6, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (265, 3, 7, 7, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (266, 3, 8, 8, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (267, 4, 1, 1, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (268, 4, 4, 2, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (269, 4, 5, 3, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (270, 4, 3, 4, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (271, 4, 2, 5, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (272, 4, 7, 6, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (273, 4, 8, 7, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (274, 4, 6, 8, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (275, 5, 9, 1, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (276, 5, 10, 2, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (277, 5, 11, 3, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (278, 5, 12, 4, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (281, 6, 10, 1, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (282, 6, 11, 2, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (283, 6, 12, 3, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (284, 6, 9, 4, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (287, 7, 1, 1, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (288, 7, 3, 2, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (289, 7, 4, 3, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (290, 7, 5, 4, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (295, 8, 5, 1, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (296, 8, 4, 2, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (297, 8, 3, 3, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (298, 8, 1, 4, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (303, 9, 2, 1, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (304, 9, 6, 2, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (305, 9, 7, 3, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (306, 9, 8, 4, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (311, 10, 8, 1, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (312, 10, 7, 2, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (313, 10, 6, 3, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (314, 10, 2, 4, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (319, 11, 9, 1, 16.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (320, 11, 10, 2, 16.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (325, 12, 11, 1, 16.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (326, 12, 12, 2, 16.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (331, 13, 1, 1, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (332, 13, 2, 2, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (333, 13, 3, 3, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (334, 13, 6, 4, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (335, 13, 7, 5, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (336, 14, 4, 1, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (337, 14, 5, 2, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (338, 14, 8, 3, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (339, 14, 2, 4, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (340, 14, 6, 5, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (341, 15, 3, 1, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (342, 15, 7, 2, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (343, 15, 1, 3, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (344, 15, 8, 4, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (345, 15, 5, 5, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (346, 16, 9, 1, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (347, 16, 10, 2, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (348, 16, 11, 3, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (349, 16, 12, 4, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (351, 17, 10, 1, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (352, 17, 11, 2, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (353, 17, 12, 3, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (354, 17, 9, 4, 10.00, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (356, 18, 1, 1, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (357, 18, 3, 2, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (358, 18, 4, 3, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (359, 18, 5, 4, 17.75, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (360, 18, 2, 5, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (361, 18, 6, 6, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (362, 18, 7, 7, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (363, 18, 8, 8, 24.85, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (364, 19, 9, 1, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (365, 19, 10, 2, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (366, 19, 11, 3, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (367, 19, 12, 4, 23.67, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (370, 20, 1, 1, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (371, 20, 2, 2, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (372, 20, 3, 3, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (373, 20, 4, 4, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (374, 20, 5, 5, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (375, 20, 6, 6, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (376, 20, 7, 7, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (377, 20, 8, 8, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (378, 21, 4, 1, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (379, 21, 5, 2, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (380, 21, 6, 3, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (381, 21, 7, 4, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (382, 21, 8, 5, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (383, 21, 1, 6, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (384, 21, 2, 7, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (385, 21, 3, 8, 12.50, '2026-02-23 01:16:23');
INSERT INTO `exam_question` VALUES (386, 41, 101, 1, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (387, 41, 102, 2, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (388, 41, 103, 3, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (389, 41, 104, 4, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (390, 41, 105, 5, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (391, 41, 106, 6, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (392, 41, 107, 7, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (393, 41, 108, 8, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (394, 41, 109, 9, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (395, 41, 110, 10, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (396, 41, 115, 11, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (397, 41, 116, 12, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (398, 41, 117, 13, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (399, 41, 111, 14, 15.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (400, 41, 113, 15, 15.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (401, 42, 101, 1, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (402, 42, 102, 2, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (403, 42, 103, 3, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (404, 42, 104, 4, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (405, 42, 105, 5, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (406, 42, 106, 6, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (407, 42, 107, 7, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (408, 42, 108, 8, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (409, 42, 109, 9, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (410, 42, 110, 10, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (411, 42, 115, 11, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (412, 42, 116, 12, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (413, 42, 117, 13, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (414, 42, 112, 14, 15.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (415, 42, 114, 15, 15.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (416, 43, 118, 1, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (417, 43, 101, 2, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (418, 43, 102, 3, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (419, 43, 103, 4, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (420, 43, 104, 5, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (421, 43, 119, 6, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (422, 43, 107, 7, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (423, 43, 108, 8, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (424, 43, 109, 9, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (425, 43, 110, 10, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (426, 43, 115, 11, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (427, 43, 116, 12, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (428, 43, 117, 13, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (429, 43, 120, 14, 15.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (430, 43, 113, 15, 15.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (431, 44, 118, 1, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (432, 44, 101, 2, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (433, 44, 102, 3, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (434, 44, 103, 4, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (435, 44, 104, 5, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (436, 44, 119, 6, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (437, 44, 107, 7, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (438, 44, 108, 8, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (439, 44, 109, 9, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (440, 44, 110, 10, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (441, 44, 115, 11, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (442, 44, 116, 12, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (443, 44, 117, 13, 1.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (444, 44, 120, 14, 15.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (445, 44, 114, 15, 15.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (446, 45, 101, 1, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (447, 45, 102, 2, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (448, 45, 103, 3, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (449, 45, 104, 4, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (450, 45, 105, 5, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (456, 46, 103, 1, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (457, 46, 104, 2, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (458, 46, 105, 3, 4.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (465, 47, 106, 1, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (466, 47, 107, 2, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (467, 47, 108, 3, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (468, 47, 109, 4, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (469, 47, 110, 5, 2.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (475, 48, 107, 1, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (476, 48, 108, 2, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (477, 48, 109, 3, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (478, 48, 110, 4, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (483, 49, 118, 1, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (484, 49, 103, 2, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (485, 49, 104, 3, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (486, 49, 105, 4, 4.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (491, 50, 119, 1, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (492, 50, 108, 2, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (493, 50, 109, 3, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (494, 50, 110, 4, 3.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (499, 51, 111, 1, 25.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (500, 51, 112, 2, 25.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (501, 52, 113, 1, 25.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (502, 52, 114, 2, 25.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (503, 53, 120, 1, 50.00, '2026-02-23 01:59:34');
INSERT INTO `exam_question` VALUES (504, 54, 113, 1, 25.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (505, 54, 114, 2, 25.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (506, 55, 121, 1, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (507, 55, 122, 2, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (508, 55, 123, 3, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (516, 56, 115, 1, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (517, 56, 116, 2, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (518, 56, 117, 3, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (526, 57, 118, 1, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (527, 57, 119, 2, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (528, 57, 107, 3, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (529, 57, 108, 4, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (530, 57, 115, 5, 1.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (531, 57, 116, 6, 1.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (532, 57, 117, 7, 1.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (533, 57, 120, 8, 15.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (534, 57, 113, 9, 15.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (535, 58, 118, 1, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (536, 58, 119, 2, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (537, 58, 108, 3, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (538, 58, 109, 4, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (539, 58, 115, 5, 1.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (540, 58, 116, 6, 1.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (541, 58, 117, 7, 1.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (542, 58, 120, 8, 15.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (543, 58, 114, 9, 15.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (544, 59, 101, 1, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (545, 59, 106, 2, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (546, 59, 121, 3, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (547, 59, 115, 4, 2.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (548, 59, 113, 5, 12.00, '2026-02-23 01:59:35');
INSERT INTO `exam_question` VALUES (549, 41, 118, 16, 2.00, '2026-02-23 02:54:37');
INSERT INTO `exam_question` VALUES (550, 41, 119, 17, 2.00, '2026-02-23 02:54:37');
INSERT INTO `exam_question` VALUES (553, 50, 201, 1, 106.50, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (554, 50, 202, 2, 7.10, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (555, 50, 203, 3, 7.10, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (556, 50, 204, 4, 7.10, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (557, 50, 205, 5, 7.10, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (558, 50, 206, 6, 7.10, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (559, 50, 207, 7, 7.10, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (560, 50, 208, 8, 3.55, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (561, 50, 209, 9, 3.55, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (562, 50, 210, 10, 7.10, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (563, 50, 211, 11, 14.20, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (564, 50, 212, 12, 14.20, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (565, 50, 213, 13, 14.20, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (566, 50, 214, 14, 14.20, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (567, 50, 215, 15, 106.50, '2026-02-23 03:15:11');
INSERT INTO `exam_question` VALUES (568, 50, 216, 16, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (569, 50, 217, 17, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (570, 50, 218, 18, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (571, 50, 219, 19, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (572, 50, 220, 20, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (573, 50, 221, 21, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (574, 50, 222, 22, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (575, 50, 223, 23, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (576, 50, 224, 24, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (577, 50, 225, 25, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (578, 50, 226, 26, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (579, 50, 227, 27, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (580, 50, 228, 28, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (581, 50, 229, 29, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (582, 50, 230, 30, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (583, 50, 231, 31, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (584, 50, 232, 32, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (585, 50, 233, 33, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (586, 50, 234, 34, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (587, 50, 235, 35, 3.55, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (588, 50, 236, 36, 3.55, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (589, 50, 237, 37, 3.55, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (590, 50, 238, 38, 3.55, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (591, 50, 239, 39, 3.55, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (592, 50, 240, 40, 3.55, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (593, 50, 241, 41, 3.55, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (594, 50, 242, 42, 3.55, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (595, 50, 243, 43, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (596, 50, 244, 44, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (597, 50, 245, 45, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (598, 50, 246, 46, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (599, 50, 247, 47, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (600, 50, 248, 48, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (601, 50, 249, 49, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (602, 50, 250, 50, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (603, 50, 251, 51, 7.10, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (604, 50, 252, 52, 14.20, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (605, 50, 253, 53, 14.20, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (606, 50, 254, 54, 14.20, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (607, 50, 255, 55, 14.20, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (608, 50, 256, 56, 14.20, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (609, 50, 257, 57, 14.20, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (610, 50, 258, 58, 14.20, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (611, 50, 259, 59, 14.20, '2026-02-23 03:20:18');
INSERT INTO `exam_question` VALUES (612, 999, 216, 1, 10.00, '2026-02-23 19:41:48');
INSERT INTO `exam_question` VALUES (613, 999, 217, 2, 10.00, '2026-02-23 19:41:48');
INSERT INTO `exam_question` VALUES (614, 999, 218, 3, 10.00, '2026-02-23 19:41:48');
INSERT INTO `exam_question` VALUES (615, 42, 247, 16, 7.10, '2026-02-27 23:45:58');
INSERT INTO `exam_question` VALUES (616, 42, 244, 17, 7.10, '2026-02-27 23:46:04');
INSERT INTO `exam_question` VALUES (617, 42, 240, 18, 3.55, '2026-02-27 23:46:07');
INSERT INTO `exam_question` VALUES (618, 1001, 259, 1, 14.20, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (619, 1001, 258, 2, 14.20, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (620, 1001, 257, 3, 14.20, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (621, 1001, 256, 4, 14.20, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (622, 1001, 255, 5, 14.20, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (623, 1001, 254, 6, 14.20, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (624, 1001, 253, 7, 14.20, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (625, 1001, 252, 8, 14.20, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (626, 1001, 251, 9, 7.10, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (627, 1001, 250, 10, 7.10, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (628, 1001, 249, 11, 7.10, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (629, 1001, 248, 12, 7.10, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (630, 1001, 247, 13, 7.10, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (631, 1001, 246, 14, 7.10, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (632, 1001, 245, 15, 7.10, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (633, 1001, 244, 16, 7.10, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (634, 1001, 243, 17, 7.10, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (635, 1001, 242, 18, 3.55, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (636, 1001, 241, 19, 3.55, '2026-02-27 23:47:07');
INSERT INTO `exam_question` VALUES (637, 1001, 240, 20, 3.55, '2026-02-27 23:47:07');

-- ----------------------------
-- Table structure for exam_record
-- ----------------------------
DROP TABLE IF EXISTS `exam_record`;
CREATE TABLE `exam_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` bigint(20) NOT NULL COMMENT '考试ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `duration` int(11) NULL DEFAULT NULL COMMENT '实际用时(分钟)',
  `total_score` decimal(10, 2) NULL DEFAULT NULL COMMENT '总分',
  `obtained_score` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '得分',
  `correct_count` int(11) NULL DEFAULT 0 COMMENT '正确题数',
  `wrong_count` int(11) NULL DEFAULT 0 COMMENT '错误题数',
  `rank_position` int(11) NULL DEFAULT NULL COMMENT '排名',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'IN_PROGRESS' COMMENT '状态:IN_PROGRESS/COMPLETED/TIMEOUT',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `browser_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器信息',
  `cheat_warning_count` int(11) NULL DEFAULT 0 COMMENT '作弊警告次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_exam_student`(`exam_id` ASC, `student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1014 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_record
-- ----------------------------
INSERT INTO `exam_record` VALUES (1, 1, 2, '2026-02-18 09:00:00', '2026-02-18 11:00:00', '2026-02-18 11:00:00', 120, 100.00, 87.50, 35, 5, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `exam_record` VALUES (2, 2, 2, '2026-02-19 14:30:00', '2026-02-19 15:00:00', '2026-02-19 15:00:00', 30, 50.00, 38.00, 19, 6, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `exam_record` VALUES (3, 1, 2, '2026-02-20 10:00:00', '2026-02-20 11:55:00', '2026-02-20 11:55:00', 115, 100.00, 72.00, 29, 11, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `exam_record` VALUES (7, 50, 2, '2026-02-23 02:17:53', '2026-02-23 02:24:46', '2026-02-23 02:24:46', 6, 100.00, 0.00, 0, 0, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-23 02:17:53', '2026-02-23 02:17:53');
INSERT INTO `exam_record` VALUES (8, 58, 2, '2026-02-23 02:25:30', '2026-02-23 02:26:01', '2026-02-23 02:26:01', 0, 100.00, 0.00, 0, 0, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-23 02:25:30', '2026-02-23 02:25:30');
INSERT INTO `exam_record` VALUES (9, 58, 2, '2026-02-23 02:26:12', '2026-02-23 02:36:36', '2026-02-23 02:36:36', 10, 100.00, 0.00, 0, 9, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-23 02:26:12', '2026-02-23 02:26:12');
INSERT INTO `exam_record` VALUES (999, 999, 2, '2026-02-23 19:41:48', '2026-02-23 19:42:11', '2026-02-23 19:42:11', 0, 100.00, 20.00, 2, 1, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-23 19:41:48', '2026-02-23 19:41:48');
INSERT INTO `exam_record` VALUES (1000, 999, 2, '2026-02-23 20:10:40', NULL, NULL, NULL, 100.00, 0.00, 0, 0, NULL, 'IN_PROGRESS', NULL, NULL, 0, '2026-02-23 20:10:40', '2026-02-23 20:10:40');
INSERT INTO `exam_record` VALUES (1001, 42, 2, '2026-02-23 20:25:38', '2026-02-26 21:58:58', '2026-02-26 21:58:58', 4413, 710.00, 0.00, 0, 15, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-23 20:25:38', '2026-02-23 20:25:38');
INSERT INTO `exam_record` VALUES (1002, 42, 2, '2026-02-27 00:28:41', NULL, NULL, NULL, 710.00, 0.00, 0, 0, NULL, 'IN_PROGRESS', NULL, NULL, 0, '2026-02-27 00:28:41', '2026-02-27 00:28:41');
INSERT INTO `exam_record` VALUES (1003, 1, 4, '2026-02-20 14:00:00', '2026-02-20 16:00:00', '2026-02-20 16:00:00', 120, 100.00, 78.00, 31, 9, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-28 16:08:14', '2026-02-28 16:08:14');
INSERT INTO `exam_record` VALUES (1004, 1, 5, '2026-02-19 10:00:00', '2026-02-19 12:00:00', '2026-02-19 12:00:00', 120, 100.00, 82.50, 33, 7, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-28 16:08:14', '2026-02-28 16:08:14');
INSERT INTO `exam_record` VALUES (1005, 2, 6, '2026-02-21 15:30:00', '2026-02-21 16:00:00', '2026-02-21 16:00:00', 30, 50.00, 42.00, 21, 4, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-28 16:08:14', '2026-02-28 16:08:14');
INSERT INTO `exam_record` VALUES (1006, 44, 2, '2026-03-02 01:52:17', NULL, NULL, NULL, 710.00, 0.00, 0, 0, NULL, 'IN_PROGRESS', NULL, NULL, 0, '2026-03-02 01:52:17', '2026-03-02 01:52:17');
INSERT INTO `exam_record` VALUES (1007, 1, 2, '2026-02-24 03:05:44', '2026-02-24 05:05:44', '2026-02-24 05:05:44', 120, 100.00, 85.00, 17, 3, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-24 03:05:44', '2026-03-02 03:05:44');
INSERT INTO `exam_record` VALUES (1008, 1, 2, '2026-02-25 03:05:44', '2026-02-25 04:55:44', '2026-02-25 04:55:44', 110, 100.00, 78.00, 15, 5, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-25 03:05:44', '2026-03-02 03:05:44');
INSERT INTO `exam_record` VALUES (1009, 2, 2, '2026-02-26 03:05:44', '2026-02-26 03:35:44', '2026-02-26 03:35:44', 30, 50.00, 42.00, 21, 9, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-26 03:05:44', '2026-03-02 03:05:44');
INSERT INTO `exam_record` VALUES (1010, 1, 2, '2026-02-27 03:05:44', '2026-02-27 05:10:44', '2026-02-27 05:10:44', 125, 100.00, 92.00, 18, 2, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-27 03:05:44', '2026-03-02 03:05:44');
INSERT INTO `exam_record` VALUES (1011, 2, 2, '2026-02-28 03:05:44', '2026-02-28 03:33:44', '2026-02-28 03:33:44', 28, 50.00, 45.00, 22, 8, NULL, 'COMPLETED', NULL, NULL, 0, '2026-02-28 03:05:44', '2026-03-02 03:05:44');
INSERT INTO `exam_record` VALUES (1012, 1, 2, '2026-03-01 03:05:44', '2026-03-01 05:00:44', '2026-03-01 05:00:44', 115, 100.00, 88.00, 17, 3, NULL, 'COMPLETED', NULL, NULL, 0, '2026-03-01 03:05:44', '2026-03-02 03:05:44');
INSERT INTO `exam_record` VALUES (1013, 2, 2, '2026-03-02 03:05:44', '2026-03-02 03:37:44', '2026-03-02 03:37:44', 32, 50.00, 48.00, 24, 6, NULL, 'COMPLETED', NULL, NULL, 0, '2026-03-02 03:05:44', '2026-03-02 03:05:44');

-- ----------------------------
-- Table structure for group_member
-- ----------------------------
DROP TABLE IF EXISTS `group_member`;
CREATE TABLE `group_member`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `group_id` bigint(20) NOT NULL COMMENT '小组ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `member_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MEMBER' COMMENT '角色:LEADER/ADMIN/MEMBER',
  `join_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_group_id`(`group_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '小组成员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_member
-- ----------------------------
INSERT INTO `group_member` VALUES (1, 1, 2, 'LEADER', '2026-02-01 10:00:00');
INSERT INTO `group_member` VALUES (2, 2, 2, 'MEMBER', '2026-02-10 14:30:00');
INSERT INTO `group_member` VALUES (3, 3, 2, 'MEMBER', '2026-02-15 16:00:00');
INSERT INTO `group_member` VALUES (4, 4, 2, 'LEADER', '2026-02-27 03:07:02');

-- ----------------------------
-- Table structure for homework
-- ----------------------------
DROP TABLE IF EXISTS `homework`;
CREATE TABLE `homework`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '作业描述',
  `teacher_id` bigint(20) NOT NULL COMMENT '教师ID',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '等级',
  `homework_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NORMAL' COMMENT '类型:NORMAL/EXAM/PRACTICE',
  `deadline` datetime NULL DEFAULT NULL COMMENT '截止时间',
  `total_score` decimal(5, 2) NULL DEFAULT 100.00 COMMENT '总分',
  `attachment_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件URL',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态:0禁用1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_deadline`(`deadline` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of homework
-- ----------------------------
INSERT INTO `homework` VALUES (1, '四级阅读理解专项练习Unit 5', '请认真完成教材Unit 5的阅读理解练习题，注意总结生词和长难句。要求：\n1. 完成所有题目并标注答案\n2. 整理不认识的生词\n3. 分析2-3个长难句的结构', 3, 'CET4', 'NORMAL', '2026-02-19 23:59:59', 100.00, NULL, 1, '2026-02-23 20:44:16', '2026-02-23 20:44:16', 0);
INSERT INTO `homework` VALUES (2, '四级听力Section A训练', '完成四级听力Section A部分练习，共8道题。\n要求：\n1. 听录音完成题目\n2. 听写关键句子\n3. 总结听力技巧', 3, 'CET4', 'NORMAL', '2026-02-25 23:59:59', 100.00, '/files/homework/listening_section_a.mp3', 1, '2026-02-23 20:44:16', '2026-02-23 20:44:16', 0);
INSERT INTO `homework` VALUES (3, '四级作文练习：The Importance of Time Management', '请以\"The Importance of Time Management\"为题写一篇不少于120词的英语作文。\n要求：\n1. 结构完整（引言、正文、结论）\n2. 观点明确，论据充分\n3. 语法正确，用词恰当\n4. 字数120-180词', 3, 'CET4', 'NORMAL', '2026-02-28 23:59:59', 100.00, NULL, 1, '2026-02-23 20:44:16', '2026-02-23 20:44:16', 0);
INSERT INTO `homework` VALUES (4, '四级段落翻译练习3', '将以下中文段落翻译成英文：\n\n\"中国的互联网社区是全世界发展最快的。2010年，中国约有4.2亿网民，而且人数还在迅速增长。互联网的日渐流行带来了重大的社会变化。\"\n\n要求：\n1. 译文准确流畅\n2. 注意专业术语的翻译\n3. 语法结构正确', 3, 'CET4', 'NORMAL', '2026-02-24 23:59:59', 100.00, NULL, 1, '2026-02-23 20:44:16', '2026-02-23 20:44:16', 0);
INSERT INTO `homework` VALUES (5, '四级核心词汇Unit 1-3测试', '完成教材Unit 1-3的词汇测试题，共50道选择题。\n要求：\n1. 独立完成，不查词典\n2. 记录错题\n3. 整理词汇笔记', 3, 'CET4', 'NORMAL', '2026-02-20 23:59:59', 100.00, NULL, 1, '2026-02-23 20:44:16', '2026-02-23 20:44:16', 0);
INSERT INTO `homework` VALUES (6, '四级综合模拟练习（阅读+翻译）', '完成一套完整的四级阅读和翻译练习。\n包括：\n1. 长篇阅读1篇\n2. 仔细阅读2篇\n3. 段落翻译1段\n\n请按考试时间要求完成（建议40分钟）', 3, 'CET4', 'NORMAL', '2026-03-02 23:59:59', 150.00, '/files/homework/comprehensive_practice.pdf', 1, '2026-02-23 20:44:16', '2026-02-23 20:44:16', 0);

-- ----------------------------
-- Table structure for homework_submission
-- ----------------------------
DROP TABLE IF EXISTS `homework_submission`;
CREATE TABLE `homework_submission`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `homework_id` bigint(20) NOT NULL COMMENT '作业ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '作业内容',
  `attachment_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件URL',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '得分',
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '教师反馈',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态:PENDING/SUBMITTED/GRADED/RETURNED',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_homework_id`(`homework_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业提交表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of homework_submission
-- ----------------------------
INSERT INTO `homework_submission` VALUES (2, 1, 2, '作业内容：完成Unit 5的阅读理解练习\n\n【题目答案】\n1. C - 根据第一段可知...\n2. A - 文章第二段明确指出...\n3. D - 推理判断题，综合全文...\n4. B - 细节题，定位到第三段...\n5. C - 主旨大意题...\n\n【生词整理】\n1. comprehensive - adj. 综合的，全面的\n2. phenomenon - n. 现象\n3. distinguish - v. 区分，辨别\n4. crucial - adj. 关键的，重要的\n5. enhance - v. 提高，增强\n\n【长难句分析】\n1. \"It is widely acknowledged that reading comprehension plays a crucial role in language learning.\"\n   结构：It is + 过去分词 + that从句（形式主语句型）\n   翻译：人们普遍认为阅读理解在语言学习中起着关键作用。\n\n2. \"The ability to distinguish between main ideas and supporting details enables students to grasp the essence of the text.\"\n   结构：主语(The ability to do) + 谓语(enables) + 宾语 + 宾补(to do)\n   翻译：区分主要观点和支持细节的能力使学生能够抓住文章的本质。', NULL, '2026-02-19 18:30:00', 88.00, '完成得很好！阅读理解能力有明显提升。\n\n【优点】\n1. 答题准确率高，5道题对4道\n2. 生词整理认真，注意了词性和例句\n3. 长难句分析到位，结构划分清晰\n\n【改进建议】\n1. 第5题主旨大意题理解有偏差，建议多关注段落主题句\n2. 生词可以补充例句，帮助记忆\n3. 继续保持这种学习态度！\n\n【下次作业重点】\n加强推理判断题的训练', 'GRADED', '2026-02-23 20:44:16', '2026-02-23 20:44:16');
INSERT INTO `homework_submission` VALUES (3, 2, 2, '【听力练习答案】\nSection A - Short Conversations\n1. B\n2. C\n3. A\n4. D\n5. B\n6. C\n7. A\n8. D\n\n【关键句听写】\n1. \"I\'m afraid I can\'t make it to the meeting tomorrow.\"\n2. \"Would you mind if I borrowed your notes?\"\n3. \"The library will be closed for renovation next week.\"\n\n【听力技巧总结】\n1. 注意对话中的转折词：but, however, although\n2. 关注说话人的语气和态度\n3. 抓住关键信息词，如时间、地点、人物\n4. 排除干扰选项，避免主观臆断', NULL, '2026-02-22 20:15:00', NULL, NULL, 'SUBMITTED', '2026-02-23 20:44:16', '2026-02-23 20:44:16');
INSERT INTO `homework_submission` VALUES (4, 5, 2, '【词汇测试答案】\n1-10题：C B A D B C A B D C\n11-20题：A D B C A B D C B A\n21-30题：B C D A B C D A B C\n31-40题：D B A C D B A C D B\n41-50题：C A B D C A B D C A\n\n【错题记录】\n第5题：误选A，正确答案B - distinct\n第18题：误选D，正确答案C - comprehend\n第27题：误选A，正确答案D - sufficient\n\n【词汇笔记】\n因为过期提交，部分笔记未整理完整...', NULL, '2026-02-21 15:40:00', NULL, NULL, 'SUBMITTED', '2026-02-23 20:44:16', '2026-02-23 20:44:16');
INSERT INTO `homework_submission` VALUES (5, 1, 4, '作业内容：完成了阅读理解...（其他学生的答案）', NULL, '2026-02-19 17:00:00', 92.00, '非常优秀！', 'GRADED', '2026-02-23 20:44:16', '2026-02-23 20:44:16');
INSERT INTO `homework_submission` VALUES (6, 1, 5, '作业内容：阅读理解答案...', NULL, '2026-02-19 19:30:00', 75.00, '基本掌握，需要加强细节理解', 'GRADED', '2026-02-23 20:44:16', '2026-02-23 20:44:16');
INSERT INTO `homework_submission` VALUES (7, 2, 4, '听力答案：1.B 2.C 3.A...', NULL, '2026-02-22 18:00:00', NULL, NULL, 'SUBMITTED', '2026-02-23 20:44:16', '2026-02-23 20:44:16');
INSERT INTO `homework_submission` VALUES (8, 2, 5, '听力练习完成...', NULL, '2026-02-23 10:30:00', NULL, NULL, 'SUBMITTED', '2026-02-23 20:44:16', '2026-02-23 20:44:16');
INSERT INTO `homework_submission` VALUES (9, 3, 4, 'Time Management is crucial in our daily life...（作文内容）', NULL, '2026-02-26 14:20:00', 85.00, '文章结构清晰，观点明确', 'GRADED', '2026-02-23 20:44:16', '2026-02-23 20:44:16');
INSERT INTO `homework_submission` VALUES (10, 6, 2, '1111', '', '2026-02-23 21:22:26', NULL, NULL, 'SUBMITTED', '2026-02-23 21:18:12', '2026-02-23 21:18:12');

-- ----------------------------
-- Table structure for learning_path
-- ----------------------------
DROP TABLE IF EXISTS `learning_path`;
CREATE TABLE `learning_path`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '路径ID',
  `path_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '路径名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '路径描述',
  `target_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标等级',
  `difficulty` tinyint(4) NULL DEFAULT 1 COMMENT '难度:1初级2中级3高级',
  `estimated_days` int(11) NULL DEFAULT NULL COMMENT '预计天数',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面URL',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习路径表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_path
-- ----------------------------
INSERT INTO `learning_path` VALUES (1, '四级30天冲刺计划', '30天系统复习四级考试重点内容', 'CET4', 2, 30, '/paths/cet4_30days.jpg', 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `learning_path` VALUES (2, '六级听力突破', '专注提升六级听力能力', 'CET6', 3, 45, '/paths/cet6_listening.jpg', 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `learning_path` VALUES (3, '词汇速记计划', '21天掌握核心词汇', 'CET4', 1, 21, '/paths/vocabulary.jpg', 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `learning_path` VALUES (4, '四级冲刺学习路径', '专为四级考试设计的完整学习路径，包含词汇、听力、阅读、写作、翻译五大模块', 'CET4', 1, 60, NULL, 1, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `learning_path` VALUES (5, '六级强化训练路径', '六级高分突破学习路径，重点提升综合应用能力', 'CET6', 1, 90, NULL, 1, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);

-- ----------------------------
-- Table structure for learning_path_node
-- ----------------------------
DROP TABLE IF EXISTS `learning_path_node`;
CREATE TABLE `learning_path_node`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '节点ID',
  `path_id` bigint(20) NOT NULL COMMENT '路径ID',
  `node_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节点名称',
  `node_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '节点类型:VIDEO/ARTICLE/QUIZ/PRACTICE',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '节点内容',
  `resource_id` bigint(20) NULL DEFAULT NULL COMMENT '关联资源ID',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序',
  `estimated_minutes` int(11) NULL DEFAULT NULL COMMENT '预计时长(分钟)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_path_id`(`path_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习路径节点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_path_node
-- ----------------------------
INSERT INTO `learning_path_node` VALUES (1, 1, '第1天：词汇Day1', 'VIDEO', '学习核心词汇100个', 1, 1, 60, '2026-02-22 04:10:46');
INSERT INTO `learning_path_node` VALUES (2, 1, '第2天：听力基础', 'VIDEO', '听力基础训练', 3, 2, 45, '2026-02-22 04:10:46');
INSERT INTO `learning_path_node` VALUES (3, 1, '第3天：阅读技巧', 'VIDEO', '阅读理解方法', 4, 3, 60, '2026-02-22 04:10:46');
INSERT INTO `learning_path_node` VALUES (4, 1, '第4天：写作入门', 'VIDEO', '写作基础训练', 5, 4, 50, '2026-02-22 04:10:46');
INSERT INTO `learning_path_node` VALUES (5, 1, '第5天：模拟测试', 'QUIZ', '第一周测试', 1, 5, 120, '2026-02-22 04:10:46');
INSERT INTO `learning_path_node` VALUES (6, 2, '基础听力训练', 'ARTICLE', 'BBC新闻听力材料', 6, 1, 30, '2026-02-22 04:10:46');
INSERT INTO `learning_path_node` VALUES (7, 2, '进阶听力练习', 'VIDEO', '听力技巧提升', 2, 2, 40, '2026-02-22 04:10:46');
INSERT INTO `learning_path_node` VALUES (8, 2, '听力模拟测试', 'QUIZ', '听力专项测试', 2, 3, 60, '2026-02-22 04:10:46');
INSERT INTO `learning_path_node` VALUES (9, 1, '四级核心词汇', 'VOCABULARY', '掌握四级考试必备词汇1500个', 4, 1, 300, '2026-02-28 16:08:14');
INSERT INTO `learning_path_node` VALUES (10, 1, '四级听力技巧', 'LISTENING', '系统学习四级听力解题方法', 1, 2, 240, '2026-02-28 16:08:14');
INSERT INTO `learning_path_node` VALUES (11, 1, '四级阅读理解', 'READING', '提升阅读速度和理解能力', NULL, 3, 360, '2026-02-28 16:08:14');
INSERT INTO `learning_path_node` VALUES (12, 1, '四级写作模板', 'WRITING', '掌握常用写作结构和高分句型', 3, 4, 180, '2026-02-28 16:08:14');
INSERT INTO `learning_path_node` VALUES (13, 1, '四级翻译技巧', 'TRANSLATION', '学习中译英的基本方法和技巧', 5, 5, 120, '2026-02-28 16:08:14');

-- ----------------------------
-- Table structure for learning_resource
-- ----------------------------
DROP TABLE IF EXISTS `learning_resource`;
CREATE TABLE `learning_resource`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `resource_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源名称',
  `resource_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源类型:视频/音频/文档/图片',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '等级',
  `skill_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '技能类型',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '分类ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '文件URL',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面URL',
  `file_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小(字节)',
  `duration` int(11) NULL DEFAULT NULL COMMENT '时长(秒)',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览次数',
  `download_count` int(11) NULL DEFAULT 0 COMMENT '下载次数',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞数',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_resource_type`(`resource_type` ASC) USING BTREE,
  INDEX `idx_level`(`level` ASC) USING BTREE,
  INDEX `idx_skill_type`(`skill_type` ASC) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_resource
-- ----------------------------
INSERT INTO `learning_resource` VALUES (1, '四级词汇精讲', '视频', 'CET4', '', NULL, '四级核心词汇详解', '', '/uploads/images/2026/02/28/e2a76a803e5b454388684546e5e7132e.png', NULL, 45, 19, 0, 0, NULL, 3, 1, '2026-02-22 04:10:46', '2026-02-28 11:14:10', 0);
INSERT INTO `learning_resource` VALUES (2, '听力技巧提升', '音频', 'CET6', NULL, NULL, '提升听力理解能力', '', NULL, NULL, 30, 5, 0, 0, NULL, 3, 0, '2026-02-22 04:10:46', '2026-02-28 11:44:01', 0);
INSERT INTO `learning_resource` VALUES (3, '四级听力短对话技巧', '视频', 'CET4', '听力', NULL, '详解四级听力短对话的解题技巧和常见场景', '/resources/video/cet4_listening_tips.mp4', '/resources/covers/listening.jpg', NULL, 1200, 159, 0, 23, NULL, 3, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `learning_resource` VALUES (4, '四级阅读理解方法', '视频', 'CET4', '阅读', NULL, '教你快速定位阅读答案的方法', '/resources/video/cet4_reading_methods.mp4', '/resources/covers/reading.jpg', NULL, 1800, 235, 0, 45, NULL, 3, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `learning_resource` VALUES (5, '四级写作模板精讲', '视频', 'CET4', '写作', NULL, '常用写作模板和高分句型', '/resources/video/cet4_writing_templates.mp4', '/resources/covers/writing.jpg', NULL, 1500, 190, 0, 34, NULL, 3, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `learning_resource` VALUES (6, 'BBC新闻听力练习', '音频', 'CET6', '听力', NULL, '精选BBC新闻片段，提升听力理解能力', '/resources/audio/bbc_news_01.mp3', '/resources/covers/bbc.jpg', NULL, 600, 270, 0, 56, NULL, 3, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `learning_resource` VALUES (7, 'VOA慢速英语', '音频', 'CET4', '听力', NULL, '适合四级水平的VOA慢速英语材料', '/resources/audio/voa_slow_01.mp3', '/resources/covers/voa.jpg', NULL, 480, 313, 0, 67, NULL, 3, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `learning_resource` VALUES (8, '四级核心词汇5500', '文档', 'CET4', '词汇', NULL, '四级考试必备核心词汇表', '/resources/docs/cet4_vocabulary_5500.pdf', '/resources/covers/vocab.jpg', NULL, NULL, 445, 0, 89, NULL, 3, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `learning_resource` VALUES (9, '六级语法知识点汇总', '文档', 'CET6', '语法', NULL, '六级常考语法点详解', '/resources/docs/cet6_grammar.pdf', '/resources/covers/grammar.jpg', NULL, NULL, 298, 0, 72, NULL, 3, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `learning_resource` VALUES (10, '英语作文万能句型', '文档', 'CET4', '写作', NULL, '100个作文加分句型', '/resources/docs/writing_sentences.pdf', '/resources/covers/sentences.jpg', NULL, NULL, 523, 0, 134, NULL, 3, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `learning_resource` VALUES (11, '四级听力技巧全攻略', '视频', 'CET4', NULL, NULL, '系统讲解四级听力解题技巧和方法', '', NULL, NULL, 120, 2, 0, 0, NULL, 3, 1, '2026-02-28 14:55:44', '2026-03-02 01:51:46', 0);
INSERT INTO `learning_resource` VALUES (12, '六级阅读高分秘籍', '文档', 'CET6', NULL, NULL, '六级阅读理解高分策略和真题解析', '', NULL, NULL, 80, 0, 0, 0, NULL, 3, 1, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `learning_resource` VALUES (13, '英语写作万能模板', '文档', 'CET4', NULL, NULL, '四级写作常用模板和高分句型', '', NULL, NULL, 60, 0, 0, 0, NULL, 3, 1, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `learning_resource` VALUES (14, '词汇记忆魔法', '音频', 'CET4', NULL, NULL, '科学记忆四级核心词汇的方法', '', NULL, NULL, 45, 0, 0, 0, NULL, 3, 1, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `learning_resource` VALUES (15, '翻译技巧实战', '视频', 'CET6', NULL, NULL, '六级翻译技巧和常见话题解析', '', NULL, NULL, 90, 0, 0, 0, NULL, 3, 1, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `learning_resource` VALUES (16, '四级听力技巧全攻略', '视频', 'CET4', NULL, NULL, '系统讲解四级听力解题技巧和方法', '', NULL, NULL, 120, 12, 0, 0, NULL, 3, 1, '2026-02-28 16:08:14', '2026-03-02 01:51:53', 0);
INSERT INTO `learning_resource` VALUES (17, '六级阅读高分秘籍', '文档', 'CET6', NULL, NULL, '六级阅读理解高分策略和真题解析', '', NULL, NULL, 80, 0, 0, 0, NULL, 3, 1, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `learning_resource` VALUES (18, '英语写作万能模板', '文档', 'CET4', NULL, NULL, '四级写作常用模板和高分句型', '', NULL, NULL, 60, 10, 0, 0, NULL, 3, 1, '2026-02-28 16:08:14', '2026-03-02 01:51:46', 0);
INSERT INTO `learning_resource` VALUES (19, '词汇记忆魔法', '音频', 'CET4', NULL, NULL, '科学记忆四级核心词汇的方法', '', NULL, NULL, 45, 5, 0, 0, NULL, 3, 1, '2026-02-28 16:08:14', '2026-03-02 01:51:52', 0);
INSERT INTO `learning_resource` VALUES (20, '翻译技巧实战', '视频', 'CET6', NULL, NULL, '六级翻译技巧和常见话题解析', '', NULL, NULL, 90, 0, 0, 0, NULL, 3, 1, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `learning_resource` VALUES (21, '四级词汇精讲', '视频', 'CET4', NULL, NULL, '四级核心词汇详解', '', NULL, NULL, 45, 0, 0, 0, NULL, 3, 1, '2026-03-02 03:05:44', '2026-03-02 03:05:44', 0);
INSERT INTO `learning_resource` VALUES (22, '听力技巧提升', '音频', 'CET6', NULL, NULL, '提升听力理解能力', '', NULL, NULL, 30, 0, 0, 0, NULL, 3, 1, '2026-03-02 03:05:44', '2026-03-02 03:05:44', 0);

-- ----------------------------
-- Table structure for passage
-- ----------------------------
DROP TABLE IF EXISTS `passage`;
CREATE TABLE `passage`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章内容',
  `passage_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章类型：CLOZE-完形填空, READING-阅读理解',
  `difficulty` tinyint(4) NULL DEFAULT 1 COMMENT '难度等级：1-简单, 2-中等, 3-困难',
  `word_count` int(11) NULL DEFAULT 0 COMMENT '字数',
  `topic` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主题/话题',
  `keywords` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关键词',
  `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`passage_type` ASC) USING BTREE,
  INDEX `idx_difficulty`(`difficulty` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章表-用于完形填空和阅读理解' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of passage
-- ----------------------------
INSERT INTO `passage` VALUES (1, '在线学习的发展趋势', 'In recent years, the popularity of online learning has grown dramatically. More and more students are choosing to take courses through the internet rather than in traditional classrooms. This trend has been (1)______ by several factors, including the flexibility and convenience that online education offers.\n\nOne of the main advantages of online learning is that students can study at their own (2)______. They do not need to travel to a physical location, which saves both time and money. Additionally, online courses often provide access to a wide range of (3)______ from different universities and institutions around the world.\n\nHowever, online learning also has its challenges. Some students find it difficult to stay (4)______ without the structure of a traditional classroom. Others miss the social interaction that comes with face-to-face learning. Despite these challenges, the (5)______ of online education continues to grow as technology improves and more institutions offer high-quality online programs.', 'CLOZE', 2, 180, '在线教育', 'online learning,education,technology', 1, 1, '2026-02-23 02:52:31', '2026-02-23 02:52:31', 0);
INSERT INTO `passage` VALUES (2, 'News Report 1 - Tech Innovation', 'A major technology company announced yesterday that it has developed a new artificial intelligence system capable of diagnosing certain diseases with accuracy comparable to human doctors. The system was tested on over 10,000 patients and showed remarkable results in identifying early-stage cancers and heart conditions.', 'LISTENING', 2, 50, 'Technology,Healthcare', 'AI,diagnosis,innovation', 1, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `passage` VALUES (3, 'Conversation 1 - Campus Life', 'M: Hi Sarah, are you going to the career fair next week?\nW: Yes, I am. I heard several big companies will be there. I want to find an internship for next summer.\nM: Me too. I think we should prepare our resumes in advance.\nW: Good idea. Maybe we can also practice our interview skills together.', 'LISTENING', 2, 60, 'Campus,Career', 'career fair,internship,resume', 1, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `passage` VALUES (4, 'Passage 1 - Environmental Protection', 'Climate change has become one of the most pressing issues of our time. Scientists warn that without immediate action, global temperatures could rise by more than 2 degrees Celsius within the next few decades. This would lead to devastating consequences including rising sea levels, extreme weather events, and the loss of biodiversity. Governments around the world are now working together to reduce carbon emissions and promote renewable energy sources.', 'LISTENING', 2, 80, 'Environment', 'climate change,carbon emissions,renewable energy', 1, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `passage` VALUES (5, 'The Impact of Social Media on Modern Communication', 'Social media has revolutionized the way people communicate in the 21st century. Platforms like Facebook, Twitter, and Instagram have made it possible to connect with friends and family across vast distances instantly. However, this convenience comes with both benefits and drawbacks.\n\nOn the positive side, social media allows people to maintain relationships that might otherwise fade due to geographical separation. It also provides a platform for sharing ideas, promoting businesses, and organizing social movements. During emergencies, social media has proven invaluable for spreading information quickly and coordinating relief efforts.\n\nOn the negative side, excessive use of social media has been linked to mental health issues, including anxiety and depression. The constant comparison with others\' curated online personas can lead to feelings of inadequacy. Moreover, the spread of misinformation on these platforms has become a serious concern, affecting everything from public health to democratic processes.\n\nAs we move forward, it is crucial to find a balance in our use of social media. While embracing its benefits, we must also be aware of its potential harms and take steps to mitigate them. This includes practicing digital literacy, limiting screen time, and being mindful of the content we consume and share.', 'READING', 2, 200, 'Technology,Society', 'social media,communication,mental health', 1, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `passage` VALUES (6, 'The Future of Work in the Digital Age', 'The workplace is undergoing a dramatic transformation driven by technological advancement. Automation and artificial intelligence are changing not only how we work but also what kinds of jobs are available. Some experts predict that up to 40% of current jobs could be automated within the next two decades.\n\nThis shift presents both opportunities and challenges. On one hand, automation can eliminate tedious and dangerous tasks, freeing humans to focus on more creative and fulfilling work. On the other hand, it raises concerns about unemployment and the need for workers to acquire new skills.\n\nEducation systems must adapt to prepare students for this changing landscape. Rather than focusing solely on memorization and routine tasks, schools should emphasize critical thinking, creativity, and adaptability. Lifelong learning will become increasingly important as workers need to continually update their skills throughout their careers.\n\nGovernments and businesses also have a role to play in managing this transition. Policies supporting worker retraining, social safety nets, and equitable distribution of the benefits of automation will be essential to ensure that technological progress benefits society as a whole.', 'READING', 2, 180, 'Work,Technology', 'automation,AI,future of work,education', 1, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `passage` VALUES (7, 'News Report 2 - Education Reform', 'The Ministry of Education announced a comprehensive education reform plan yesterday. The new policy aims to reduce student workload and promote well-rounded development. Schools will be required to limit homework time and offer more extracurricular activities. The reform will be implemented gradually over the next three years.', 'LISTENING', 2, 55, 'Education,Policy', 'education reform,student workload', 1, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `passage` VALUES (8, 'News Report 3 - Space Exploration', 'NASA scientists have discovered evidence of water ice on the moon\'s surface. This breakthrough finding could be crucial for future lunar missions and long-term space exploration. The ice is located in permanently shadowed craters near the lunar poles. Scientists believe this discovery could support human settlements on the moon.', 'LISTENING', 2, 60, 'Space,Science', 'NASA,moon,water ice,space exploration', 1, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `passage` VALUES (9, 'Conversation 2 - Library Study', 'W: Excuse me, is this seat taken?\nM: No, feel free to sit down. Are you here to prepare for exams too?\nW: Yes, I have my finals next week. This library is much quieter than the dormitory.\nM: I agree. I come here every evening. By the way, the library closes at 10 PM on weekdays.\nW: Thanks for letting me know. I\'ll make sure to leave before then.', 'LISTENING', 2, 75, 'Campus,Study', 'library,exam preparation,study', 1, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `passage` VALUES (10, 'Conversation 3 - Restaurant Reservation', 'M: Good evening, I\'d like to make a reservation for dinner tomorrow.\nW: Certainly, sir. For how many people and what time?\nM: Four people at 7:30 PM, please.\nW: Let me check our availability... Yes, we have a table for four at 7:30. May I have your name and phone number?\nM: It\'s John Smith, and my number is 555-0123.\nW: Perfect, Mr. Smith. We look forward to seeing you tomorrow evening.', 'LISTENING', 2, 80, 'Daily Life,Service', 'restaurant,reservation,dining', 1, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `passage` VALUES (11, 'Passage 2 - Urban Transportation', 'Traffic congestion has become a serious problem in many major cities worldwide. As urban populations continue to grow, the demand for efficient transportation systems increases. Many cities are now investing in public transportation infrastructure, including subway systems, bus rapid transit, and bike-sharing programs. These initiatives not only help reduce traffic but also contribute to environmental protection by lowering carbon emissions. However, changing people\'s transportation habits remains a significant challenge.', 'LISTENING', 2, 90, 'Transportation,Urban', 'traffic,public transportation,urban planning', 1, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `passage` VALUES (12, 'Passage 3 - Healthy Lifestyle', 'Maintaining a healthy lifestyle has become increasingly important in modern society. Regular exercise, balanced nutrition, and adequate sleep are the three pillars of good health. Studies show that people who exercise at least three times a week have significantly better physical and mental health compared to those who lead sedentary lifestyles. Additionally, eating a variety of fruits and vegetables while limiting processed foods can help prevent many chronic diseases. Getting seven to eight hours of sleep each night is equally crucial for overall well-being.', 'LISTENING', 2, 95, 'Health,Lifestyle', 'exercise,nutrition,sleep,wellness', 1, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `passage` VALUES (13, 'The Power of Habit Formation', 'Habits shape our daily lives more than we realize. Research in behavioral psychology has shown that approximately 40% of our daily actions are driven by habits rather than conscious decisions. Understanding how habits form and how they can be changed is crucial for personal development and achieving long-term goals.\n\nThe habit formation process involves three key components: the cue, the routine, and the reward. A cue triggers the brain to initiate a behavior. The routine is the behavior itself. The reward is what the brain receives that helps it remember the habit loop in the future. For example, feeling stressed (cue) might lead to eating comfort food (routine), which provides temporary relief (reward).\n\nBreaking bad habits and forming new ones requires patience and consistency. Research suggests that it takes an average of 66 days for a new behavior to become automatic. The key is to start small and gradually build up. Rather than trying to completely overhaul your life overnight, focus on making tiny changes that you can sustain over time.\n\nEnvironmental design plays a crucial role in habit formation. By modifying your environment to make good habits easier and bad habits harder, you can significantly increase your chances of success. For instance, placing healthy snacks at eye level and hiding junk food can help promote better eating habits without requiring constant willpower.', 'READING', 2, 220, 'Psychology,Self-improvement', 'habits,behavior,psychology,personal development', 1, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `passage` VALUES (14, 'Artificial Intelligence in Healthcare', 'The integration of artificial intelligence into healthcare is revolutionizing medical diagnosis and treatment. AI systems can now analyze medical images with accuracy comparable to or exceeding that of human radiologists. This technology is particularly valuable in detecting early-stage diseases where subtle changes might be missed by the human eye.\n\nMachine learning algorithms are being trained on vast datasets of patient records to identify patterns and predict health outcomes. For example, AI can predict which patients are at high risk of developing certain conditions, allowing for early intervention. This predictive capability has the potential to shift healthcare from a reactive to a proactive model.\n\nHowever, the implementation of AI in healthcare raises important ethical questions. Patient privacy must be protected, and the decision-making process of AI systems must be transparent and explainable. There are also concerns about the potential reduction in human interaction in healthcare, which is an important aspect of patient care and comfort.\n\nDespite these challenges, the potential benefits of AI in healthcare are enormous. It can help address doctor shortages, reduce healthcare costs, and improve patient outcomes. As the technology continues to evolve, finding the right balance between AI assistance and human judgment will be crucial.', 'READING', 2, 210, 'Technology,Medicine', 'AI,healthcare,medical diagnosis,machine learning', 1, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `passage` VALUES (15, 'Sustainable Fashion Movement', 'The fashion industry is one of the world\'s largest polluters, responsible for significant carbon emissions and water consumption. However, a growing sustainable fashion movement is challenging traditional practices and promoting environmentally friendly alternatives. This shift is driven by increasing consumer awareness and concern about the environmental impact of fast fashion.\n\nSustainable fashion encompasses various practices, including using organic and recycled materials, implementing ethical labor practices, and creating durable, timeless designs rather than following fleeting trends. Some brands are adopting circular economy principles, where products are designed to be recycled or composted at the end of their life cycle.\n\nConsumer behavior plays a crucial role in this transformation. By choosing quality over quantity, buying second-hand clothing, and properly caring for garments to extend their lifespan, individuals can significantly reduce their fashion footprint. The rise of clothing rental services and swap events also demonstrates changing attitudes toward fashion consumption.\n\nThe transition to sustainable fashion is not without obstacles. Sustainable materials and ethical production methods often cost more, making eco-friendly options less accessible to budget-conscious consumers. Additionally, the complexity of global supply chains makes it difficult to ensure complete transparency and sustainability.', 'READING', 2, 200, 'Environment,Fashion', 'sustainable fashion,environment,consumerism,ethics', 1, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `passage` VALUES (16, 'The Science of Sleep', 'Sleep is essential for physical health, mental well-being, and cognitive function, yet many people chronically fail to get enough quality sleep. Understanding the science behind sleep can help us appreciate its importance and take steps to improve our sleep habits.\n\nDuring sleep, the brain goes through different stages, each serving specific functions. Non-REM sleep, which includes light and deep sleep stages, is crucial for physical restoration and memory consolidation. REM sleep, characterized by rapid eye movements and vivid dreams, is important for emotional processing and creative thinking. A complete sleep cycle lasts about 90 minutes, and we typically go through 4-6 cycles per night.\n\nSleep deprivation has serious consequences. In the short term, it impairs attention, judgment, and reaction time. Chronic sleep loss is associated with increased risk of obesity, diabetes, cardiovascular disease, and mental health problems. It also weakens the immune system, making us more susceptible to infections.\n\nImproving sleep quality requires establishing good sleep hygiene. This includes maintaining a consistent sleep schedule, creating a comfortable sleep environment, limiting screen time before bed, avoiding caffeine and heavy meals in the evening, and managing stress through relaxation techniques. While individual sleep needs vary, most adults require 7-9 hours of sleep per night for optimal functioning.', 'READING', 2, 230, 'Health,Science', 'sleep,health,circadian rhythm,sleep hygiene', 1, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);

-- ----------------------------
-- Table structure for post_collection
-- ----------------------------
DROP TABLE IF EXISTS `post_collection`;
CREATE TABLE `post_collection`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `post_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_user`(`post_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子收藏记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_collection
-- ----------------------------

-- ----------------------------
-- Table structure for post_like
-- ----------------------------
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `post_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_user`(`post_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子点赞记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_like
-- ----------------------------

-- ----------------------------
-- Table structure for post_reply
-- ----------------------------
DROP TABLE IF EXISTS `post_reply`;
CREATE TABLE `post_reply`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `post_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父回复ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回复内容',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞数',
  `is_best_answer` tinyint(4) NULL DEFAULT 0 COMMENT '是否最佳答案',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子回复表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_reply
-- ----------------------------
INSERT INTO `post_reply` VALUES (1, 1, 3, 0, '感谢分享！你的学习方法很实用，我也要试试~', 5, 0, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `post_reply` VALUES (2, 1, 2, 1, '一起加油！坚持就是胜利💪', 2, 0, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `post_reply` VALUES (3, 2, 3, 0, '我的经验是：先预读题目，带着问题去听，效果会好很多', 8, 0, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `post_reply` VALUES (4, 2, 2, 3, '好方法！明天就试试，谢谢老师~', 1, 0, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `post_reply` VALUES (5, 3, 3, 0, '词根词缀法确实很有效，推荐配合艾宾浩斯遗忘曲线复习', 6, 0, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `post_reply` VALUES (6, 5, 2, 0, '2222', 0, 0, '2026-03-02 01:52:31', '2026-03-02 01:52:31', 0);

-- ----------------------------
-- Table structure for pronunciation_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `pronunciation_evaluation`;
CREATE TABLE `pronunciation_evaluation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `text_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '朗读文本',
  `audio_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '录音URL',
  `total_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '总分',
  `accuracy_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '准确度分',
  `fluency_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '流利度分',
  `intonation_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '语调分',
  `speed_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '语速分',
  `recognized_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '识别文本',
  `error_details` json NULL COMMENT '错误详情(JSON格式)',
  `suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '改进建议',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '发音评测记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pronunciation_evaluation
-- ----------------------------
INSERT INTO `pronunciation_evaluation` VALUES (1, 2, 'Hello, my name is Zhang San. I am a second-year student majoring in Computer Science.', '/audio/pronunciation/2_20260220_001.mp3', 86.00, 88.00, 85.00, 84.00, 87.00, 'Hello, my name is Zhang San. I am a second-year student majoring in Computer Science.', NULL, '发音评价：\n1. 整体发音清晰准确\n2. \"majoring\"一词的重音位置可以更标准\n3. 语速适中，流利度良好\n4. 建议：注意句子的升降调，使语音更自然', '2026-02-22 04:10:46');
INSERT INTO `pronunciation_evaluation` VALUES (2, 2, 'The weather is nice today. Would you like to go for a walk?', '/audio/pronunciation/2_20260219_001.mp3', 78.00, 80.00, 76.00, 78.00, 78.00, 'The weather is nice today. Would you like to go for a walk?', NULL, '发音评价：\n1. \"weather\"的/ð/音发音不够准确\n2. 问句的语调上扬可以更明显\n3. 整体流利度有待提高\n4. 建议：多练习连读和语调变化', '2026-02-22 04:10:46');
INSERT INTO `pronunciation_evaluation` VALUES (3, 2, 'Reading books is one of my favorite hobbies.', '/audio/pronunciation/2_20260218_001.mp3', 92.00, 94.00, 91.00, 90.00, 93.00, 'Reading books is one of my favorite hobbies.', NULL, '发音评价：\n1. 发音非常标准，语音流畅\n2. 语调自然，节奏把握好\n3. 继续保持！\n4. 建议：可以尝试更复杂的句子', '2026-02-22 04:10:46');

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `question_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题型:单选/多选/填空/问答/听力/作文',
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '题目内容',
  `passage_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的文章ID',
  `passage` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '阅读材料/完形填空文章',
  `options` json NULL COMMENT '选项(JSON格式)',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '答案',
  `analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '解析',
  `difficulty` tinyint(4) NULL DEFAULT 1 COMMENT '难度:1简单2中等3困难',
  `score` decimal(5, 2) NULL DEFAULT 1.00 COMMENT '分值',
  `audio_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '音频URL',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频URL',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `keywords` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关键词',
  `usage_count` int(11) NULL DEFAULT 0 COMMENT '使用次数',
  `correct_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '正确率',
  `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态:0禁用1正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_question_type`(`question_type` ASC) USING BTREE,
  INDEX `idx_difficulty`(`difficulty` ASC) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_passage_id`(`passage_id` ASC) USING BTREE,
  INDEX `idx_status_create_time`(`status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 263 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, 1, 'SINGLE_CHOICE', '听力题目示例', 'What does the man mean?', NULL, NULL, '[\"A. He likes coffee\", \"B. He prefers tea\", \"C. He wants water\", \"D. He is not thirsty\"]', 'B', '根据对话内容，男士表示更喜欢茶', 1, 1.00, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-22 04:10:46', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (2, 2, 'SINGLE_CHOICE', '阅读理解示例', 'What is the main idea of the passage?', NULL, NULL, '[\"A. Option A\", \"B. Option B\", \"C. Option C\", \"D. Option D\"]', 'C', '文章主要讨论了...', 2, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-22 04:10:46', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (45, 1, 'SINGLE_CHOICE', '短对话-询问时间', 'When will the meeting start?', NULL, NULL, '[\"A. At 2:00 PM\", \"B. At 2:30 PM\", \"C. At 3:00 PM\", \"D. At 3:30 PM\"]', 'B', '从对话中\"half past two\"可以判断是2:30', 1, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (46, 1, 'SINGLE_CHOICE', '短对话-地点判断', 'Where are the speakers?', NULL, NULL, '[\"A. At a bank\", \"B. At a post office\", \"C. At a hotel\", \"D. At a hospital\"]', 'C', '从\"check in\"和\"room key\"可以判断在酒店', 1, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (47, 1, 'SINGLE_CHOICE', '长对话-细节理解', 'What is the man looking for?', NULL, NULL, '[\"A. A job\", \"B. An apartment\", \"C. A car\", \"D. A book\"]', 'B', '男士明确说在找apartment', 2, 2.50, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (48, 1, 'SINGLE_CHOICE', '长对话-推理判断', 'What can we infer from the conversation?', NULL, NULL, '[\"A. The man is satisfied\", \"B. The woman is helpful\", \"C. They are colleagues\", \"D. It\'s a formal meeting\"]', 'B', '从女士提供的帮助可以推断', 2, 2.50, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (49, 2, 'SINGLE_CHOICE', '快速阅读-主旨大意', 'What is the main topic of the passage?', NULL, NULL, '[\"A. Modern technology\", \"B. Environmental issues\", \"C. Economic development\", \"D. Social changes\"]', 'A', '文章主要讨论现代科技的发展', 2, 2.50, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (50, 2, 'SINGLE_CHOICE', '仔细阅读-细节题', 'According to the passage, what happened in 2020?', NULL, NULL, '[\"A. A new policy was introduced\", \"B. The company expanded\", \"C. The market declined\", \"D. Technology improved\"]', 'A', '文中明确提到2020年推出了新政策', 2, 2.50, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (51, 2, 'SINGLE_CHOICE', '词汇理解-同义替换', 'The word \"significant\" in paragraph 2 means:', NULL, NULL, '[\"A. Important\", \"B. Small\", \"C. Interesting\", \"D. Difficult\"]', 'A', 'significant表示重要的，显著的', 1, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (52, 2, 'SINGLE_CHOICE', '推理判断-作者态度', 'What is the author\'s attitude towards the issue?', NULL, NULL, '[\"A. Positive\", \"B. Negative\", \"C. Neutral\", \"D. Critical\"]', 'A', '从作者的用词可以看出积极态度', 2, 2.50, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (53, 3, 'SINGLE_CHOICE', '六级讲座-主旨题', 'What is the lecture mainly about?', NULL, NULL, '[\"A. Economic theories\", \"B. Historical events\", \"C. Scientific discoveries\", \"D. Cultural differences\"]', 'C', '讲座主要讨论科学发现', 3, 3.00, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (54, 3, 'SINGLE_CHOICE', '六级对话-推理题', 'What will the woman probably do next?', NULL, NULL, '[\"A. Go to the library\", \"B. Call her professor\", \"C. Revise her paper\", \"D. Attend a meeting\"]', 'C', '从对话推断女士会修改论文', 2, 2.50, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (55, 4, 'SINGLE_CHOICE', '六级深度阅读', 'What is the primary purpose of the passage?', NULL, NULL, '[\"A. To inform\", \"B. To persuade\", \"C. To entertain\", \"D. To criticize\"]', 'B', '文章的主要目的是说服读者', 3, 3.00, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (56, 4, 'SINGLE_CHOICE', '六级态度题', 'The author\'s tone in the passage can best be described as:', NULL, NULL, '[\"A. Objective\", \"B. Pessimistic\", \"C. Optimistic\", \"D. Skeptical\"]', 'C', '作者的语气是乐观的', 3, 3.00, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-02-23 01:16:23', '2026-02-27 21:59:17', 0);
INSERT INTO `question` VALUES (101, 1, 'LISTENING', '听力理解-短对话1', 'What does the woman mean?', NULL, NULL, '[\"A. She needs more time to prepare\", \"B. She is ready for the exam\", \"C. She wants to postpone the exam\", \"D. She has finished studying\"]', 'B', '女士表示她已经准备好了考试', 2, 1.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (102, 1, 'LISTENING', '听力理解-短对话2', 'Where does this conversation most probably take place?', NULL, NULL, '[\"A. In a library\", \"B. In a bookstore\", \"C. In a classroom\", \"D. In a coffee shop\"]', 'A', '从对话中提到的借书和安静环境可以判断是在图书馆', 2, 1.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (103, 1, 'LISTENING', '听力理解-长对话1', 'What is the main topic of the conversation?', NULL, NULL, '[\"A. Travel plans\", \"B. Job interview\", \"C. Study abroad\", \"D. Language learning\"]', 'C', '对话主要讨论出国留学事宜', 2, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (104, 1, 'LISTENING', '听力理解-长对话2', 'What did the man suggest?', NULL, NULL, '[\"A. Taking a different course\", \"B. Changing the schedule\", \"C. Joining a study group\", \"D. Hiring a tutor\"]', 'C', '男士建议加入学习小组', 2, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (105, 1, 'LISTENING', '听力理解-短文理解', 'What is the passage mainly about?', NULL, NULL, '[\"A. Environmental protection\", \"B. Climate change\", \"C. Energy conservation\", \"D. Sustainable development\"]', 'D', '文章主要讨论可持续发展', 3, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (106, 2, 'READING', '阅读理解-词汇理解1', 'The word \"deteriorate\" in line 10 most probably means ______.', NULL, NULL, '[\"A. improve\", \"B. worsen\", \"C. maintain\", \"D. change\"]', 'B', 'deteriorate表示\"恶化、变坏\"', 2, 1.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (107, 2, 'READING', '阅读理解-细节题1', 'According to the passage, what is the main reason for...?', NULL, NULL, '[\"A. Economic factors\", \"B. Social pressure\", \"C. Environmental concerns\", \"D. Political issues\"]', 'C', '文中明确提到环境问题是主要原因', 2, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (108, 2, 'READING', '阅读理解-推理题1', 'It can be inferred from the passage that ______.', NULL, NULL, '[\"A. Technology will solve all problems\", \"B. Human behavior needs to change\", \"C. Government intervention is unnecessary\", \"D. The situation is hopeless\"]', 'B', '从文章可推断出人类行为需要改变', 3, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (109, 2, 'READING', '阅读理解-主旨题1', 'What is the main idea of the passage?', NULL, NULL, '[\"A. The history of technology\", \"B. The impact of social media\", \"C. The future of education\", \"D. The benefits of reading\"]', 'B', '文章主要讨论社交媒体的影响', 2, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (110, 2, 'READING', '阅读理解-态度题1', 'What is the authors attitude toward...?', NULL, NULL, '[\"A. Positive\", \"B. Negative\", \"C. Neutral\", \"D. Indifferent\"]', 'A', '作者态度积极正面', 2, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (111, 3, 'WRITING', '图表作文', 'Directions: For this part, you are allowed 30 minutes to write a short essay based on the picture below...', NULL, NULL, NULL, NULL, '参考范文：The chart shows... In conclusion...', 3, 15.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (112, 3, 'WRITING', '应用文写作', 'Directions: Write a letter of complaint about...', NULL, NULL, NULL, NULL, '参考范文：Dear Sir/Madam, I am writing to complain about...', 2, 15.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (113, 4, 'TRANSLATION', '汉译英-传统文化', '请将下列汉语翻译成英语：中国传统文化博大精深，源远流长。', NULL, NULL, NULL, 'Chinese traditional culture is extensive and profound, with a long history.', '注意词汇选择和句式结构', 3, 15.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (114, 4, 'TRANSLATION', '汉译英-经济发展', '请将下列汉语翻译成英语：随着经济的发展，人们的生活水平不断提高。', NULL, NULL, NULL, 'With the development of economy, peoples living standards have been continuously improving.', '注意时态和固定搭配', 2, 15.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (115, 5, 'CLOZE', '完形填空1', 'The discovery of new evidence led to ______.', 1, NULL, '[\"A. the thief being caught\", \"B. catch the thief\", \"C. the thief to be caught\", \"D. the thief to catch\"]', 'A', 'lead to后接动名词', 2, 1.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 02:52:31', 0);
INSERT INTO `question` VALUES (116, 5, 'CLOZE', '完形填空2', 'She was so absorbed in her work that she didn\'t notice the time ______.', 1, NULL, '[\"A. pass\", \"B. passed\", \"C. passing\", \"D. to pass\"]', 'C', 'notice后接现在分词表示进行', 2, 1.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 02:52:31', 0);
INSERT INTO `question` VALUES (117, 5, 'CLOZE', '完形填空3', 'The company has ______ a new policy to reduce costs.', 1, NULL, '[\"A. adapted\", \"B. adopted\", \"C. adjusted\", \"D. admitted\"]', 'B', 'adopt表示\"采纳、采用\"', 2, 1.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 02:52:31', 0);
INSERT INTO `question` VALUES (118, 1, 'LISTENING', '六级听力-讲座1', 'What is the lecture mainly discussing?', NULL, NULL, '[\"A. Artificial intelligence\", \"B. Machine learning\", \"C. Data analysis\", \"D. Computer programming\"]', 'A', '讲座主要讨论人工智能', 3, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (119, 2, 'READING', '六级阅读-深度理解', 'The author mentions X primarily to ______.', NULL, NULL, '[\"A. provide an example\", \"B. introduce a concept\", \"C. refute an argument\", \"D. emphasize a point\"]', 'D', '作者提到X主要是为了强调观点', 3, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (120, 3, 'WRITING', '六级议论文', 'Directions: Write an essay commenting on the saying...', NULL, NULL, NULL, NULL, '参考范文：In todays society... In conclusion...', 3, 15.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (121, 6, 'SINGLE_CHOICE', '词汇语法-单选1', 'He suggested that we ______ the meeting until next week.', NULL, NULL, '[\"A. postpone\", \"B. postponed\", \"C. will postpone\", \"D. would postpone\"]', 'A', 'suggest后的宾语从句用虚拟语气，动词用原形', 2, 1.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (122, 6, 'SINGLE_CHOICE', '词汇语法-单选2', 'Not until yesterday ______ the importance of the issue.', NULL, NULL, '[\"A. did he realize\", \"B. he realized\", \"C. he did realize\", \"D. realized he\"]', 'A', 'not until置于句首时，主句要部分倒装', 2, 1.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (123, 6, 'SINGLE_CHOICE', '词汇语法-单选3', 'The book is worth ______.', NULL, NULL, '[\"A. read\", \"B. to read\", \"C. reading\", \"D. being read\"]', 'C', 'be worth doing为固定搭配', 1, 1.00, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 01:59:34', '2026-02-23 01:59:34', 0);
INSERT INTO `question` VALUES (201, 1, 'WRITING', 'Part I Writing', 'Directions: For this part, you are allowed 30 minutes to write an essay on the importance of time management for college students. You should write at least 120 words but no more than 180 words.', NULL, NULL, NULL, NULL, '写作评分标准：1. 内容切题，结构清晰（40%）2. 语言准确流畅（40%）3. 用词丰富多样（20%）', 2, 106.50, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (202, 2, 'LISTENING', 'News Report 1 - Question 1', 'What is the news report mainly about?', 2, NULL, '[\"A. A new hospital opening\", \"B. An AI system for disease diagnosis\", \"C. A medical conference\", \"D. Patient care improvements\"]', 'B', '新闻主要报道了一家科技公司开发的AI诊断系统，答案是B', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (203, 2, 'LISTENING', 'News Report 1 - Question 2', 'How many patients was the AI system tested on?', 2, NULL, '[\"A. 5,000\", \"B. 8,000\", \"C. 10,000\", \"D. 15,000\"]', 'C', '文中明确提到tested on over 10,000 patients', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (204, 2, 'LISTENING', 'Conversation 1 - Question 1', 'What are the speakers mainly discussing?', 3, NULL, '[\"A. A job interview\", \"B. Career fair preparation\", \"C. Resume writing\", \"D. Summer vacation plans\"]', 'B', '对话主要讨论准备参加招聘会，答案是B', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (205, 2, 'LISTENING', 'Conversation 1 - Question 2', 'What does the woman want to find?', 3, NULL, '[\"A. A part-time job\", \"B. A full-time job\", \"C. An internship\", \"D. A volunteer position\"]', 'C', '女士说want to find an internship for next summer', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (206, 2, 'LISTENING', 'Passage 1 - Question 1', 'What is the main topic of the passage?', 4, NULL, '[\"A. Weather patterns\", \"B. Climate change\", \"C. Ocean currents\", \"D. Renewable energy\"]', 'B', '文章主要讨论气候变化问题', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (207, 2, 'LISTENING', 'Passage 1 - Question 2', 'According to the passage, what could happen if no action is taken?', 4, NULL, '[\"A. Temperatures could drop\", \"B. Oceans could freeze\", \"C. Temperatures could rise by more than 2°C\", \"D. Weather would stabilize\"]', 'C', '文中提到without action, temperatures could rise by more than 2 degrees', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (208, 3, 'CLOZE', '完形填空 - Question 1', 'Social media has ______ the way people communicate.', 1, NULL, '[\"A. revolutionized\", \"B. destroyed\", \"C. ignored\", \"D. prevented\"]', 'A', 'revolutionize表示彻底改变，符合语境', 2, 3.55, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (209, 3, 'CLOZE', '完形填空 - Question 2', 'It allows people to ______ relationships across distances.', 1, NULL, '[\"A. destroy\", \"B. maintain\", \"C. forget\", \"D. avoid\"]', 'B', 'maintain relationships表示维持关系', 2, 3.55, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (210, 3, 'READING', '长篇阅读 - Question 1', 'Social media has made long-distance communication easier.', 5, NULL, '[\"A. 第1段\", \"B. 第2段\", \"C. 第3段\", \"D. 第4段\"]', 'B', '第2段提到social media allows people to maintain relationships across distances', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (211, 3, 'READING', 'Passage 1 - Question 1', 'According to the passage, what is one benefit of social media?', 5, NULL, '[\"A. It reduces mental health issues\", \"B. It helps maintain long-distance relationships\", \"C. It eliminates misinformation\", \"D. It increases privacy\"]', 'B', '第2段明确提到maintain relationships across distances是社交媒体的好处', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (212, 3, 'READING', 'Passage 1 - Question 2', 'What negative effect of social media is mentioned?', 5, NULL, '[\"A. Higher costs\", \"B. Slower communication\", \"C. Mental health issues\", \"D. Reduced productivity\"]', 'C', '第3段提到excessive use has been linked to mental health issues', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (213, 3, 'READING', 'Passage 2 - Question 1', 'What percentage of jobs could be automated in the next two decades?', 6, NULL, '[\"A. 20%\", \"B. 30%\", \"C. 40%\", \"D. 50%\"]', 'C', '文中明确提到up to 40% of current jobs could be automated', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (214, 3, 'READING', 'Passage 2 - Question 2', 'According to the passage, what should education focus on?', 6, NULL, '[\"A. Memorization\", \"B. Routine tasks\", \"C. Critical thinking and creativity\", \"D. Traditional subjects\"]', 'C', '第3段提到schools should emphasize critical thinking, creativity, and adaptability', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (215, 4, 'TRANSLATION', 'Part IV Translation', 'Directions: For this part, you are allowed 30 minutes to translate a passage from Chinese into English.\n\n中国的互联网社区是全世界发展最快的。2025年中国约有7亿网民，而且这个数字还在迅速增长。互联网的日渐流行带来了重大的社会变化。中国网民往往不同于美国网民。美国网民更多是受实际需要的驱使，用互联网为工具发电子邮件、买卖商品、规划旅程或付款。中国网民更多是出于社交原因使用互联网，因而更广泛地使用论坛、博客、聊天室等。', NULL, NULL, NULL, 'China\'s internet community is the fastest growing in the world. In 2025, China had about 700 million netizens, and this number is still growing rapidly. The increasing popularity of the internet has brought about significant social changes. Chinese netizens are often different from American netizens. American netizens are more driven by practical needs, using the internet as a tool to send emails, buy and sell goods, plan trips, or make payments. Chinese netizens use the internet more for social reasons, and thus make wider use of forums, blogs, and chat rooms.', '翻译要点：1. 准确传达原文意思 2. 语言表达流畅自然 3. 注意时态和数字表达 4. 词汇选择恰当', 2, 106.50, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:15:11', '2026-02-23 03:15:11', 0);
INSERT INTO `question` VALUES (216, 2, 'LISTENING', 'News Report 2 - Question 1', 'What is the main purpose of the education reform?', 7, NULL, '[\"A. To increase homework\", \"B. To reduce student workload\", \"C. To close schools\", \"D. To raise tuition fees\"]', 'B', '新闻提到reform aims to reduce student workload', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (217, 2, 'LISTENING', 'News Report 2 - Question 2', 'How long will the reform take to implement?', 7, NULL, '[\"A. One year\", \"B. Two years\", \"C. Three years\", \"D. Five years\"]', 'C', '文中明确说over the next three years', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (218, 2, 'LISTENING', 'News Report 3 - Question 1', 'What did NASA scientists discover?', 8, NULL, '[\"A. A new planet\", \"B. Water ice on the moon\", \"C. Alien life\", \"D. A black hole\"]', 'B', '新闻报道发现了moon上的water ice', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (219, 2, 'LISTENING', 'News Report 3 - Question 2', 'Where is the ice located?', 8, NULL, '[\"A. On mountain peaks\", \"B. In craters\", \"C. On the equator\", \"D. Underground\"]', 'B', '文中提到located in permanently shadowed craters', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (220, 2, 'LISTENING', 'News Report 3 - Question 3', 'Why is this discovery important?', 8, NULL, '[\"A. For tourism\", \"B. For future lunar missions\", \"C. For mining\", \"D. For research only\"]', 'B', '发现对future lunar missions很关键', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (221, 2, 'LISTENING', 'Conversation 2 - Question 1', 'Where does this conversation take place?', 9, NULL, '[\"A. In a classroom\", \"B. In the library\", \"C. In the cafeteria\", \"D. In the dormitory\"]', 'B', '对话开始提到library', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (222, 2, 'LISTENING', 'Conversation 2 - Question 2', 'Why does the woman come to the library?', 9, NULL, '[\"A. To return books\", \"B. To meet friends\", \"C. To prepare for exams\", \"D. To work part-time\"]', 'C', '女士说prepare for exams', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (223, 2, 'LISTENING', 'Conversation 2 - Question 3', 'What time does the library close on weekdays?', 9, NULL, '[\"A. 9 PM\", \"B. 10 PM\", \"C. 11 PM\", \"D. Midnight\"]', 'B', '男士提到closes at 10 PM on weekdays', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (224, 2, 'LISTENING', 'Conversation 3 - Question 1', 'What is the man doing?', 10, NULL, '[\"A. Ordering food\", \"B. Canceling a reservation\", \"C. Making a reservation\", \"D. Complaining about service\"]', 'C', '男士说I\'d like to make a reservation', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (225, 2, 'LISTENING', 'Conversation 3 - Question 2', 'How many people will be dining?', 10, NULL, '[\"A. Two\", \"B. Three\", \"C. Four\", \"D. Five\"]', 'C', '男士说Four people', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (226, 2, 'LISTENING', 'Passage 2 - Question 1', 'What is the main topic of the passage?', 11, NULL, '[\"A. Air pollution\", \"B. Urban transportation\", \"C. City planning\", \"D. Tourism\"]', 'B', '文章主要讨论城市交通问题', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (227, 2, 'LISTENING', 'Passage 2 - Question 2', 'What are cities investing in?', 11, NULL, '[\"A. More roads\", \"B. Parking lots\", \"C. Public transportation\", \"D. Private cars\"]', 'C', '文中提到investing in public transportation infrastructure', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (228, 2, 'LISTENING', 'Passage 2 - Question 3', 'What benefit of public transportation is mentioned?', 11, NULL, '[\"A. Faster speed\", \"B. Lower costs\", \"C. Environmental protection\", \"D. More comfort\"]', 'C', '提到contribute to environmental protection', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (229, 2, 'LISTENING', 'Passage 2 - Question 4', 'What remains a challenge?', 11, NULL, '[\"A. Building infrastructure\", \"B. Raising funds\", \"C. Hiring drivers\", \"D. Changing people\'s habits\"]', 'D', '文末提到changing people\'s transportation habits remains a challenge', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (230, 2, 'LISTENING', 'Passage 3 - Question 1', 'What are the three pillars of good health?', 12, NULL, '[\"A. Diet, sleep, and work\", \"B. Exercise, nutrition, and sleep\", \"C. Medicine, rest, and play\", \"D. Study, work, and rest\"]', 'B', '文中明确提到three pillars是exercise, nutrition, and sleep', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (231, 2, 'LISTENING', 'Passage 3 - Question 2', 'How often should people exercise according to studies?', 12, NULL, '[\"A. Once a week\", \"B. Twice a week\", \"C. Three times a week\", \"D. Every day\"]', 'C', '提到at least three times a week', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (232, 2, 'LISTENING', 'Passage 3 - Question 3', 'What type of food should be limited?', 12, NULL, '[\"A. Fruits\", \"B. Vegetables\", \"C. Processed foods\", \"D. Whole grains\"]', 'C', '文中说limiting processed foods', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (233, 2, 'LISTENING', 'Passage 3 - Question 4', 'How many hours of sleep are recommended?', 12, NULL, '[\"A. 5-6 hours\", \"B. 6-7 hours\", \"C. 7-8 hours\", \"D. 8-9 hours\"]', 'C', '文末提到seven to eight hours of sleep', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (234, 2, 'LISTENING', 'Passage 3 - Question 5', 'What is the passage mainly about?', 12, NULL, '[\"A. Exercise tips\", \"B. Diet plans\", \"C. Sleep science\", \"D. Healthy lifestyle\"]', 'D', '整篇文章讨论maintaining a healthy lifestyle', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (235, 3, 'CLOZE', '完形填空 - Question 3', 'Understanding how habits ______ is crucial.', 13, NULL, '[\"A. form\", \"B. fail\", \"C. disappear\", \"D. change\"]', 'A', 'form表示形成，how habits form指习惯如何形成', 2, 3.55, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (236, 3, 'CLOZE', '完形填空 - Question 4', 'The habit loop involves a cue, routine, and ______.', 13, NULL, '[\"A. punishment\", \"B. reward\", \"C. failure\", \"D. cost\"]', 'B', '习惯循环的三要素是cue, routine, reward', 2, 3.55, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (237, 3, 'CLOZE', '完形填空 - Question 5', 'Research suggests it takes ______ days to form a new habit.', 13, NULL, '[\"A. 21\", \"B. 30\", \"C. 66\", \"D. 100\"]', 'C', '文中提到an average of 66 days', 2, 3.55, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (238, 3, 'CLOZE', '完形填空 - Question 6', 'Environmental design can ______ habit formation.', 13, NULL, '[\"A. prevent\", \"B. influence\", \"C. destroy\", \"D. ignore\"]', 'B', 'influence表示影响，环境设计影响习惯形成', 2, 3.55, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (239, 3, 'CLOZE', '完形填空 - Question 7', 'AI systems can analyze medical images with great ______.', 14, NULL, '[\"A. difficulty\", \"B. cost\", \"C. accuracy\", \"D. speed\"]', 'C', '文中提到with accuracy comparable to human radiologists', 2, 3.55, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (240, 3, 'CLOZE', '完形填空 - Question 8', 'Machine learning can ______ health outcomes.', 14, NULL, '[\"A. predict\", \"B. prevent\", \"C. ignore\", \"D. worsen\"]', 'A', 'predict health outcomes表示预测健康结果', 2, 3.55, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (241, 3, 'CLOZE', '完形填空 - Question 9', 'The fashion industry is a major ______.', 15, NULL, '[\"A. saver\", \"B. polluter\", \"C. helper\", \"D. cleaner\"]', 'B', '文中说fashion industry is one of the world\'s largest polluters', 2, 3.55, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (242, 3, 'CLOZE', '完形填空 - Question 10', 'Sustainable fashion uses ______ materials.', 15, NULL, '[\"A. cheap\", \"B. expensive\", \"C. organic\", \"D. plastic\"]', 'C', '可持续时尚使用organic materials', 2, 3.55, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (243, 3, 'READING', '长篇阅读 - Question 2', 'Habits involve three components: cue, routine, and reward.', 13, NULL, '[\"A. 第1段\", \"B. 第2段\", \"C. 第3段\", \"D. 第4段\"]', 'B', '第2段详细描述了习惯的三个组成部分', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (244, 3, 'READING', '长篇阅读 - Question 3', 'It takes about 66 days to develop a new habit.', 13, NULL, '[\"A. 第1段\", \"B. 第2段\", \"C. 第3段\", \"D. 第4段\"]', 'C', '第3段提到66 days', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (245, 3, 'READING', '长篇阅读 - Question 4', 'AI can predict which patients are at high risk.', 14, NULL, '[\"A. 第1段\", \"B. 第2段\", \"C. 第3段\", \"D. 第4段\"]', 'B', '第2段提到AI的预测能力', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (246, 3, 'READING', '长篇阅读 - Question 5', 'Patient privacy is an ethical concern with AI.', 14, NULL, '[\"A. 第1段\", \"B. 第2段\", \"C. 第3段\", \"D. 第4段\"]', 'C', '第3段讨论了隐私等伦理问题', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (247, 3, 'READING', '长篇阅读 - Question 6', 'Sustainable fashion includes using recycled materials.', 15, NULL, '[\"A. 第1段\", \"B. 第2段\", \"C. 第3段\", \"D. 第4段\"]', 'B', '第2段提到using organic and recycled materials', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (248, 3, 'READING', '长篇阅读 - Question 7', 'Consumer behavior is crucial for sustainable fashion.', 15, NULL, '[\"A. 第1段\", \"B. 第2段\", \"C. 第3段\", \"D. 第4段\"]', 'C', '第3段强调consumer behavior的重要性', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (249, 3, 'READING', '长篇阅读 - Question 8', 'Sleep has different stages with specific functions.', 16, NULL, '[\"A. 第1段\", \"B. 第2段\", \"C. 第3段\", \"D. 第4段\"]', 'B', '第2段详细描述了睡眠的不同阶段', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (250, 3, 'READING', '长篇阅读 - Question 9', 'Sleep deprivation increases disease risk.', 16, NULL, '[\"A. 第1段\", \"B. 第2段\", \"C. 第3段\", \"D. 第4段\"]', 'C', '第3段讨论了睡眠不足的后果', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (251, 3, 'READING', '长篇阅读 - Question 10', 'Good sleep hygiene includes consistent sleep schedule.', 16, NULL, '[\"A. 第1段\", \"B. 第2段\", \"C. 第3段\", \"D. 第4段\"]', 'D', '第4段提到maintaining a consistent sleep schedule', 2, 7.10, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (252, 3, 'READING', 'Passage 3 - Question 1', 'According to the passage, what percentage of daily actions are driven by habits?', 13, NULL, '[\"A. 20%\", \"B. 30%\", \"C. 40%\", \"D. 50%\"]', 'C', '第1段提到approximately 40% of our daily actions', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (253, 3, 'READING', 'Passage 3 - Question 2', 'What is the key to breaking bad habits?', 13, NULL, '[\"A. Quick changes\", \"B. Patience and consistency\", \"C. Strong willpower\", \"D. Professional help\"]', 'B', '第3段强调requires patience and consistency', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (254, 3, 'READING', 'Passage 4 - Question 1', 'What can AI systems do in healthcare?', 14, NULL, '[\"A. Replace all doctors\", \"B. Analyze medical images accurately\", \"C. Cure all diseases\", \"D. Reduce patient costs\"]', 'B', '第1段提到AI can analyze medical images with accuracy', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (255, 3, 'READING', 'Passage 4 - Question 2', 'What is a concern about AI in healthcare?', 14, NULL, '[\"A. It is too expensive\", \"B. It is too slow\", \"C. Patient privacy issues\", \"D. It is unreliable\"]', 'C', '第3段提到patient privacy must be protected', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (256, 3, 'READING', 'Passage 5 - Question 1', 'What is sustainable fashion mainly about?', 15, NULL, '[\"A. Following trends\", \"B. Environmental protection\", \"C. Low prices\", \"D. Fast production\"]', 'B', '文章讨论可持续时尚的环保理念', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (257, 3, 'READING', 'Passage 5 - Question 2', 'What obstacle does sustainable fashion face?', 15, NULL, '[\"A. Lack of demand\", \"B. Poor quality\", \"C. Higher costs\", \"D. Complicated designs\"]', 'C', '第4段提到cost more是障碍之一', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (258, 3, 'READING', 'Passage 6 - Question 1', 'How long does a complete sleep cycle last?', 16, NULL, '[\"A. 60 minutes\", \"B. 75 minutes\", \"C. 90 minutes\", \"D. 120 minutes\"]', 'C', '第2段提到A complete sleep cycle lasts about 90 minutes', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (259, 3, 'READING', 'Passage 6 - Question 2', 'What is one consequence of chronic sleep loss?', 16, NULL, '[\"A. Improved memory\", \"B. Weight loss\", \"C. Increased disease risk\", \"D. Better mood\"]', 'C', '第3段提到increased risk of obesity, diabetes等疾病', 2, 14.20, NULL, NULL, NULL, NULL, 0, 0.00, NULL, 1, '2026-02-23 03:20:18', '2026-02-23 03:20:18', 0);
INSERT INTO `question` VALUES (260, 5, 'writing', '11111', '11111', NULL, NULL, '[\"\", \"\", \"\", \"\"]', '1111', '1111', 2, 5.00, NULL, NULL, NULL, '1', 0, 0.00, 3, 1, '2026-02-27 23:48:00', '2026-02-27 23:48:00', 0);
INSERT INTO `question` VALUES (261, 1, '单选', '听力题目示例', 'What does the man mean?', NULL, NULL, '[\"A. He likes coffee\", \"B. He prefers tea\", \"C. He wants water\", \"D. He is not thirsty\"]', 'B', '根据对话内容，男士表示更喜欢茶', 1, 1.00, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-03-02 03:05:44', '2026-03-02 03:05:44', 0);
INSERT INTO `question` VALUES (262, 2, '单选', '阅读理解示例', 'What is the main idea of the passage?', NULL, NULL, '[\"A. Option A\", \"B. Option B\", \"C. Option C\", \"D. Option D\"]', 'C', '文章主要讨论了...', 2, 2.00, NULL, NULL, NULL, NULL, 0, 0.00, 3, 1, '2026-03-02 03:05:44', '2026-03-02 03:05:44', 0);

-- ----------------------------
-- Table structure for question_category
-- ----------------------------
DROP TABLE IF EXISTS `question_category`;
CREATE TABLE `question_category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父分类ID',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `category_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类编码',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '等级:CET4/CET6/IELTS/TOEFL',
  `skill_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '技能类型:听力/阅读/写作/翻译',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_category_code`(`category_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_category
-- ----------------------------
INSERT INTO `question_category` VALUES (1, 0, '英语四级', 'CET4', 'CET4', NULL, 0, 1, '2026-02-22 04:10:45', '2026-02-22 04:10:45', 0);
INSERT INTO `question_category` VALUES (2, 0, '英语六级', 'CET6', 'CET6', NULL, 0, 1, '2026-02-22 04:10:45', '2026-02-22 04:10:45', 0);
INSERT INTO `question_category` VALUES (3, 1, '听力理解', 'CET4_LISTENING', 'CET4', '听力', 0, 1, '2026-02-22 04:10:45', '2026-02-22 04:10:45', 0);
INSERT INTO `question_category` VALUES (4, 1, '阅读理解', 'CET4_READING', 'CET4', '阅读', 0, 1, '2026-02-22 04:10:45', '2026-02-22 04:10:45', 0);
INSERT INTO `question_category` VALUES (5, 1, '写作翻译', 'CET4_WRITING', 'CET4', '写作', 0, 1, '2026-02-22 04:10:45', '2026-02-22 04:10:45', 0);
INSERT INTO `question_category` VALUES (6, 2, '听力理解', 'CET6_LISTENING', 'CET6', '听力', 0, 1, '2026-02-22 04:10:45', '2026-02-22 04:10:45', 0);
INSERT INTO `question_category` VALUES (7, 2, '阅读理解', 'CET6_READING', 'CET6', '阅读', 0, 1, '2026-02-22 04:10:45', '2026-02-22 04:10:45', 0);
INSERT INTO `question_category` VALUES (8, 2, '写作翻译', 'CET6_WRITING', 'CET6', '写作', 0, 1, '2026-02-22 04:10:45', '2026-02-22 04:10:45', 0);
INSERT INTO `question_category` VALUES (9, 0, '四级听力', 'CET4_LISTENING', 'CET4', '听力', 1, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `question_category` VALUES (10, 0, '四级阅读', 'CET4_READING', 'CET4', '阅读', 2, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `question_category` VALUES (11, 0, '六级听力', 'CET6_LISTENING', 'CET6', '听力', 3, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `question_category` VALUES (12, 0, '六级阅读', 'CET6_READING', 'CET6', '阅读', 4, 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `question_category` VALUES (13, 0, '四级听力', 'CET4_LISTENING', 'CET4', '听力', 1, 1, '2026-03-02 03:05:44', '2026-03-02 03:05:44', 0);
INSERT INTO `question_category` VALUES (14, 0, '四级阅读', 'CET4_READING', 'CET4', '阅读', 2, 1, '2026-03-02 03:05:44', '2026-03-02 03:05:44', 0);
INSERT INTO `question_category` VALUES (15, 0, '六级听力', 'CET6_LISTENING', 'CET6', '听力', 3, 1, '2026-03-02 03:05:44', '2026-03-02 03:05:44', 0);
INSERT INTO `question_category` VALUES (16, 0, '六级阅读', 'CET6_READING', 'CET6', '阅读', 4, 1, '2026-03-02 03:05:44', '2026-03-02 03:05:44', 0);

-- ----------------------------
-- Table structure for reply_like
-- ----------------------------
DROP TABLE IF EXISTS `reply_like`;
CREATE TABLE `reply_like`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `reply_id` bigint(20) NOT NULL COMMENT '回复ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_reply_user`(`reply_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_reply_id`(`reply_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '回复点赞记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reply_like
-- ----------------------------

-- ----------------------------
-- Table structure for resource_category
-- ----------------------------
DROP TABLE IF EXISTS `resource_category`;
CREATE TABLE `resource_category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父分类ID，0为顶级分类',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序号',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资源分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resource_category
-- ----------------------------
INSERT INTO `resource_category` VALUES (1, '222', 0, 2, '2222', 3, '2026-02-28 11:22:26', '2026-02-28 11:22:26', 0);
INSERT INTO `resource_category` VALUES (2, '344444', 1, 0, '444', 3, '2026-02-28 11:22:37', '2026-02-28 11:22:37', 0);
INSERT INTO `resource_category` VALUES (3, '123', 0, 0, '123', 3, '2026-02-28 11:23:02', '2026-02-28 11:23:02', 0);

-- ----------------------------
-- Table structure for resource_collection
-- ----------------------------
DROP TABLE IF EXISTS `resource_collection`;
CREATE TABLE `resource_collection`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `resource_id` bigint(20) NOT NULL COMMENT '资源ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_resource`(`user_id` ASC, `resource_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_resource_id`(`resource_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资源收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resource_collection
-- ----------------------------
INSERT INTO `resource_collection` VALUES (1, 2, 1, '2026-02-22 04:10:46', 1);
INSERT INTO `resource_collection` VALUES (2, 2, 2, '2026-02-22 04:10:46', 1);
INSERT INTO `resource_collection` VALUES (3, 2, 3, '2026-02-22 04:10:46', 1);
INSERT INTO `resource_collection` VALUES (4, 2, 6, '2026-02-22 04:10:46', 1);
INSERT INTO `resource_collection` VALUES (5, 2, 8, '2026-02-22 04:10:46', 0);
INSERT INTO `resource_collection` VALUES (21, 2, 16, '2026-03-02 01:29:50', 1);

-- ----------------------------
-- Table structure for resource_like
-- ----------------------------
DROP TABLE IF EXISTS `resource_like`;
CREATE TABLE `resource_like`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `resource_id` bigint(20) NOT NULL COMMENT '资源ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_resource`(`user_id` ASC, `resource_id` ASC) USING BTREE,
  INDEX `idx_resource_id`(`resource_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资源点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resource_like
-- ----------------------------

-- ----------------------------
-- Table structure for student_info
-- ----------------------------
DROP TABLE IF EXISTS `student_info`;
CREATE TABLE `student_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `school` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `current_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '当前等级',
  `target_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标等级',
  `study_days` int(11) NULL DEFAULT 0 COMMENT '学习天数',
  `continuous_days` int(11) NULL DEFAULT 0 COMMENT '连续学习天数',
  `total_study_time` int(11) NULL DEFAULT 0 COMMENT '总学习时长(分钟)',
  `achievement_score` int(11) NULL DEFAULT 0 COMMENT '成就积分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_info
-- ----------------------------
INSERT INTO `student_info` VALUES (1, 2, '北京大学', '计算机科学', '大二', 'S2024002', 'CET4', 'CET6', 14, 1, 12000, 2500, '2026-02-22 04:10:46', '2026-02-28 16:08:14');
INSERT INTO `student_info` VALUES (3, 3, '中国', NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 0, '2026-02-28 20:31:33', '2026-02-28 20:31:33');

-- ----------------------------
-- Table structure for study_group
-- ----------------------------
DROP TABLE IF EXISTS `study_group`;
CREATE TABLE `study_group`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '小组ID',
  `group_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '小组名称',
  `group_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '小组头像',
  `group_intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '小组简介',
  `leader_id` bigint(20) NOT NULL COMMENT '组长ID',
  `max_members` int(11) NULL DEFAULT 100 COMMENT '最大成员数',
  `current_members` int(11) NULL DEFAULT 1 COMMENT '当前成员数',
  `activity_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活跃度:LOW/MEDIUM/HIGH',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_leader_id`(`leader_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习小组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_group
-- ----------------------------
INSERT INTO `study_group` VALUES (1, '四级冲刺小组', '/avatars/group_cet4.jpg', '一起备战四级考试，每日打卡学习！', 2, 50, 12, 'HIGH', 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `study_group` VALUES (2, '英语口语练习组', '/avatars/group_speaking.jpg', '每天练习英语口语，提升表达能力', 3, 30, 8, 'MEDIUM', 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `study_group` VALUES (3, '六级备考互助组', '/avatars/group_cet6.jpg', '六级备考资料分享，互相监督学习', 3, 40, 15, 'HIGH', 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `study_group` VALUES (4, '1111', NULL, '111', 2, 50, 1, 'NORMAL', 1, '2026-02-27 03:07:02', '2026-02-27 03:07:02', 0);

-- ----------------------------
-- Table structure for study_note
-- ----------------------------
DROP TABLE IF EXISTS `study_note`;
CREATE TABLE `study_note`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '笔记ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `is_shared` tinyint(4) NULL DEFAULT 0 COMMENT '是否分享',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_is_shared`(`is_shared` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习笔记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_note
-- ----------------------------
INSERT INTO `study_note` VALUES (1, 2, '四级高频词汇整理', '## Day 1 - 经济类词汇\n\n1. **economy** [ɪˈkɒnəmi] n. 经济\n2. **economic** [ˌiːkəˈnɒmɪk] adj. 经济的\n3. **finance** [ˈfaɪnæns] n. 财政，金融\n4. **budget** [ˈbʌdʒɪt] n. 预算\n\n记忆技巧：联想记忆法', '词汇', '四级,词汇,经济类', 1, 46, 12, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `study_note` VALUES (2, 2, '听力技巧总结', '## 听力解题技巧\n\n### 1. 预读选项\n- 快速浏览四个选项\n- 找出关键词差异\n- 预测对话主题\n\n### 2. 听时标注\n- 听到关键词立即标注\n- 注意转折词：but, however\n- 留意语气变化', '听力', '四级,听力,技巧', 1, 78, 18, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `study_note` VALUES (3, 2, '阅读理解错题分析', '## 2月18日错题分析\n\n**题目**：主旨大意题\n**错因**：只关注细节，没有把握全文\n**正确方法**：\n1. 看首尾段\n2. 各段首句\n3. 总结归纳', '阅读', '四级,阅读,错题分析', 0, 0, 0, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `study_note` VALUES (4, 2, '写作模板汇总', '## 英语四级作文模板\n\n### 开头段\n1. 引出话题：Nowadays, ... has become a hot topic.\n2. 提出观点：I firmly believe that...\n\n### 主体段\n1. 论据一：First and foremost, ...\n2. 论据二：Furthermore, ...\n3. 论据三：Last but not least, ...', '写作', '四级,写作,模板', 1, 123, 34, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `study_note` VALUES (5, 2, '语法易错点整理', '## 常见语法错误\n\n### 1. 时态一致\n❌ He said he will come tomorrow.\n✓ He said he would come the next day.\n\n### 2. 主谓一致\n❌ The number of students are increasing.\n✓ The number of students is increasing.', '语法', '四级,语法,易错点', 0, 0, 0, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);

-- ----------------------------
-- Table structure for study_plan
-- ----------------------------
DROP TABLE IF EXISTS `study_plan`;
CREATE TABLE `study_plan`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '计划名称',
  `target_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标等级',
  `target_score` decimal(10, 2) NULL DEFAULT NULL COMMENT '目标分数',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `daily_duration` int(11) NULL DEFAULT NULL COMMENT '每日学习时长(分钟)',
  `daily_questions` int(11) NULL DEFAULT NULL COMMENT '每日题目数',
  `progress` int(11) NULL DEFAULT 0 COMMENT '进度(0-100)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'IN_PROGRESS' COMMENT '状态:IN_PROGRESS/COMPLETED/SUSPENDED',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_plan
-- ----------------------------
INSERT INTO `study_plan` VALUES (1, 2, '四级冲刺计划', 'CET4', 550.00, '2026-02-01', '2026-06-15', 120, 50, 35, 'IN_PROGRESS', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `study_plan` VALUES (2, 2, '每日听力训练', 'CET4', NULL, '2026-02-15', '2026-03-15', 30, 10, 20, 'IN_PROGRESS', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `study_plan` VALUES (3, 2, '词汇突击计划', 'CET4', NULL, '2026-01-01', '2026-01-31', 60, 100, 100, 'COMPLETED', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);

-- ----------------------------
-- Table structure for study_task
-- ----------------------------
DROP TABLE IF EXISTS `study_task`;
CREATE TABLE `study_task`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `plan_id` bigint(20) NULL DEFAULT NULL COMMENT '关联学习计划ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务描述',
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MEDIUM' COMMENT '优先级：LOW/MEDIUM/HIGH',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/IN_PROGRESS/COMPLETED',
  `progress` int(11) NULL DEFAULT 0 COMMENT '完成进度0-100',
  `deadline` date NULL DEFAULT NULL COMMENT '截止日期',
  `completed_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_task
-- ----------------------------
INSERT INTO `study_task` VALUES (1, 2, NULL, '背诵四级核心词汇500个', '使用扇贝单词APP，每天100个，共5天完成', 'HIGH', 'IN_PROGRESS', 0, '2026-02-25', '2026-02-22 06:43:08', '2026-02-22 06:23:08', '2026-02-22 06:23:08', 0);
INSERT INTO `study_task` VALUES (2, 2, NULL, '完成听力模拟题3套', '每天1套，注意错题整理和总结', 'HIGH', 'IN_PROGRESS', 33, '2026-02-24', NULL, '2026-02-22 06:23:08', '2026-02-22 06:23:08', 0);
INSERT INTO `study_task` VALUES (3, 2, NULL, '完成阅读理解10篇', '每天2篇，限时完成并分析', 'MEDIUM', 'IN_PROGRESS', 0, '2026-02-28', '2026-02-22 06:43:12', '2026-02-22 06:23:08', '2026-02-22 06:23:08', 0);
INSERT INTO `study_task` VALUES (4, 2, NULL, '写作练习5篇', '使用四级真题作文题目', 'MEDIUM', 'IN_PROGRESS', 0, '2026-03-01', '2026-02-22 06:43:26', '2026-02-22 06:23:08', '2026-02-22 06:23:08', 0);
INSERT INTO `study_task` VALUES (5, 2, NULL, '整理语法笔记', '重点：时态、虚拟语气、非谓语动词', 'LOW', 'COMPLETED', 100, '2026-02-20', NULL, '2026-02-22 06:23:08', '2026-02-22 06:23:08', 0);
INSERT INTO `study_task` VALUES (6, 2, NULL, '完成四级阅读理解练习', '完成教材Unit 5-6的阅读理解题目，共20题', 'HIGH', 'PENDING', 0, '2026-02-25', NULL, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `study_task` VALUES (7, 2, NULL, '听力Section A专项训练', '完成四级听力Section A部分练习，共15题', 'MEDIUM', 'PENDING', 0, '2026-02-24', NULL, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `study_task` VALUES (8, 2, NULL, '词汇记忆任务', '背诵四级核心词汇100个，整理生词本', 'HIGH', 'IN_PROGRESS', 0, '2026-02-23', NULL, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `study_task` VALUES (9, 2, NULL, '写作练习', '完成一篇150词的英语作文，题目：My View on Online Learning', 'MEDIUM', 'PENDING', 0, '2026-02-26', NULL, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `study_task` VALUES (10, 2, NULL, '翻译练习', '完成3段中译英练习，每段50-80词', 'LOW', 'PENDING', 0, '2026-02-27', NULL, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `study_task` VALUES (11, 4, NULL, '完成听力练习', '四级听力Section B练习', 'HIGH', 'PENDING', 0, '2026-02-25', NULL, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `study_task` VALUES (12, 5, NULL, '词汇测试', '完成Unit 3词汇测试', 'MEDIUM', 'PENDING', 0, '2026-02-24', NULL, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `study_task` VALUES (13, 6, NULL, '阅读理解', '六级阅读长篇练习', 'HIGH', 'PENDING', 0, '2026-02-26', NULL, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `study_task` VALUES (14, 7, NULL, '写作任务', '六级作文练习', 'MEDIUM', 'PENDING', 0, '2026-02-25', NULL, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `study_task` VALUES (15, 8, NULL, '基础语法', '语法专项练习', 'LOW', 'PENDING', 0, '2026-02-23', NULL, '2026-02-28 14:55:44', '2026-02-28 14:55:44', 0);
INSERT INTO `study_task` VALUES (16, 2, NULL, '完成四级阅读理解练习', '完成教材Unit 5-6的阅读理解题目，共20题', 'HIGH', 'PENDING', 0, '2026-02-25', NULL, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `study_task` VALUES (17, 2, NULL, '听力Section A专项训练', '完成四级听力Section A部分练习，共15题', 'MEDIUM', 'PENDING', 0, '2026-02-24', NULL, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `study_task` VALUES (18, 2, NULL, '词汇记忆任务', '背诵四级核心词汇100个，整理生词本', 'HIGH', 'IN_PROGRESS', 0, '2026-02-23', NULL, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `study_task` VALUES (19, 2, NULL, '写作练习', '完成一篇150词的英语作文，题目：My View on Online Learning', 'MEDIUM', 'PENDING', 0, '2026-02-26', NULL, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `study_task` VALUES (20, 2, NULL, '翻译练习', '完成3段中译英练习，每段50-80词', 'LOW', 'PENDING', 0, '2026-02-27', NULL, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `study_task` VALUES (21, 4, NULL, '完成听力练习', '四级听力Section B练习', 'HIGH', 'PENDING', 0, '2026-02-25', NULL, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `study_task` VALUES (22, 5, NULL, '词汇测试', '完成Unit 3词汇测试', 'MEDIUM', 'PENDING', 0, '2026-02-24', NULL, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `study_task` VALUES (23, 6, NULL, '阅读理解', '六级阅读长篇练习', 'HIGH', 'PENDING', 0, '2026-02-26', NULL, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `study_task` VALUES (24, 7, NULL, '写作任务', '六级作文练习', 'MEDIUM', 'PENDING', 0, '2026-02-25', NULL, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);
INSERT INTO `study_task` VALUES (25, 8, NULL, '基础语法', '语法专项练习', 'LOW', 'PENDING', 0, '2026-02-23', NULL, '2026-02-28 16:08:14', '2026-02-28 16:08:14', 0);

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `config_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'STRING' COMMENT '配置类型',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态:0禁用1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记:0未删除1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'system.name', '英语等级智能学习与考试系统', 'STRING', '系统名称', 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `sys_config` VALUES (2, 'system.version', '1.0.0', 'STRING', '系统版本', 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `sys_config` VALUES (3, 'exam.anti_cheat.enabled', 'true', 'STRING', '是否启用防作弊', 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `sys_config` VALUES (4, 'file.upload.max_size', '104857600', 'STRING', '文件上传最大大小(字节)', 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `sys_config` VALUES (5, 'ai.chat.enabled', 'true', 'STRING', 'AI聊天功能是否启用', 1, '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `sys_config` VALUES (6, 'systemName', '英语等级智能学习与考试系统', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (7, 'systemLogo', '/uploads/images/2026/03/01/ba2fa68cf52a499c8cf49b815d69edf8.jpg', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (8, 'copyright', '© 2024 English Learning System', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (9, 'icp', '', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (10, 'examDefaultDuration', '120', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (11, 'examSwitchWarning', '3', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (12, 'examAutoSubmit', 'true', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (13, 'examScoreDecimal', '1', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (14, 'smtpHost', '', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (15, 'smtpPort', '465', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (16, 'senderEmail', '', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (17, 'storageType', 'local', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (18, 'storageMaxSize', '100', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (19, 'storageAllowTypes', 'jpg,png,gif,pdf,doc,docx,mp3,mp4', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (20, 'email.host', '', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (21, 'email.username', '', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);
INSERT INTO `sys_config` VALUES (22, 'email.port', '465', 'STRING', NULL, 1, '2026-03-01 02:46:53', '2026-03-01 02:46:53', 0);

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地理位置',
  `browser` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器',
  `os` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态:0失败1成功',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `execute_time` int(11) NULL DEFAULT NULL COMMENT '执行时长(毫秒)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES (1, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 0, '用户名或密码错误', 340, '2026-02-22 04:48:28');
INSERT INTO `sys_log` VALUES (2, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 0, '用户名或密码错误', 337, '2026-02-22 04:48:37');
INSERT INTO `sys_log` VALUES (3, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 0, '用户名或密码错误', 330, '2026-02-22 04:48:40');
INSERT INTO `sys_log` VALUES (4, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 0, '用户名或密码错误', 332, '2026-02-22 04:48:57');
INSERT INTO `sys_log` VALUES (5, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 0, '用户名或密码错误', 5, '2026-02-22 04:53:36');
INSERT INTO `sys_log` VALUES (6, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 0, '用户名或密码错误', 5, '2026-02-22 04:54:02');
INSERT INTO `sys_log` VALUES (7, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 1, NULL, 345, '2026-02-22 05:03:56');
INSERT INTO `sys_log` VALUES (8, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Other', 'Windows', 1, NULL, 336, '2026-02-22 06:21:28');
INSERT INTO `sys_log` VALUES (9, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 1, NULL, 400, '2026-02-23 19:32:10');
INSERT INTO `sys_log` VALUES (10, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Other', 'Windows', 1, NULL, 318, '2026-02-23 19:42:11');
INSERT INTO `sys_log` VALUES (11, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Other', 'Windows', 1, NULL, 539, '2026-02-23 19:51:18');
INSERT INTO `sys_log` VALUES (12, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 1, NULL, 397, '2026-02-25 20:00:02');
INSERT INTO `sys_log` VALUES (13, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 1, NULL, 416, '2026-02-26 21:56:42');
INSERT INTO `sys_log` VALUES (14, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 1, NULL, 330, '2026-02-26 22:05:42');
INSERT INTO `sys_log` VALUES (15, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 1, NULL, 330, '2026-02-26 22:08:31');
INSERT INTO `sys_log` VALUES (16, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 1, NULL, 332, '2026-02-26 22:58:27');
INSERT INTO `sys_log` VALUES (17, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Other', 'Windows', 1, NULL, 331, '2026-02-27 03:35:21');
INSERT INTO `sys_log` VALUES (18, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Other', 'Windows', 1, NULL, 331, '2026-02-27 03:36:58');
INSERT INTO `sys_log` VALUES (19, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Other', 'Windows', 1, NULL, 338, '2026-02-27 03:37:10');
INSERT INTO `sys_log` VALUES (20, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Other', 'Windows', 1, NULL, 341, '2026-02-27 04:16:12');
INSERT INTO `sys_log` VALUES (21, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Chrome', 'Windows', 1, NULL, 329, '2026-02-27 04:18:55');
INSERT INTO `sys_log` VALUES (22, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 1, '2026-02-27 21:48:09');
INSERT INTO `sys_log` VALUES (23, NULL, 'anonymousUser', '重置密码', 'com.english.learning.controller.AuthController.resetPassword()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-02-27 21:48:09');
INSERT INTO `sys_log` VALUES (24, NULL, 'anonymousUser', '用户注册', 'com.english.learning.controller.AuthController.register()', '{\"request\":{\"username\":\"newuser\",\"password\":\"password123\",\"email\":\"newuser@example.com\",\"roleType\":\"student\"}}', '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-02-27 21:48:09');
INSERT INTO `sys_log` VALUES (25, NULL, 'anonymousUser', '退出登录', 'com.english.learning.controller.AuthController.logout()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 2, '2026-02-27 21:48:09');
INSERT INTO `sys_log` VALUES (26, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Chrome', 'Windows', 1, NULL, 515, '2026-02-28 09:23:42');
INSERT INTO `sys_log` VALUES (27, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Chrome', 'Windows', 1, NULL, 395, '2026-02-28 21:13:30');
INSERT INTO `sys_log` VALUES (28, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '0:0:0:0:0:0:0:1', NULL, 'Other', 'Other', 1, NULL, 367, '2026-02-28 21:56:13');
INSERT INTO `sys_log` VALUES (29, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 1, NULL, 393, '2026-02-28 21:57:52');
INSERT INTO `sys_log` VALUES (30, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 0, '用户名或密码错误', 1, '2026-02-28 21:57:52');
INSERT INTO `sys_log` VALUES (31, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 0, '用户名或密码错误', 1, '2026-02-28 21:57:52');
INSERT INTO `sys_log` VALUES (32, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 1, NULL, 353, '2026-02-28 21:57:53');
INSERT INTO `sys_log` VALUES (33, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 1, NULL, 345, '2026-02-28 21:58:56');
INSERT INTO `sys_log` VALUES (34, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 1, NULL, 357, '2026-02-28 22:01:28');
INSERT INTO `sys_log` VALUES (35, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 0, '用户名或密码错误', 0, '2026-02-28 22:01:28');
INSERT INTO `sys_log` VALUES (36, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 0, '用户名或密码错误', 0, '2026-02-28 22:01:28');
INSERT INTO `sys_log` VALUES (37, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 1, NULL, 343, '2026-02-28 22:01:29');
INSERT INTO `sys_log` VALUES (38, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 1, NULL, 153, '2026-02-28 22:14:42');
INSERT INTO `sys_log` VALUES (39, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 1, NULL, 80, '2026-02-28 22:14:43');
INSERT INTO `sys_log` VALUES (40, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 1, NULL, 62, '2026-02-28 22:14:43');
INSERT INTO `sys_log` VALUES (41, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 1, NULL, 58, '2026-02-28 22:14:43');
INSERT INTO `sys_log` VALUES (42, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 0, '账号已被禁用', 50, '2026-02-28 22:14:43');
INSERT INTO `sys_log` VALUES (43, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 1, NULL, 55, '2026-02-28 22:14:43');
INSERT INTO `sys_log` VALUES (44, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Other', 'Other', 1, NULL, 59, '2026-02-28 22:14:43');
INSERT INTO `sys_log` VALUES (45, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Chrome', 'Windows', 1, NULL, 161, '2026-02-28 22:45:22');
INSERT INTO `sys_log` VALUES (46, NULL, 'anonymousUser', '退出登录', 'com.english.learning.controller.AuthController.logout()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 3, '2026-02-28 23:57:17');
INSERT INTO `sys_log` VALUES (47, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 1, '2026-02-28 23:57:17');
INSERT INTO `sys_log` VALUES (48, NULL, 'anonymousUser', '用户注册', 'com.english.learning.controller.AuthController.register()', '{\"request\":{\"username\":\"newuser\",\"password\":\"password123\",\"email\":\"newuser@example.com\",\"roleType\":\"student\"}}', '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-02-28 23:57:17');
INSERT INTO `sys_log` VALUES (49, NULL, 'anonymousUser', '重置密码', 'com.english.learning.controller.AuthController.resetPassword()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-02-28 23:57:17');
INSERT INTO `sys_log` VALUES (50, NULL, 'anonymousUser', '重置密码', 'com.english.learning.controller.AuthController.resetPassword()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-03-01 00:11:05');
INSERT INTO `sys_log` VALUES (51, NULL, 'anonymousUser', '用户注册', 'com.english.learning.controller.AuthController.register()', '{\"request\":{\"username\":\"newuser\",\"password\":\"password123\",\"email\":\"newuser@example.com\",\"roleType\":\"student\"}}', '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-03-01 00:11:05');
INSERT INTO `sys_log` VALUES (52, NULL, 'anonymousUser', '退出登录', 'com.english.learning.controller.AuthController.logout()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 2, '2026-03-01 00:11:04');
INSERT INTO `sys_log` VALUES (53, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 1, '2026-03-01 00:11:05');
INSERT INTO `sys_log` VALUES (54, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-03-01 00:21:46');
INSERT INTO `sys_log` VALUES (55, NULL, 'anonymousUser', '重置密码', 'com.english.learning.controller.AuthController.resetPassword()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-03-01 00:21:46');
INSERT INTO `sys_log` VALUES (56, NULL, 'anonymousUser', '退出登录', 'com.english.learning.controller.AuthController.logout()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 2, '2026-03-01 00:21:45');
INSERT INTO `sys_log` VALUES (57, NULL, 'anonymousUser', '用户注册', 'com.english.learning.controller.AuthController.register()', '{\"request\":{\"username\":\"newuser\",\"password\":\"password123\",\"email\":\"newuser@example.com\",\"roleType\":\"student\"}}', '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-03-01 00:21:46');
INSERT INTO `sys_log` VALUES (58, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 1, '2026-03-01 00:23:04');
INSERT INTO `sys_log` VALUES (59, NULL, 'anonymousUser', '退出登录', 'com.english.learning.controller.AuthController.logout()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 1, '2026-03-01 00:23:04');
INSERT INTO `sys_log` VALUES (60, NULL, 'anonymousUser', '重置密码', 'com.english.learning.controller.AuthController.resetPassword()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-03-01 00:23:04');
INSERT INTO `sys_log` VALUES (61, NULL, 'anonymousUser', '用户注册', 'com.english.learning.controller.AuthController.register()', '{\"request\":{\"username\":\"newuser\",\"password\":\"password123\",\"email\":\"newuser@example.com\",\"roleType\":\"student\"}}', '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 1, '2026-03-01 00:23:04');
INSERT INTO `sys_log` VALUES (62, NULL, 'anonymousUser', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-03-01 01:05:09');
INSERT INTO `sys_log` VALUES (63, NULL, 'anonymousUser', '重置密码', 'com.english.learning.controller.AuthController.resetPassword()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 0, '2026-03-01 01:05:09');
INSERT INTO `sys_log` VALUES (64, NULL, 'anonymousUser', '退出登录', 'com.english.learning.controller.AuthController.logout()', NULL, '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 1, '2026-03-01 01:05:09');
INSERT INTO `sys_log` VALUES (65, NULL, 'anonymousUser', '用户注册', 'com.english.learning.controller.AuthController.register()', '{\"request\":{\"username\":\"newuser\",\"password\":\"password123\",\"email\":\"newuser@example.com\",\"roleType\":\"student\"}}', '127.0.0.1', NULL, 'Unknown', 'Unknown', 1, NULL, 1, '2026-03-01 01:05:09');
INSERT INTO `sys_log` VALUES (66, NULL, 'teacher1', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Chrome', 'Windows', 1, NULL, 103, '2026-03-01 04:59:11');
INSERT INTO `sys_log` VALUES (67, NULL, 'student1', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Chrome', 'Windows', 1, NULL, 174, '2026-03-02 01:29:31');
INSERT INTO `sys_log` VALUES (68, NULL, 'teacher1', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Chrome', 'Windows', 1, NULL, 107, '2026-03-02 04:27:11');
INSERT INTO `sys_log` VALUES (69, NULL, 'teacher1', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Chrome', 'Windows', 1, NULL, 165, '2026-03-02 04:27:44');
INSERT INTO `sys_log` VALUES (70, NULL, 'admin', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Chrome', 'Windows', 1, NULL, 140, '2026-03-02 04:28:36');
INSERT INTO `sys_log` VALUES (71, NULL, 'student1', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Chrome', 'Windows', 1, NULL, 144, '2026-04-09 02:01:55');
INSERT INTO `sys_log` VALUES (72, NULL, 'student1', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Chrome', 'Windows', 1, NULL, 120, '2026-04-10 20:12:31');
INSERT INTO `sys_log` VALUES (73, NULL, 'teacher1', '用户登录', 'com.english.learning.controller.AuthController.login()', NULL, '127.0.0.1', NULL, 'Chrome', 'Windows', 1, NULL, 170, '2026-04-10 20:13:14');

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint(20) NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint(20) NOT NULL COMMENT '接收者ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NOTIFICATION' COMMENT '消息类型:NOTIFICATION-通知,REMINDER-提醒,BROADCAST-广播,PRIVATE-私信',
  `is_read` tinyint(4) NULL DEFAULT 0 COMMENT '是否已读:0-未读,1-已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `sender_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '发送者是否删除:0-否,1-是',
  `receiver_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '接收者是否删除:0-否,1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_receiver_id`(`receiver_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_message
-- ----------------------------
INSERT INTO `sys_message` VALUES (1, 1, 2, '考试成绩公布通知', '您参加的\"四级模拟考试1\"已批改完成，得分87.5分，排名第15名。继续加油！', 'NOTIFICATION', 1, NULL, 0, 0, '2026-02-22 04:10:46');
INSERT INTO `sys_message` VALUES (2, 1, 2, '学习计划提醒', '您的\"四级冲刺计划\"今日任务：完成50道题目，学习时长120分钟。加油！', 'REMINDER', 1, NULL, 0, 0, '2026-02-22 04:10:46');
INSERT INTO `sys_message` VALUES (3, 3, 2, '作业批改完成', '您提交的作文\"The Importance of English Learning\"已批改完成，得分82分。请查看详细反馈。', 'NOTIFICATION', 1, '2026-02-23 23:53:46', 0, 0, '2026-02-22 04:10:46');
INSERT INTO `sys_message` VALUES (4, 1, 2, '打卡提醒', '今天还没有打卡哦~坚持每日学习，养成好习惯！', 'REMINDER', 0, NULL, 0, 0, '2026-02-22 04:10:46');
INSERT INTO `sys_message` VALUES (5, 1, 2, '系统更新通知', '系统将于今晚22:00-23:00进行维护更新，期间部分功能可能无法使用，请提前保存学习进度。', 'BROADCAST', 0, NULL, 0, 0, '2026-02-22 04:10:46');
INSERT INTO `sys_message` VALUES (6, 3, 2, '作业布置通知', 'teacher1老师为您布置了新的作业：四级阅读理解专项练习Unit 5，请及时完成。', 'NOTIFICATION', 0, NULL, 0, 0, '2026-02-28 16:08:14');
INSERT INTO `sys_message` VALUES (7, 3, 4, '学习进度提醒', '您本周的学习任务完成度为65%，建议加强练习。', 'REMINDER', 1, NULL, 0, 0, '2026-02-28 16:08:14');
INSERT INTO `sys_message` VALUES (8, 1, 5, '系统升级通知', '系统将于本周末进行升级维护，部分功能可能会短暂不可用。', 'BROADCAST', 0, NULL, 0, 0, '2026-02-28 16:08:14');
INSERT INTO `sys_message` VALUES (9, 3, 6, '课程资源更新', '新增了\"六级阅读高分秘籍\"课程资源，建议下载学习。', 'NOTIFICATION', 1, NULL, 0, 0, '2026-02-28 16:08:14');
INSERT INTO `sys_message` VALUES (10, 1, 7, '账户安全提醒', '检测到您的账户在新设备登录，请确认是否本人操作。', 'WARNING', 0, NULL, 0, 0, '2026-02-28 16:08:14');
INSERT INTO `sys_message` VALUES (11, 3, 2, '来自教师的消息', '11111李老师在给你发信息 张三', 'NOTIFICATION', 1, '2026-03-01 05:14:47', 0, 0, '2026-02-28 16:25:29');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态:0禁用1正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_name`(`role_name` ASC) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '学生', 'ROLE_STUDENT', '学生角色', 1, '2026-02-22 04:10:45', '2026-02-22 04:10:45', 0);
INSERT INTO `sys_role` VALUES (2, '教师', 'ROLE_TEACHER', '教师角色', 1, '2026-02-22 04:10:45', '2026-02-22 04:10:45', 0);
INSERT INTO `sys_role` VALUES (3, '管理员', 'ROLE_ADMIN', '管理员角色', 1, '2026-02-22 04:10:45', '2026-02-22 04:10:45', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint(4) NULL DEFAULT NULL COMMENT '性别:0女1男',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态:0禁用1正常',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最近登录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@example.com', '13800000001', '系统管理员', '/uploads/images/2026/02/28/eaf552a6ad5d4b439711c2686fb5e275.png', NULL, NULL, 1, '2026-03-02 04:28:36', '2026-02-22 04:10:45', '2026-02-22 05:01:39', 0);
INSERT INTO `sys_user` VALUES (2, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'student1@example.com', '15677778888', '张三', '/uploads/images/2026/02/28/04ab261e86aa4643986e22f6885ccbf7.png', NULL, NULL, 1, '2026-04-10 20:12:31', '2026-02-22 04:10:46', '2026-02-22 05:01:39', 0);
INSERT INTO `sys_user` VALUES (3, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'teacher1@example.com', '15666666666', '李老师', '/uploads/images/2026/02/28/1d193e8af67f4ff4bfeb49f2468a3a61.png', NULL, NULL, 1, '2026-04-10 20:13:14', '2026-02-22 04:10:46', '2026-02-22 05:01:39', 0);
INSERT INTO `sys_user` VALUES (6, 'qa_user_221442', '$2a$10$yU9oR3jQHUQnadnFVNsZIer3d08lWAPgI0dwDc2PAMWTGulqNpv0K', 'qa_221442@example.com', '13900221442', '回归用户-更新', 'https://example.com/a.png', NULL, NULL, 1, '2026-02-28 22:14:43', '2026-02-28 22:14:43', '2026-02-28 22:14:43', 1);
INSERT INTO `sys_user` VALUES (7, '测试1', '$2a$10$UWMQFhcwIpvjYM1YFHqDQeGUzx.kwaiHyzRC3mzQ9y3ycHX99Mixu', '1666787777@qq.com', '', '11', '/uploads/images/2026/02/28/44eb39fbfcd642db921f1b1998056bbb.png', NULL, NULL, 1, NULL, '2026-02-28 22:52:29', '2026-02-28 22:52:29', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (4, 3, 2, '2026-02-22 04:10:46');
INSERT INTO `sys_user_role` VALUES (10, 2, 1, '2026-02-28 22:21:47');
INSERT INTO `sys_user_role` VALUES (13, 1, 3, '2026-02-28 22:51:36');
INSERT INTO `sys_user_role` VALUES (15, 7, 1, '2026-02-28 22:52:40');
INSERT INTO `sys_user_role` VALUES (16, 1, 3, '2026-03-02 03:05:10');
INSERT INTO `sys_user_role` VALUES (17, 2, 1, '2026-03-02 03:05:10');
INSERT INTO `sys_user_role` VALUES (18, 1, 3, '2026-03-02 03:05:16');
INSERT INTO `sys_user_role` VALUES (19, 2, 1, '2026-03-02 03:05:16');
INSERT INTO `sys_user_role` VALUES (20, 1, 3, '2026-03-02 03:05:44');
INSERT INTO `sys_user_role` VALUES (21, 2, 1, '2026-03-02 03:05:44');
INSERT INTO `sys_user_role` VALUES (22, 3, 2, '2026-03-02 03:05:44');

-- ----------------------------
-- Table structure for teacher_class
-- ----------------------------
DROP TABLE IF EXISTS `teacher_class`;
CREATE TABLE `teacher_class`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `teacher_id` bigint(20) NOT NULL COMMENT '教师用户ID',
  `class_id` bigint(20) NOT NULL COMMENT '班级ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_teacher_class`(`teacher_id` ASC, `class_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教师班级关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher_class
-- ----------------------------
INSERT INTO `teacher_class` VALUES (1, 3, 1, '2026-02-28 14:44:42');
INSERT INTO `teacher_class` VALUES (2, 3, 2, '2026-02-28 14:44:42');

-- ----------------------------
-- Table structure for teacher_info
-- ----------------------------
DROP TABLE IF EXISTS `teacher_info`;
CREATE TABLE `teacher_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `institution` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任教机构',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职称',
  `teacher_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教师编号',
  `specialization` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业特长',
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '个人简介',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教师信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher_info
-- ----------------------------
INSERT INTO `teacher_info` VALUES (1, 3, '北京大学外语学院', '副教授', NULL, '英语听力,口语教学', NULL, '2026-02-22 04:10:46', '2026-02-22 04:10:46');

-- ----------------------------
-- Table structure for user_learning_path
-- ----------------------------
DROP TABLE IF EXISTS `user_learning_path`;
CREATE TABLE `user_learning_path`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `path_id` bigint(20) NOT NULL COMMENT '路径ID',
  `current_node_id` bigint(20) NULL DEFAULT NULL COMMENT '当前节点ID',
  `progress` int(11) NULL DEFAULT 0 COMMENT '进度(0-100)',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'IN_PROGRESS' COMMENT '状态:IN_PROGRESS/COMPLETED/PAUSED',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_path`(`user_id` ASC, `path_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户学习路径进度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_learning_path
-- ----------------------------
INSERT INTO `user_learning_path` VALUES (1, 2, 1, 3, 35, '2026-02-01 00:00:00', NULL, 'IN_PROGRESS', '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `user_learning_path` VALUES (2, 2, 3, 1, 10, '2026-02-15 00:00:00', NULL, 'IN_PROGRESS', '2026-02-22 04:10:46', '2026-02-22 04:10:46');
INSERT INTO `user_learning_path` VALUES (3, 4, 1, NULL, 30, '2026-02-05 10:00:00', NULL, 'IN_PROGRESS', '2026-02-28 16:08:14', '2026-02-28 16:08:14');
INSERT INTO `user_learning_path` VALUES (4, 6, 2, NULL, 25, '2026-02-10 14:00:00', NULL, 'IN_PROGRESS', '2026-02-28 16:08:14', '2026-02-28 16:08:14');

-- ----------------------------
-- Table structure for writing_correction
-- ----------------------------
DROP TABLE IF EXISTS `writing_correction`;
CREATE TABLE `writing_correction`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作文标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作文内容',
  `word_count` int(11) NULL DEFAULT NULL COMMENT '字数',
  `total_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '总分',
  `content_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '内容分',
  `structure_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '结构分',
  `language_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '语言分',
  `vocabulary_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '词汇分',
  `errors` json NULL COMMENT '错误标注(JSON格式)',
  `suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI建议',
  `corrected_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '修改后内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作文批改记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of writing_correction
-- ----------------------------
INSERT INTO `writing_correction` VALUES (1, 2, 'The Importance of English Learning', 'Nowadays, English has become more and more important in our daily life. As a universal language, it plays a vital role in international communication.\n\nFirst and foremost, learning English can help us broaden our horizons. By reading English books and watching English movies, we can learn about different cultures and customs. Furthermore, English is essential for our career development. Many companies require employees to have good English skills...', 156, 82.00, 22.00, 20.00, 20.00, 20.00, '[{\"type\": \"grammar\", \"original\": \"more and more important\", \"position\": 15, \"suggestion\": \"increasingly important\", \"explanation\": \"使用更高级的词汇\"}, {\"type\": \"spelling\", \"original\": \"vital\", \"position\": 89, \"suggestion\": \"✓\", \"explanation\": \"拼写正确\"}]', '总体评价：\n1. 结构清晰，论证有力\n2. 词汇使用较为丰富\n3. 建议：可以增加一些具体例子使论述更生动\n4. 语法方面表现良好，个别地方可以使用更高级的表达', NULL, '2026-02-22 04:10:46');
INSERT INTO `writing_correction` VALUES (2, 2, 'My View on Online Learning', 'With the development of technology, online learning has become a popular way of education. In my opinion, online learning has both advantages and disadvantages.\n\nOn the one hand, online learning is very convenient. Students can study anytime and anywhere they want. On the other hand, online learning requires strong self-discipline...', 98, 75.00, 18.00, 19.00, 19.00, 19.00, '[{\"type\": \"grammar\", \"original\": \"has become\", \"position\": 45, \"suggestion\": \"✓\", \"explanation\": \"时态使用正确\"}, {\"type\": \"word_choice\", \"original\": \"very convenient\", \"position\": 78, \"suggestion\": \"extremely convenient\", \"explanation\": \"建议使用更强烈的副词\"}]', '总体评价：\n1. 观点明确，正反论述平衡\n2. 可以增加段落展开的深度\n3. 词汇使用准确，但可以更多样化\n4. 建议增加结尾段的总结', NULL, '2026-02-22 04:10:46');
INSERT INTO `writing_correction` VALUES (3, 2, 'The Importance of Reading', 'Reading is one of the most important skills that everyone should develop. In today\'s fast-paced world, many people tend to neglect reading, but its benefits are numerous and significant.\n\nFirst of all, reading helps to expand our knowledge and broaden our horizons. Through books, we can learn about different cultures, histories, and ideas that we might never encounter in our daily lives.\n\nSecondly, reading improves our language skills. By reading regularly, we can enhance our vocabulary, improve our grammar, and develop better writing abilities.\n\nIn conclusion, reading is a valuable habit that everyone should cultivate. It not only enriches our minds but also helps us become better communicators and thinkers.', 110, 70.00, 68.00, 67.00, 69.00, 72.00, '[{\"type\": \"grammar\", \"original\": \"example\", \"position\": 10, \"suggestion\": \"Example correction\"}]', 'Consider using more varied sentence structures and advanced vocabulary.', 'Reading is one of the most important skills that everyone should develop. In today\'s fast-paced world, many people tend to neglect reading, but its benefits are numerous and significant.\n\nFirst of all, reading helps to expand our knowledge and broaden our horizons. Through books, we can learn about different cultures, histories, and ideas that we might never encounter in our daily lives.\n\nSecondly, reading improves our language skills. By reading regularly, we can enhance our vocabulary, improve our grammar, and develop better writing abilities.\n\nIn conclusion, reading is a valuable habit that everyone should cultivate. It not only enriches our minds but also helps us become better communicators and thinkers.', '2026-02-27 03:43:39');
INSERT INTO `writing_correction` VALUES (4, 2, 'The Importance of Reading', 'Reading is one of the most important skills that everyone should develop. In today\'s fast-paced world, many people tend to neglect reading, but its benefits are numerous and significant.\n\nFirst of all, reading helps to expand our knowledge and broaden our horizons. Through books, we can learn about different cultures, histories, and ideas that we might never encounter in our daily lives.\n\nSecondly, reading improves our language skills. By reading regularly, we can enhance our vocabulary, improve our grammar, and develop better writing abilities.\n\nIn conclusion, reading is a valuable habit that everyone should cultivate. It not only enriches our minds but also helps us become better communicators and thinkers.', 110, 70.00, 68.00, 67.00, 69.00, 72.00, '[{\"type\": \"grammar\", \"original\": \"example\", \"position\": 10, \"suggestion\": \"Example correction\"}]', 'Consider using more varied sentence structures and advanced vocabulary.', 'Reading is one of the most important skills that everyone should develop. In today\'s fast-paced world, many people tend to neglect reading, but its benefits are numerous and significant.\n\nFirst of all, reading helps to expand our knowledge and broaden our horizons. Through books, we can learn about different cultures, histories, and ideas that we might never encounter in our daily lives.\n\nSecondly, reading improves our language skills. By reading regularly, we can enhance our vocabulary, improve our grammar, and develop better writing abilities.\n\nIn conclusion, reading is a valuable habit that everyone should cultivate. It not only enriches our minds but also helps us become better communicators and thinkers.', '2026-03-02 01:52:47');

-- ----------------------------
-- Table structure for wrong_question
-- ----------------------------
DROP TABLE IF EXISTS `wrong_question`;
CREATE TABLE `wrong_question`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `question_id` bigint(20) NOT NULL COMMENT '题目ID',
  `wrong_count` int(11) NULL DEFAULT 1 COMMENT '错误次数',
  `last_wrong_time` datetime NULL DEFAULT NULL COMMENT '最近错误时间',
  `mastery_level` tinyint(4) NULL DEFAULT 0 COMMENT '掌握程度:0未掌握1基本掌握2已掌握',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '笔记',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_mastery_level`(`mastery_level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '错题本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wrong_question
-- ----------------------------
INSERT INTO `wrong_question` VALUES (1, 2, 1, 3, '2026-02-20 11:45:00', 0, '听力题总是听不清楚关键词，需要多练习', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (2, 2, 2, 2, '2026-02-20 11:50:00', 0, '阅读理解题主旨大意总是理解错误', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (3, 2, 1, 2, '2026-02-19 15:00:00', 0, '单词拼写错误', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (4, 2, 2, 1, '2026-02-18 10:30:00', 0, '语法时态用错了', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (5, 2, 1, 1, '2026-02-18 10:45:00', 0, '介词搭配不熟悉', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (6, 2, 2, 2, '2026-02-19 14:45:00', 1, '复习后有进步，但还需巩固', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (7, 2, 1, 1, '2026-02-19 14:50:00', 1, '概念基本理解，细节需注意', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (8, 2, 2, 1, '2026-02-18 10:15:00', 1, '记住了解题技巧', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (9, 2, 1, 2, '2026-02-18 10:20:00', 1, '易混淆词汇已区分', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (10, 2, 2, 1, '2026-02-18 09:30:00', 1, '句型结构掌握了', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (11, 2, 1, 1, '2026-02-18 09:45:00', 2, '听力技巧在提升', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (12, 2, 1, 1, '2026-02-17 16:00:00', 2, '完全理解，不会再错', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (13, 2, 2, 1, '2026-02-17 16:10:00', 2, '已经熟练掌握', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (14, 2, 1, 1, '2026-02-16 15:30:00', 2, '牢固掌握该知识点', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (15, 2, 2, 1, '2026-02-16 15:45:00', 2, '反复练习后完全理解', '2026-02-22 04:10:46', '2026-02-22 04:10:46', 0);
INSERT INTO `wrong_question` VALUES (16, 2, 101, 3, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (17, 2, 102, 3, '2026-02-26 21:58:58', 1, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (18, 2, 103, 3, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (19, 2, 104, 3, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (20, 2, 105, 3, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (21, 2, 106, 3, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (22, 2, 107, 3, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (23, 2, 108, 4, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (24, 2, 109, 4, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (25, 2, 110, 3, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (26, 2, 115, 4, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (27, 2, 116, 4, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (28, 2, 117, 4, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (29, 2, 111, 1, '2026-02-23 02:31:48', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (30, 2, 113, 1, '2026-02-23 02:31:48', 0, NULL, '2026-02-23 02:31:48', '2026-02-23 02:31:48', 0);
INSERT INTO `wrong_question` VALUES (31, 2, 118, 1, '2026-02-23 02:36:36', 0, NULL, '2026-02-23 02:36:36', '2026-02-23 02:36:36', 0);
INSERT INTO `wrong_question` VALUES (32, 2, 119, 1, '2026-02-23 02:36:36', 0, NULL, '2026-02-23 02:36:36', '2026-02-23 02:36:36', 0);
INSERT INTO `wrong_question` VALUES (33, 2, 120, 1, '2026-02-23 02:36:36', 0, NULL, '2026-02-23 02:36:36', '2026-02-23 02:36:36', 0);
INSERT INTO `wrong_question` VALUES (34, 2, 114, 3, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:36:36', '2026-02-23 02:36:36', 0);
INSERT INTO `wrong_question` VALUES (35, 2, 112, 2, '2026-02-26 21:58:58', 0, NULL, '2026-02-23 02:44:05', '2026-02-23 02:44:05', 0);
INSERT INTO `wrong_question` VALUES (36, 2, 218, 1, '2026-02-23 19:42:11', 0, NULL, '2026-02-23 19:42:11', '2026-02-23 19:42:11', 0);

SET FOREIGN_KEY_CHECKS = 1;
