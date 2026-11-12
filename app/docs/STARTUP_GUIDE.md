# 启动指南

本项目的推荐启动方式是根目录的一键启动脚本 `start-all.bat`，无需手动进入各子目录。

## 1. 环境准备

### 演示模式（仅看功能）
- Node.js 18+

### 全量模式（真实前后端）
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.2+
- Node.js 18+

## 2. 一键启动

```bat
start-all.bat          :: 纯演示模式（推荐，不依赖后端与数据库）
start-all.bat full     :: 前端 + 后端一起启动
start-all.bat build    :: 打包静态站点到 app\frontend\dist
start-all.bat stop     :: 停止所有服务
start-all.bat status   :: 查看端口占用
```

首次启动会自动安装前端依赖。前端默认端口 `3002`，后端默认端口 `8080`。

### 演示模式说明

演示模式不请求任何后端接口，所有数据来自内置模拟数据（稳定伪随机，刷新不跳变）。适合演示、答辩与离线预览。

## 3. 手动启动

### 3.1 前端

```bash
cd app/frontend
npm install
npm run dev:mock     # 纯演示模式
npm run dev          # 连接后端开发（需先启动后端）
```

前端启动后访问：http://localhost:3002

### 3.2 后端

```bash
cd app/backend
start.bat            # Windows 一键启动（自动检查环境并启动 jar）
```

或手动执行：

```bash
cd app/backend
mvn clean package -DskipTests
java -jar english-learning-system-1.0.0.jar
```

后端启动后访问：
- API 文档：http://localhost:8080/swagger-ui.html
- 健康检查：http://localhost:8080/api/test/health

### 3.3 数据库初始化

```bash
mysql -u root -p
source app/backend/src/main/resources/sql/schema.sql
source app/backend/src/main/resources/sql/init-data.sql
```

随后编辑 `app/backend/src/main/resources/application.yml`，填写 MySQL 与 Redis 连接信息。

## 4. 账号说明

演示模式下密码不做校验（前端要求不少于 6 位），**用户名决定登录角色**：

| 角色 | 用户名 | 登录后跳转 |
| --- | --- | --- |
| 学生 | `student` 或任意普通名字 | `/student/dashboard` |
| 教师 | `teacher` | `/teacher/dashboard` |
| 管理员 | `admin` | `/admin/dashboard` |

连接真实后端时，请使用数据库中已初始化的账号。

## 5. 主要功能模块

### 学生端
- 学习仪表板（今日任务、错题待复习、学习进度与能力雷达）
- 考试中心（CET4 / CET6 / IELTS 模拟考试，在线答题与判分）
- 题库练习、学习资源、学习计划
- AI 智能辅导（对话答疑、作文批改、错题溯源）

### 教师端
- 教学仪表板（班级学情、成绩分布、活跃度）
- 题库管理、考试管理、作业管理、学习资源、学生管理
- AI 教学助手（提交情况汇总、作文批量批改、讲评稿生成）

### 管理员端
- 用户管理、班级管理、系统配置、日志审计、系统监控

## 6. 技术架构

### 前端
- Vue 3（`<script setup>`）
- Vite 5
- Element Plus 2.4
- Pinia
- Vue Router 4（hash 模式）
- ECharts

### 后端
- Spring Boot 3.2.5
- Spring Security + JWT
- MyBatis-Plus
- MySQL 8.0
- Redis 7.2
- SpringDoc OpenAPI

### AI 模块
统一由 Provider 接口管理，三档实现可切换，失败自动降级：

| 档位 | 说明 |
| --- | --- |
| `local` | 内置规则引擎，零依赖、可解释，演示默认 |
| `backend` | 调用后端 AI 服务 |
| `llm` | 预留真实大模型接口，填入 API Key 即可启用 |

## 7. 项目结构

```
english/
├── start-all.bat               # 唯一启动入口
├── .github/workflows/          # GitHub Pages 自动部署
└── app/
    ├── frontend/
    │   ├── src/views/          # 学生端 / 教师端 / 管理端页面
    │   ├── src/layouts/        # 三端布局
    │   ├── src/mock/           # 内置模拟数据与接口映射
    │   ├── src/api/            # 接口封装
    │   ├── src/utils/          # AI Provider、请求、文件等
    │   └── .env.pages          # 演示模式环境变量
    ├── backend/
    │   └── src/main/java/com/english/learning/
    │       ├── controller/     # 控制器
    │       ├── service/        # 服务层
    │       ├── mapper/         # 数据访问
    │       ├── entity/         # 实体
    │       ├── dto/            # 传输对象
    │       ├── config/         # 配置
    │       └── common/         # 通用组件
    ├── docs/                   # 文档
    └── tools/                  # 辅助脚本
```

## 8. API 接口

### 认证
- `POST /api/auth/login` 用户登录
- `POST /api/auth/register` 用户注册
- `GET /api/user/info` 获取当前用户

### 题库
- `GET /api/question/page` 分页查询题目
- `GET /api/question-category/tree` 分类树

### 考试
- `GET /api/exam/page` 分页查询考试
- `POST /api/exam/start` 开始考试
- `POST /api/exam/submit` 提交试卷
- `GET /api/exam/record/{id}` 考试记录详情

### AI
- `POST /api/ai/chat` 对话答疑
- `POST /api/ai/writing/correct` 写作批改

### 学习资源
- `GET /api/learning/resource/page` 查询学习资源

### 系统管理
- `GET /api/admin/user/page` 查询用户列表
- `PUT /api/admin/user/status` 更新用户状态

## 9. 开发注意事项

1. 除登录/注册外，接口需携带 `Authorization: Bearer {token}`
2. 接口统一返回格式：
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1234567890
}
```
3. 前端路由使用 hash 模式，配合 `base: './'`，可直接部署到任意静态子路径或本地双击打开

## 10. 常见问题

### Q1: 数据库连接失败
检查 MySQL 是否启动，`application.yml` 中的用户名密码是否正确。

### Q2: Redis 连接失败
检查 Redis 是否启动、端口是否正确。

### Q3: `full` 模式打开后页面数据为空
说明后端未成功启动，最常见原因是 MySQL 或 Redis 没有运行。查看「后端服务」窗口的报错，或直接改用 `start-all.bat demo`。

### Q4: 前端跨域
后端已配置 CORS；开发模式下 `vite.config.js` 已配置 `/api` 代理。

### Q5: JWT Token 过期
Token 默认有效期 24 小时，过期后需重新登录。
