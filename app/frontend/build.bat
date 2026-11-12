@echo off
chcp 65001 >nul
title 英语等级智能学习与考试系统 - 前端构建

echo ========================================
echo   英语等级智能学习与考试系统 - 前端构建
echo ========================================
echo.

cd /d %~dp0

:: 检查Node.js
echo [1/4] 检查Node.js环境...
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Node.js，请安装Node.js 18+
    pause
    exit /b 1
)
echo [√] Node.js 已安装

:: 安装依赖
echo [2/4] 安装项目依赖...
call npm install
if %errorlevel% neq 0 (
    echo [错误] 依赖安装失败
    pause
    exit /b 1
)
echo [√] 依赖安装完成

:: 构建项目
echo [3/4] 构建生产版本...
call npm run build
if %errorlevel% neq 0 (
    echo [错误] 构建失败
    pause
    exit /b 1
)
echo [√] 构建完成

:: 输出信息
echo [4/4] 构建结果...
echo.
echo ========================================
echo   构建完成！
echo   输出目录: %~dp0dist
echo.
echo   部署方式:
echo   1. 将dist目录部署到Nginx/Apache
echo   2. 或运行 npm run preview 预览
echo ========================================

pause
