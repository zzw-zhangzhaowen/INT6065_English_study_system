# INT6065_English_study_system

英语等级智能学习与考试系统 · INT6065 项目

| 项目 | 地址 |
| --- | --- |
| 本地根目录 | `D:\Wen\EduHK\6065 MON\FINAL\INT6065_English_study_system` |
| 前端源码 | `app\frontend`（Vue 3 + Vite + Element Plus + Pinia + ECharts） |
| 后端源码 | `app\backend`（Spring Boot） |
| 演示/构建脚本 | `app\tools` |
| 线上地址 | https://zzw-zhangzhaowen.github.io/INT6065_English_study_system/ |
| GitHub 仓库 | https://github.com/zzw-zhangzhaowen/INT6065_English_study_system |

## 快速启动

**一键启动（Windows）**：双击根目录 `start-all.bat`（脚本使用 `%~dp0` 定位，可任意移动文件夹）。

| 命令 | 作用 |
| --- | --- |
| `start-all.bat` / `demo` | 独立运行模式启动前端（不请求后端），自动打开浏览器 |
| `start-all.bat build` | 构建静态站点到 `app\frontend\dist` |
| `start-all.bat status` | 查看 3002 / 8080 端口占用 |
| `start-all.bat stop` | 停止所有服务 |
| `start-all.bat full` | 全量模式（需要 MySQL + Redis） |

**Node.js 检测是三层的**，任选其一即可：

1. 系统 PATH 里的 `node`（要求 **18+**，低于 18 会自动跳过）
2. 自动探测 `%LOCALAPPDATA%\Programs\nodejs`、`%ProgramFiles%\nodejs`、`nvm` 目录
3. 兜底探测 WorkBuddy 内置运行时 `%USERPROFILE%\.workbuddy\binaries\node\versions\*`

**三者都找不到时**（机器上确实没有 Node.js），脚本会自动改用内置的
`app\tools\serve-dist.ps1`（PowerShell + HttpListener，零依赖）把已构建的 `dist/` 跑起来，
同样在 `http://localhost:3002/` 提供服务 —— 不需要装任何东西即可运行。

> 提示：直接双击 `dist\index.html` 是**打不开**的。Chrome 会以 CORS 策略拦截 `file://` 下的
> ES Module 与 CSS，页面只会白屏，必须通过 HTTP 访问。

**仅前端（独立运行模式，无需后端）**

```bash
cd app/frontend
npm install          # 首次
npm run dev:mock     # 开发
npm run build:pages  # 构建（GitHub Pages 模式）
npm run preview      # 预览构建产物
```

**安装 Node.js（推荐，一次搞定）**

```powershell
winget install OpenJS.NodeJS.LTS
```

## 独立运行模式说明

- `npm run build:pages`（`--mode pages`）下，axios adapter 拦截所有 `/api` 请求，返回本地 mock 数据，运行时真实请求数为 **0**，可直接静态部署。
- 登录页任意账号 + 密码 `123456` 即可进入；学生端 / 教师端 / 系统管理分别对应不同角色。

## AI 能力（学生端）

- **作文批改**：35 类语法错误识别 + 10 组表达升级建议，四维权重 25/25/30/20，低于 100 词禁止提交，空输入框内中英双语显示规则。
- **口语陪练**：`MediaRecorder` 真实录音 + 浏览器 `SpeechRecognition` 实时转写 + 5 秒 watchdog；权限被拒 / 转写失败 / 网络受限时自动切换为语音转写模式，录音仍可回听。
- **长难句解析**：成分切分与主干识别，主谓宾定位与从句标注。
- Provider 抽象层统一 AI 出入口，可按需切换后端服务或大语言模型。

## 部署

推送 `main` 分支即触发 `.github/workflows/deploy.yml` 构建并发布到 GitHub Pages。
