@echo off
chcp 65001 >nul
title 英语等级智能学习与考试系统 - 启动

echo ========================================
echo   英语等级智能学习与考试系统 - 启动脚本
echo ========================================
echo.

:: 检查Java环境
echo [1/5] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Java环境，请安装JDK 17+
    pause
    exit /b 1
)
echo [√] Java环境正常

:: 检查MySQL服务
echo [2/5] 检查MySQL服务...
sc query MySQL >nul 2>&1
if %errorlevel% neq 0 (
    sc query MySQL80 >nul 2>&1
    if %errorlevel% neq 0 (
        echo [警告] MySQL服务未检测到，请确保MySQL已启动
    ) else (
        echo [√] MySQL80服务已安装
    )
) else (
    echo [√] MySQL服务已安装
)

:: 检查Redis服务
echo [3/5] 检查Redis服务...
sc query Redis >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] Redis服务未检测到，请确保Redis已启动
) else (
    echo [√] Redis服务已安装
)

:: 检查JAR文件
echo [4/5] 检查应用程序...
set JAR_FILE=english-learning-system-1.0.0.jar
if not exist %JAR_FILE% (
    echo [提示] JAR文件不存在，开始编译...
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo [错误] 编译失败，请检查代码
        pause
        exit /b 1
    )
)
echo [√] 应用程序就绪

:: 启动应用
echo [5/5] 启动应用程序...
echo.
echo ========================================
echo   系统启动中，请稍候...
echo   访问地址: http://localhost:8080
echo   API文档: http://localhost:8080/swagger-ui.html
echo   按 Ctrl+C 停止服务
echo ========================================
echo.

java -jar -Dfile.encoding=UTF-8 -Xms512m -Xmx1024m %JAR_FILE%

pause
