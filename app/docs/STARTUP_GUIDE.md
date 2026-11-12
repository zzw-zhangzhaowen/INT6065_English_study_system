# 启动步骤

## 1. 环境准备

### 必需软件
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.2+
- Node.js 18+

## 2. 后端启动

### 2.1 数据库初始化
```bash
# 登录MySQL
mysql -u root -p

# 执行数据库初始化脚本
source backend/src/main/resources/sql/schema.sql
source backend/src/main/resources/sql/init-data.sql
```

### 2.2 修改配置
编辑 `backend/src/main/resources/application.yml`：
- 修改MySQL连接信息（用户名/密码）
- 修改Redis连接信息

### 2.3 启动后端
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端启动成功后访问：
- API文档：http://localhost:8080/swagger-ui.html
- 健康检查：http://localhost:8080/api/test/health

## 3. 前端启动

### 3.1 安装依赖
```bash
cd frontend
npm install
```

### 3.2 启动开发服务器
```bash
npm run dev
```

前端启动成功后访问：http://localhost:5173

## 4. 测试账号

- 管理员：admin / admin123
- 学生：student1 / student123
- 教师：teacher1 / teacher123

## 5. 主要功能模块

### 学生端
- 在线学习（学习资源、错题本、笔记）
- 在线考试（模拟考试、每日练习）
- AI智能辅导（聊天、写作批改、发音评估）
- 社区互动（发帖、回复、学习小组）

### 教师端
- 题库管理（题目录入、分类管理）
- 试卷管理（组卷、发布考试）
- 学生管理（查看学习进度、成绩分析）
- 资源管理（上传学习资料）

### 管理员端
- 用户管理（增删改查、权限分配）
- 系统配置（参数设置）
- 日志审计（操作日志）
- 系统监控（性能监控）

## 6. 技术架构

### 后端技术栈
- Spring Boot 3.2.5
- Spring Security + JWT
- MyBatis-Plus
- MySQL 8.0
- Redis 7.2
- SpringDoc OpenAPI

### 前端技术栈
- Vue 3.4
- TypeScript 5.3
- Element Plus 2.4
- Pinia 2.1
- Vue Router 4.2
- Vite 5.1

## 7. 项目结构

```
english-learning-system/
├── backend/                    # 后端项目
│   ├── src/main/java/
│   │   └── com/english/learning/
│   │       ├── controller/     # 控制器层
│   │       ├── service/        # 服务层
│   │       ├── mapper/         # 数据访问层
│   │       ├── entity/         # 实体类
│   │       ├── dto/            # 数据传输对象
│   │       ├── config/         # 配置类
│   │       └── common/         # 通用组件
│   └── src/main/resources/
│       ├── application.yml     # 配置文件
│       └── sql/                # SQL脚本
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── layouts/            # 布局组件
│   │   ├── router/             # 路由配置
│   │   └── styles/             # 样式文件
│   └── package.json
└── README.md
```

## 8. API接口说明

### 认证相关
- POST /api/auth/login - 用户登录
- POST /api/auth/register - 用户注册
- POST /api/auth/logout - 退出登录

### 题库管理
- GET /api/question/page - 分页查询题目
- POST /api/question - 创建题目
- PUT /api/question - 更新题目
- DELETE /api/question/{id} - 删除题目

### 考试系统
- GET /api/exam/page - 分页查询考试
- POST /api/exam/start - 开始考试
- POST /api/exam/submit - 提交考试

### AI功能
- POST /api/ai/chat - AI对话
- POST /api/ai/writing/correct - 写作批改
- POST /api/ai/pronunciation/evaluate - 发音评估

### 学习资源
- GET /api/learning/resource/page - 查询学习资源

### 社区互动
- GET /api/community/post/page - 查询帖子列表
- POST /api/community/post - 发布帖子

### 系统管理
- GET /api/admin/user/page - 查询用户列表
- PUT /api/admin/user/status - 更新用户状态

## 9. 开发注意事项

1. 所有API接口都需要JWT认证（除登录/注册接口外）
2. 请求头需携带：`Authorization: Bearer {token}`
3. 跨域已配置，前端可直接调用后端接口
4. 所有接口统一返回格式：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1234567890
}
```

## 10. 常见问题

### Q1: 数据库连接失败
A: 检查MySQL是否启动，配置文件中的用户名密码是否正确

### Q2: Redis连接失败
A: 检查Redis是否启动，端口是否正确

### Q3: 前端跨域问题
A: 后端已配置CORS，如仍有问题请检查vite.config.js中的proxy配置

### Q4: JWT Token过期
A: Token默认有效期24小时，过期后需重新登录
