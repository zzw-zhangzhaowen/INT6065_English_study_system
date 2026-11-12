@echo off
chcp 65001 >nul
title 英语等级智能学习与考试系统 - 前端启动

echo ========================================
echo   英语等级智能学习与考试系统 - 前端启动
echo ========================================
echo.

cd /d %~dp0

:: 检查Node.js
echo [1/3] 检查Node.js环境...
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Node.js，请安装Node.js 18+
    pause
    exit /b 1
)
echo [√] Node.js 已安装

:: 检查依赖
echo [2/3] 检查项目依赖...
if not exist node_modules (
    echo [提示] 正在安装依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo [错误] 依赖安装失败
        pause
        exit /b 1
    )
)
echo [√] 依赖已就绪

:: 启动开发服务器
echo [3/3] 启动开发服务器...
echo.
echo ========================================
echo   前端服务启动中...
echo   访问地址: http://localhost:5173
echo   按 Ctrl+C 停止服务
echo ========================================
echo.

call npm run dev

pause
