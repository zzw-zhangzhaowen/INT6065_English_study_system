# 部署指南

## 一、环境要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 推荐 Eclipse Temurin |
| MySQL | 8.0+ | 业务数据库 |
| Redis | 7.0+ | 缓存服务 |
| Node.js | 18+ | 前端构建 |
| Maven | 3.6+ | 后端构建 |

## 二、部署方式

本项目有两种部署形态，按需选择：

| 方式 | 适用场景 | 是否需要数据库 |
|------|----------|----------------|
| 纯演示模式 | 答辩演示、静态托管、离线预览 | 否 |
| 全量前后端 | 完整功能验证 | 是 |

### 2.1 纯演示模式（推荐用于演示）

前端内置了完整的模拟数据，不请求任何后端接口。

```bash
cd app/frontend
npm install
npm run build:pages
```

产物在 `app/frontend/dist`，可直接托管到任意静态服务器，也可双击其中的 `index.html` 本地打开。

项目已配置 GitHub Actions（`.github/workflows/deploy.yml`），推送到 `main` 分支后自动构建并发布到 GitHub Pages。

### 2.2 全量前后端

#### 数据库初始化

```bash
mysql -u root -p
source app/backend/src/main/resources/sql/schema.sql
source app/backend/src/main/resources/sql/init-data.sql
```

编辑 `app/backend/src/main/resources/application.yml` 填写 MySQL 与 Redis 连接信息。

#### 启动后端

在项目根目录执行：

```bat
start-all.bat full
```

或手动启动：

```bash
cd app/backend
mvn clean package -DskipTests
java -jar english-learning-system-1.0.0.jar
```

也可以直接双击 `app/backend/start.bat`，脚本会自动检查 Java、MySQL、Redis 环境并启动 jar。

#### 启动前端

```bash
cd app/frontend
npm run dev
```

## 三、启动脚本说明

项目根目录的 `start-all.bat` 是唯一入口：

| 命令 | 功能 |
|------|------|
| `start-all.bat` | 纯演示模式启动（默认） |
| `start-all.bat full` | 前端 + 后端一起启动 |
| `start-all.bat build` | 打包静态站点到 `app/frontend/dist` |
| `start-all.bat stop` | 停止占用 3002 / 8080 的进程 |
| `start-all.bat status` | 查看端口占用情况 |

## 四、测试

```bash
cd app/backend
mvn test                    # 运行全部测试
mvn test -Dtest=*ServiceTest # 只跑 Service 层
mvn surefire-report:report  # 生成测试报告
```

## 五、默认账号

演示模式下密码不做校验（不少于 6 位即可），用户名决定角色：

| 角色 | 用户名 |
|------|--------|
| 管理员 | admin |
| 教师 | teacher |
| 学生 | student 或任意普通名字 |

连接真实后端时使用数据库初始化的账号。

## 六、访问地址

- 前端页面：http://localhost:3002
- 后端 API：http://localhost:8080
- API 文档：http://localhost:8080/swagger-ui.html

## 七、常见问题

### Q1: 启动失败提示数据库连接错误
确保 MySQL 服务已启动，并检查 `application.yml` 中的数据库配置。

### Q2: Redis 连接失败
确保 Redis 服务已启动，或在配置中禁用 Redis 缓存。

### Q3: 前端无法访问后端 API
检查后端 CORS 配置，以及 `vite.config.js` 中的 `/api` 代理目标。

### Q4: `full` 模式页面数据为空
后端未成功启动，最常见原因是 MySQL 或 Redis 未运行。查看「后端服务」窗口报错，或改用 `start-all.bat demo`。
