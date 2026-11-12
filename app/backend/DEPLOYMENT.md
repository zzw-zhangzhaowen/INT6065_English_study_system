# 英语等级智能学习与考试系统 - 部署与测试指南

## 一、环境要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 推荐Eclipse Temurin |
| MySQL | 8.0+ | 数据库 |
| Redis | 7.0+ | 缓存服务 |
| Node.js | 18+ | 前端构建 |
| Maven | 3.6+ | 后端构建 |

## 二、快速部署

### 2.1 一键部署（推荐）

```bash
# 1. 运行安装指南查看依赖
install-guide.bat

# 2. 初始化数据库
init-db.bat

# 3. 一键编译部署
deploy.bat

# 4. 启动后端服务
start.bat
```

### 2.2 手动部署

#### 后端部署
```bash
cd backend

# 编译
mvn clean package -DskipTests

# 启动
java -jar target/english-learning-system-1.0.0.jar
```

#### 前端部署
```bash
cd frontend

# 安装依赖
npm install

# 开发模式
npm run dev

# 生产构建
npm run build
```

## 三、部署脚本说明

| 脚本 | 功能 |
|------|------|
| `start.bat` | 启动后端服务 |
| `stop.bat` | 停止后端服务 |
| `init-db.bat` | 初始化数据库 |
| `deploy.bat` | 一键编译部署 |
| `install-guide.bat` | 查看安装指南 |
| `run-tests.bat` | 运行测试 |

## 四、测试说明

### 4.1 测试结构

```
src/test/java/com/english/learning/
├── controller/           # Controller层单元测试
│   ├── AuthControllerTest.java
│   ├── QuestionControllerTest.java
│   ├── ExamControllerTest.java
│   ├── AIControllerTest.java
│   ├── ClassroomControllerTest.java
│   └── CommunityControllerTest.java
├── service/              # Service层单元测试
│   ├── AuthServiceTest.java
│   ├── QuestionServiceTest.java
│   ├── ExamServiceTest.java
│   ├── AIServiceTest.java
│   ├── LearningResourceServiceTest.java
│   ├── CommunityServiceTest.java
│   └── ClassroomServiceTest.java
└── integration/          # 集成测试
    ├── AuthIntegrationTest.java
    ├── ExamIntegrationTest.java
    ├── LearningCenterIntegrationTest.java
    ├── AIIntegrationTest.java
    ├── CommunityIntegrationTest.java
    └── ClassroomIntegrationTest.java
```

### 4.2 运行测试

```bash
# 运行所有测试
mvn test

# 运行单元测试
mvn test -Dtest=*ServiceTest,*ControllerTest

# 运行集成测试
mvn test -Dtest=*IntegrationTest -Dspring.profiles.active=test

# 生成测试报告
mvn surefire-report:report

# 或使用脚本
run-tests.bat
```

### 4.3 测试覆盖模块

| 模块 | 单元测试 | 集成测试 |
|------|----------|----------|
| 用户认证 | ✅ | ✅ |
| 题库管理 | ✅ | ✅ |
| 在线考试 | ✅ | ✅ |
| 学习中心 | ✅ | ✅ |
| AI功能 | ✅ | ✅ |
| 社区功能 | ✅ | ✅ |
| 在线课堂 | ✅ | ✅ |

## 五、默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |

## 六、访问地址

- 前端页面: http://localhost:5173 (开发模式)
- 后端API: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html
- H2控制台(测试): http://localhost:8080/h2-console

## 七、常见问题

### Q1: 启动失败提示数据库连接错误
确保MySQL服务已启动，并检查`application.yml`中的数据库配置。

### Q2: Redis连接失败
确保Redis服务已启动，或在配置中禁用Redis缓存。

### Q3: 前端无法访问后端API
检查后端CORS配置，确保允许前端域名访问。

### Q4: 测试运行失败
确保测试环境配置正确，H2数据库依赖已添加。
