-- 英语等级智能学习与考试系统数据库初始化脚本

CREATE DATABASE IF NOT EXISTS english_learning DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE english_learning;

-- ==================== 用户管理模块 ====================

-- 用户表
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar VARCHAR(255) COMMENT '头像URL',
    gender TINYINT COMMENT '性别:0女1男',
    birthday DATE COMMENT '生日',
    status TINYINT DEFAULT 1 COMMENT '状态:0禁用1正常',
    last_login_time DATETIME COMMENT '最近登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态:0禁用1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 学生信息扩展表
CREATE TABLE student_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    school VARCHAR(100) COMMENT '学校',
    major VARCHAR(100) COMMENT '专业',
    grade VARCHAR(50) COMMENT '年级',
    student_no VARCHAR(50) COMMENT '学号',
    current_level VARCHAR(20) COMMENT '当前等级',
    target_level VARCHAR(20) COMMENT '目标等级',
    study_days INT DEFAULT 0 COMMENT '学习天数',
    continuous_days INT DEFAULT 0 COMMENT '连续学习天数',
    total_study_time INT DEFAULT 0 COMMENT '总学习时长(分钟)',
    achievement_score INT DEFAULT 0 COMMENT '成就积分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 教学班级表
CREATE TABLE class_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '班级ID',
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    grade VARCHAR(50) COMMENT '年级',
    description VARCHAR(255) COMMENT '班级描述',
    status TINYINT DEFAULT 1 COMMENT '状态:0禁用1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学班级表';

-- 教师-班级关联表
CREATE TABLE teacher_class (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    teacher_id BIGINT NOT NULL COMMENT '教师用户ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_class_id (class_id),
    UNIQUE KEY uk_teacher_class (teacher_id, class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师班级关联表';

-- 班级-学生关联表
CREATE TABLE class_student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    student_id BIGINT NOT NULL COMMENT '学生用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_class_id (class_id),
    INDEX idx_student_id (student_id),
    UNIQUE KEY uk_class_student (class_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级学生关联表';

-- 教师信息扩展表
CREATE TABLE teacher_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    institution VARCHAR(100) COMMENT '任教机构',
    title VARCHAR(50) COMMENT '职称',
    teacher_no VARCHAR(50) COMMENT '教师编号',
    specialization VARCHAR(255) COMMENT '专业特长',
    intro TEXT COMMENT '个人简介',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- ==================== 题库管理模块 ====================

-- 题目分类表
CREATE TABLE question_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    level VARCHAR(20) COMMENT '等级:CET4/CET6/IELTS/TOEFL',
    skill_type VARCHAR(20) COMMENT '技能类型:听力/阅读/写作/翻译',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_parent_id (parent_id),
    INDEX idx_category_code (category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目分类表';

-- 题目表
CREATE TABLE question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '题目ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    question_type VARCHAR(20) NOT NULL COMMENT '题型:单选/多选/填空/问答/听力/作文',
    title TEXT NOT NULL COMMENT '题目标题',
    content TEXT COMMENT '题目内容',
    passage_id BIGINT DEFAULT NULL COMMENT '关联的文章ID',
    options JSON COMMENT '选项(JSON格式)',
    answer TEXT COMMENT '答案',
    analysis TEXT COMMENT '解析',
    difficulty TINYINT DEFAULT 1 COMMENT '难度:1简单2中等3困难',
    score DECIMAL(5,2) DEFAULT 1.00 COMMENT '分值',
    audio_url VARCHAR(255) COMMENT '音频URL',
    video_url VARCHAR(255) COMMENT '视频URL',
    image_url VARCHAR(255) COMMENT '图片URL',
    keywords VARCHAR(255) COMMENT '关键词',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    correct_rate DECIMAL(5,2) DEFAULT 0 COMMENT '正确率',
    creator_id BIGINT COMMENT '创建人ID',
    status TINYINT DEFAULT 1 COMMENT '状态:0禁用1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_category_id (category_id),
    INDEX idx_question_type (question_type),
    INDEX idx_difficulty (difficulty),
    INDEX idx_creator_id (creator_id),
    INDEX idx_passage_id (passage_id),
    INDEX idx_status_create_time (status, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- ==================== 考试管理模块 ====================

-- 考试表
CREATE TABLE exam (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考试ID',
    exam_name VARCHAR(100) NOT NULL COMMENT '考试名称',
    exam_type VARCHAR(20) NOT NULL COMMENT '考试类型:模拟/正式/章节/每日',
    level VARCHAR(20) COMMENT '等级',
    description TEXT COMMENT '考试说明',
    total_score DECIMAL(10,2) DEFAULT 100.00 COMMENT '总分',
    pass_score DECIMAL(10,2) DEFAULT 60.00 COMMENT '及格分',
    duration INT NOT NULL COMMENT '考试时长(分钟)',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    max_attempts INT DEFAULT 1 COMMENT '最大尝试次数',
    enable_anti_cheat TINYINT DEFAULT 1 COMMENT '是否启用防作弊',
    random_question TINYINT DEFAULT 0 COMMENT '是否随机题目',
    random_option TINYINT DEFAULT 0 COMMENT '是否随机选项',
    show_result TINYINT DEFAULT 1 COMMENT '考后是否显示结果',
    creator_id BIGINT COMMENT '创建人ID',
    status TINYINT DEFAULT 1 COMMENT '状态:0禁用1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_exam_type (exam_type),
    INDEX idx_level (level),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time),
    INDEX idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

-- 考试题目关联表
CREATE TABLE exam_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    question_order INT DEFAULT 0 COMMENT '题目顺序',
    question_score DECIMAL(5,2) DEFAULT 1.00 COMMENT '该题分数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_exam_id (exam_id),
    INDEX idx_question_id (question_id),
    UNIQUE KEY uk_exam_question (exam_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试题目关联表';

-- 考试记录表
CREATE TABLE exam_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    submit_time DATETIME COMMENT '提交时间',
    duration INT COMMENT '实际用时(分钟)',
    total_score DECIMAL(10,2) COMMENT '总分',
    obtained_score DECIMAL(10,2) DEFAULT 0 COMMENT '得分',
    correct_count INT DEFAULT 0 COMMENT '正确题数',
    wrong_count INT DEFAULT 0 COMMENT '错误题数',
    rank_position INT COMMENT '排名',
    status VARCHAR(20) DEFAULT 'IN_PROGRESS' COMMENT '状态:IN_PROGRESS/COMPLETED/TIMEOUT',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    browser_info VARCHAR(255) COMMENT '浏览器信息',
    cheat_warning_count INT DEFAULT 0 COMMENT '作弊警告次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_exam_id (exam_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status),
    INDEX idx_exam_student (exam_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- 答题记录表
CREATE TABLE answer_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    exam_record_id BIGINT NOT NULL COMMENT '考试记录ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    student_answer TEXT COMMENT '学生答案',
    is_correct TINYINT COMMENT '是否正确:0错误1正确',
    obtained_score DECIMAL(5,2) DEFAULT 0 COMMENT '得分',
    answer_time INT COMMENT '答题用时(秒)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_exam_record_id (exam_record_id),
    INDEX idx_question_id (question_id),
    INDEX idx_is_correct (is_correct)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录表';

-- ==================== 学习中心模块 ====================

-- 学习资源表
CREATE TABLE learning_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资源ID',
    resource_name VARCHAR(100) NOT NULL COMMENT '资源名称',
    resource_type VARCHAR(20) NOT NULL COMMENT '资源类型:视频/音频/文档/图片',
    level VARCHAR(20) COMMENT '等级',
    skill_type VARCHAR(20) COMMENT '技能类型',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    description TEXT COMMENT '描述',
    file_url VARCHAR(255) DEFAULT '' COMMENT '文件URL',
    cover_url VARCHAR(255) COMMENT '封面URL',
    file_size BIGINT COMMENT '文件大小(字节)',
    duration INT COMMENT '时长(秒)',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    tags VARCHAR(255) COMMENT '标签',
    creator_id BIGINT COMMENT '创建者ID',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_resource_type (resource_type),
    INDEX idx_level (level),
    INDEX idx_skill_type (skill_type),
    INDEX idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资源表';

-- 资源分类表
CREATE TABLE resource_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0为顶级分类',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    description VARCHAR(255) COMMENT '分类描述',
    creator_id BIGINT COMMENT '创建者ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源分类表';

-- 资源收藏表
CREATE TABLE resource_collection (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_user_id (user_id),
    INDEX idx_resource_id (resource_id),
    UNIQUE KEY uk_user_resource (user_id, resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源收藏表';

-- 资源点赞表
CREATE TABLE resource_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_user_resource (user_id, resource_id),
    INDEX idx_resource_id (resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源点赞表';

-- 错题本表
CREATE TABLE wrong_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    wrong_count INT DEFAULT 1 COMMENT '错误次数',
    last_wrong_time DATETIME COMMENT '最近错误时间',
    mastery_level TINYINT DEFAULT 0 COMMENT '掌握程度:0未掌握1基本掌握2已掌握',
    notes TEXT COMMENT '笔记',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_student_id (student_id),
    INDEX idx_question_id (question_id),
    INDEX idx_mastery_level (mastery_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错题本表';

-- 学习笔记表
CREATE TABLE study_note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '笔记ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    category VARCHAR(50) COMMENT '分类',
    tags VARCHAR(255) COMMENT '标签',
    is_shared TINYINT DEFAULT 0 COMMENT '是否分享',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_student_id (student_id),
    INDEX idx_category (category),
    INDEX idx_is_shared (is_shared)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习笔记表';

-- 学习计划表
CREATE TABLE study_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '计划ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    plan_name VARCHAR(100) NOT NULL COMMENT '计划名称',
    target_level VARCHAR(20) COMMENT '目标等级',
    target_score DECIMAL(10,2) COMMENT '目标分数',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    daily_duration INT COMMENT '每日学习时长(分钟)',
    daily_questions INT COMMENT '每日题目数',
    progress INT DEFAULT 0 COMMENT '进度(0-100)',
    status VARCHAR(20) DEFAULT 'IN_PROGRESS' COMMENT '状态:IN_PROGRESS/COMPLETED/SUSPENDED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习计划表';

-- ==================== 社区互动模块 ====================

-- 帖子表
CREATE TABLE community_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '帖子ID',
    category_id BIGINT COMMENT '分类ID',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    post_type VARCHAR(20) DEFAULT 'DISCUSSION' COMMENT '类型:DISCUSSION/QUESTION/SHARE/ANNOUNCEMENT',
    is_topped TINYINT DEFAULT 0 COMMENT '是否置顶',
    is_featured TINYINT DEFAULT 0 COMMENT '是否精华',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    reply_count INT DEFAULT 0 COMMENT '回复数',
    collect_count INT DEFAULT 0 COMMENT '收藏数',
    last_reply_time DATETIME COMMENT '最后回复时间',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_category_id (category_id),
    INDEX idx_author_id (author_id),
    INDEX idx_post_type (post_type),
    FULLTEXT idx_title_content (title, content) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子表';

-- 帖子回复表
CREATE TABLE post_reply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '回复ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父回复ID',
    content TEXT NOT NULL COMMENT '回复内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    is_best_answer TINYINT DEFAULT 0 COMMENT '是否最佳答案',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子回复表';

-- 回复点赞记录表
CREATE TABLE reply_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    reply_id BIGINT NOT NULL COMMENT '回复ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_reply_user (reply_id, user_id),
    INDEX idx_reply_id (reply_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回复点赞记录表';

-- 帖子点赞记录表
CREATE TABLE post_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_post_user (post_id, user_id),
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞记录表';

-- 帖子收藏记录表
CREATE TABLE post_collection (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_post_user (post_id, user_id),
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子收藏记录表';

-- 学习小组表
CREATE TABLE study_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '小组ID',
    group_name VARCHAR(100) NOT NULL COMMENT '小组名称',
    group_avatar VARCHAR(255) COMMENT '小组头像',
    group_intro TEXT COMMENT '小组简介',
    leader_id BIGINT NOT NULL COMMENT '组长ID',
    max_members INT DEFAULT 100 COMMENT '最大成员数',
    current_members INT DEFAULT 1 COMMENT '当前成员数',
    activity_level VARCHAR(20) COMMENT '活跃度:LOW/MEDIUM/HIGH',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_leader_id (leader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习小组表';

-- 小组成员表
CREATE TABLE group_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    group_id BIGINT NOT NULL COMMENT '小组ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    member_role VARCHAR(20) DEFAULT 'MEMBER' COMMENT '角色:LEADER/ADMIN/MEMBER',
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    INDEX idx_group_id (group_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小组成员表';

-- ==================== 在线课堂模块 ====================

-- 课堂表
CREATE TABLE classroom (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课堂ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    classroom_name VARCHAR(100) NOT NULL COMMENT '课堂名称',
    classroom_type VARCHAR(20) DEFAULT 'LIVE' COMMENT '类型:LIVE/RECORDED',
    description TEXT COMMENT '描述',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    duration INT COMMENT '时长(分钟)',
    max_students INT DEFAULT 100 COMMENT '最大学生数',
    current_students INT DEFAULT 0 COMMENT '当前学生数',
    room_url VARCHAR(255) COMMENT '课堂链接',
    record_url VARCHAR(255) COMMENT '录播链接',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态:PENDING/LIVE/ENDED',
    sign_in_active TINYINT(1) NOT NULL DEFAULT 0 COMMENT '签到是否激活',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂表';

-- 课堂参与记录表
CREATE TABLE classroom_participant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    classroom_id BIGINT NOT NULL COMMENT '课堂ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    join_time DATETIME COMMENT '加入时间',
    leave_time DATETIME COMMENT '离开时间',
    sign_in_time DATETIME COMMENT '签到时间',
    duration INT COMMENT '参与时长(分钟)',
    is_signed TINYINT DEFAULT 0 COMMENT '是否签到',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_classroom_id (classroom_id),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂参与记录表';

-- ==================== AI辅导模块 ====================

-- AI对话记录表
CREATE TABLE ai_chat_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_id VARCHAR(100) NOT NULL COMMENT '会话ID',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型:USER/AI',
    message_content TEXT NOT NULL COMMENT '消息内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话记录表';

-- 作文批改记录表
CREATE TABLE writing_correction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    title VARCHAR(200) COMMENT '作文标题',
    content TEXT NOT NULL COMMENT '作文内容',
    word_count INT COMMENT '字数',
    total_score DECIMAL(5,2) COMMENT '总分',
    content_score DECIMAL(5,2) COMMENT '内容分',
    structure_score DECIMAL(5,2) COMMENT '结构分',
    language_score DECIMAL(5,2) COMMENT '语言分',
    vocabulary_score DECIMAL(5,2) COMMENT '词汇分',
    errors JSON COMMENT '错误标注(JSON格式)',
    suggestions TEXT COMMENT 'AI建议',
    corrected_content TEXT COMMENT '修改后内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作文批改记录表';

-- 发音评测记录表
CREATE TABLE pronunciation_evaluation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    text_content TEXT NOT NULL COMMENT '朗读文本',
    audio_url VARCHAR(255) COMMENT '录音URL',
    total_score DECIMAL(5,2) COMMENT '总分',
    accuracy_score DECIMAL(5,2) COMMENT '准确度分',
    fluency_score DECIMAL(5,2) COMMENT '流利度分',
    intonation_score DECIMAL(5,2) COMMENT '语调分',
    speed_score DECIMAL(5,2) COMMENT '语速分',
    recognized_text TEXT COMMENT '识别文本',
    error_details JSON COMMENT '错误详情(JSON格式)',
    suggestions TEXT COMMENT '改进建议',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发音评测记录表';

-- ==================== 系统管理模块 ====================

-- 系统配置表
CREATE TABLE sys_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态:0禁用1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记:0未删除1已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 操作日志表
CREATE TABLE sys_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    location VARCHAR(100) COMMENT '地理位置',
    browser VARCHAR(100) COMMENT '浏览器',
    os VARCHAR(100) COMMENT '操作系统',
    status TINYINT DEFAULT 1 COMMENT '状态:0失败1成功',
    error_msg TEXT COMMENT '错误信息',
    execute_time INT COMMENT '执行时长(毫秒)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 系统消息表
CREATE TABLE sys_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT COMMENT '消息内容',
    type VARCHAR(20) DEFAULT 'NOTIFICATION' COMMENT '消息类型:NOTIFICATION-通知,REMINDER-提醒,BROADCAST-广播,PRIVATE-私信',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读:0-未读,1-已读',
    read_time DATETIME COMMENT '阅读时间',
    sender_deleted TINYINT DEFAULT 0 COMMENT '发送者是否删除:0-否,1-是',
    receiver_deleted TINYINT DEFAULT 0 COMMENT '接收者是否删除:0-否,1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_is_read (is_read),
    INDEX idx_type (type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统消息表';

-- 每日打卡表
CREATE TABLE daily_checkin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '打卡ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    checkin_date DATE NOT NULL COMMENT '打卡日期',
    checkin_time DATETIME COMMENT '打卡时间',
    study_duration INT DEFAULT 0 COMMENT '学习时长(分钟)',
    word_count INT DEFAULT 0 COMMENT '学习单词数',
    question_count INT DEFAULT 0 COMMENT '做题数量',
    notes TEXT COMMENT '学习心得',
    mood VARCHAR(20) COMMENT '心情:HAPPY/NORMAL/SAD',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_date (user_id, checkin_date),
    INDEX idx_user_id (user_id),
    INDEX idx_checkin_date (checkin_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日打卡表';

-- 作业表
CREATE TABLE homework (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '作业ID',
    title VARCHAR(200) NOT NULL COMMENT '作业标题',
    description TEXT COMMENT '作业描述',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    level VARCHAR(20) COMMENT '等级',
    homework_type VARCHAR(20) DEFAULT 'NORMAL' COMMENT '类型:NORMAL/EXAM/PRACTICE',
    deadline DATETIME COMMENT '截止时间',
    total_score DECIMAL(5,2) DEFAULT 100.00 COMMENT '总分',
    attachment_url VARCHAR(255) COMMENT '附件URL',
    status TINYINT DEFAULT 1 COMMENT '状态:0禁用1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_deadline (deadline)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- 作业提交表
CREATE TABLE homework_submission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '提交ID',
    homework_id BIGINT NOT NULL COMMENT '作业ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    content TEXT COMMENT '作业内容',
    attachment_url VARCHAR(255) COMMENT '附件URL',
    submit_time DATETIME COMMENT '提交时间',
    score DECIMAL(5,2) COMMENT '得分',
    feedback TEXT COMMENT '教师反馈',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态:PENDING/SUBMITTED/GRADED/RETURNED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_homework_id (homework_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- 学习路径表
CREATE TABLE learning_path (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '路径ID',
    path_name VARCHAR(100) NOT NULL COMMENT '路径名称',
    description TEXT COMMENT '路径描述',
    target_level VARCHAR(20) COMMENT '目标等级',
    difficulty TINYINT DEFAULT 1 COMMENT '难度:1初级2中级3高级',
    estimated_days INT COMMENT '预计天数',
    cover_url VARCHAR(255) COMMENT '封面URL',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习路径表';

-- 学习路径节点表
CREATE TABLE learning_path_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '节点ID',
    path_id BIGINT NOT NULL COMMENT '路径ID',
    node_name VARCHAR(100) NOT NULL COMMENT '节点名称',
    node_type VARCHAR(20) COMMENT '节点类型:VIDEO/ARTICLE/QUIZ/PRACTICE',
    content TEXT COMMENT '节点内容',
    resource_id BIGINT COMMENT '关联资源ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    estimated_minutes INT COMMENT '预计时长(分钟)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_path_id (path_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习路径节点表';

-- 用户学习路径进度表
CREATE TABLE user_learning_path (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    path_id BIGINT NOT NULL COMMENT '路径ID',
    current_node_id BIGINT COMMENT '当前节点ID',
    progress INT DEFAULT 0 COMMENT '进度(0-100)',
    start_time DATETIME COMMENT '开始时间',
    complete_time DATETIME COMMENT '完成时间',
    status VARCHAR(20) DEFAULT 'IN_PROGRESS' COMMENT '状态:IN_PROGRESS/COMPLETED/PAUSED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_path (user_id, path_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户学习路径进度表';

-- ==================== 消息通知模块 ====================
-- 注意：sys_message 表已在系统管理模块中定义（第497-514行），此处不重复定义

-- ==================== 初始数据 ====================

-- 插入角色数据
INSERT INTO sys_role (role_name, role_code, description) VALUES
('学生', 'ROLE_STUDENT', '学生角色'),
('教师', 'ROLE_TEACHER', '教师角色'),
('管理员', 'ROLE_ADMIN', '管理员角色');

-- 插入默认管理员账号 (密码: 123456)
INSERT INTO sys_user (username, password, email, real_name, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@example.com', '系统管理员', 1);

-- 关联管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 3);

-- 插入题目分类示例数据
INSERT INTO question_category (parent_id, category_name, category_code, level, skill_type) VALUES
(0, '英语四级', 'CET4', 'CET4', NULL),
(0, '英语六级', 'CET6', 'CET6', NULL),
(1, '听力理解', 'CET4_LISTENING', 'CET4', '听力'),
(1, '阅读理解', 'CET4_READING', 'CET4', '阅读'),
(1, '写作翻译', 'CET4_WRITING', 'CET4', '写作'),
(2, '听力理解', 'CET6_LISTENING', 'CET6', '听力'),
(2, '阅读理解', 'CET6_READING', 'CET6', '阅读'),
(2, '写作翻译', 'CET6_WRITING', 'CET6', '写作');

-- 插入系统配置
INSERT INTO sys_config (config_key, config_value, description) VALUES
('system.name', '英语等级智能学习与考试系统', '系统名称'),
('system.version', '1.0.0', '系统版本'),
('exam.anti_cheat.enabled', 'true', '是否启用防作弊'),
('file.upload.max_size', '104857600', '文件上传最大大小(字节)'),
('ai.chat.enabled', 'true', 'AI聊天功能是否启用');
