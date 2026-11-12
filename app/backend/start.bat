@echo off
chcp 936 >nul
setlocal enabledelayedexpansion
title English Study System - Backend

cd /d "%~dp0"
set "JAR_FILE=english-learning-system-1.0.0.jar"
set "BE_PORT=8080"

echo ============================================================
echo   英语等级智能学习与考试系统 - 后端服务
echo ============================================================
echo.

echo [1/5] 检查 Java 环境...
where java >nul 2>&1
if errorlevel 1 goto err_no_java
echo       Java 环境正常

echo [2/5] 检查 MySQL 服务...
sc query MySQL >nul 2>&1
if not errorlevel 1 goto mysql_ok
sc query MySQL80 >nul 2>&1
if not errorlevel 1 goto mysql_ok
echo       [警告] 未检测到 MySQL 服务，请确认数据库已启动
goto check_redis
:mysql_ok
echo       已检测到 MySQL 服务
:check_redis
echo [3/5] 检查 Redis 服务...
sc query Redis >nul 2>&1
if not errorlevel 1 goto redis_ok
echo       [警告] 未检测到 Redis 服务，请确认缓存服务已启动
goto check_jar
:redis_ok
echo       已检测到 Redis 服务
:check_jar
echo [4/5] 检查应用程序...
if exist "%JAR_FILE%" goto jar_ok
echo       JAR 不存在，开始编译（首次较慢，请耐心等待）...
call mvn clean package -DskipTests
if errorlevel 1 goto err_build
:jar_ok
echo       应用程序就绪

echo [5/5] 启动应用程序...
echo.
echo ============================================================
echo   访问地址：http://localhost:%BE_PORT%
echo   API 文档：http://localhost:%BE_PORT%/swagger-ui.html
echo   按 Ctrl+C 停止服务
echo ============================================================
echo.
java -jar -Dfile.encoding=UTF-8 -Xms512m -Xmx1024m "%JAR_FILE%"
goto end

:err_no_java
echo [错误] 未检测到 Java，请先安装 JDK 17 及以上版本
goto end
:err_build
echo [错误] 编译失败，请检查代码或 Maven 环境
goto end

:end
echo.
echo 后端服务已退出。
pause
