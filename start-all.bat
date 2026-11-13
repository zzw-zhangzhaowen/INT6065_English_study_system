@echo off
chcp 936 >nul
setlocal enabledelayedexpansion
title English Study System

cd /d "%~dp0"
set "APP_DIR=%~dp0app"
set "FE_DIR=%APP_DIR%\frontend"
set "BE_DIR=%APP_DIR%\backend"
set "TOOLS_DIR=%APP_DIR%\tools"
set "FE_PORT=3002"
set "BE_PORT=8080"
set "SITE=http://localhost:%FE_PORT%"

if /i "%~1"=="stop" goto do_stop
if /i "%~1"=="status" goto do_status
if /i "%~1"=="full" goto do_full
if /i "%~1"=="build" goto do_build
if /i "%~1"=="help" goto do_help
if /i "%~1"=="demo" goto do_demo
if /i "%~1"=="front" goto do_demo
goto do_demo

:do_help
echo ============================================================
echo   英语等级智能学习与考试系统 - 启动器
echo ============================================================
echo   start-all.bat           一键启动（纯演示模式，推荐）
echo   start-all.bat demo      同上，纯演示模式
echo   start-all.bat full      前端 + 后端一起启动（需 MySQL / Redis）
echo   start-all.bat build     打包静态站点（输出到 app\frontend\dist）
echo   start-all.bat stop      停止所有服务
echo   start-all.bat status    查看端口占用
echo ============================================================
echo   演示模式不依赖后端与数据库，所有数据来自内置模拟数据。
echo ============================================================
goto end

:do_demo
echo ============================================================
echo   英语等级智能学习与考试系统
echo ============================================================
echo.
echo 正在以「纯演示模式」启动前端（不请求后端，数据走内置模拟数据）
if not exist "%FE_DIR%\package.json" goto err_no_fe
call :check_node
if errorlevel 1 goto demo_offline
call :check_port %FE_PORT%
if not defined BUSY_PID goto demo_spawn
echo [提示] 端口 %FE_PORT% 已在运行，直接打开浏览器
start "" "%SITE%"
goto end
:demo_spawn
start "前端服务-演示模式" /d "%FE_DIR%" cmd /k "npm run dev:mock"
echo 正在等待服务就绪...
call :wait_port %FE_PORT% 40
if not defined READY goto demo_timeout
echo.
echo   已就绪，正在打开浏览器...
start "" "%SITE%"
echo   前端地址：%SITE%
echo   停止服务：start-all.bat stop
echo ============================================================
goto end
:demo_timeout
echo.
echo   [警告] 等待超时，请查看「前端服务-演示模式」窗口里的报错信息
echo   前端地址：%SITE%
goto end

:demo_offline
call :check_port %FE_PORT%
if defined BUSY_PID goto demo_open_busy
if not exist "%FE_DIR%\dist\index.html" goto err_no_node
if not exist "%TOOLS_DIR%\serve-dist.ps1" goto err_no_node
echo [提示] 未检测到可用的 Node.js，改用内置 PowerShell 静态服务器运行已构建页面
start "静态服务器-演示模式" /min "%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe" -NoProfile -ExecutionPolicy Bypass -File "%TOOLS_DIR%\serve-dist.ps1" -Port %FE_PORT% -Root "%FE_DIR%\dist"
echo 正在等待静态服务器就绪...
call :wait_port %FE_PORT% 30
if not defined READY goto demo_offline_fail
echo.
echo   已就绪，正在打开浏览器...
start "" "%SITE%"
echo   前端地址：%SITE%
echo   停止服务：start-all.bat stop
echo ============================================================
goto end
:demo_open_busy
echo [提示] 端口 %FE_PORT% 已被占用，直接打开该地址
start "" "http://localhost:%FE_PORT%"
goto end
:demo_offline_fail
echo [错误] 静态服务器启动失败，可直接访问线上地址
start "" "https://zzw-zhangzhaowen.github.io/INT6065_English_study_system/"
goto end
:do_full
echo ============================================================
echo   英语等级智能学习与考试系统 - 全量模式
echo ============================================================
echo.
echo 说明：此模式使用真实后端接口，需要 MySQL 与 Redis 已启动。
echo       若只想看功能演示，建议直接运行 start-all.bat demo
echo.
if not exist "%FE_DIR%\package.json" goto err_no_fe
call :check_node
if errorlevel 1 goto err_no_node
set "BE_STARTED="
call :check_port %FE_PORT%
if defined BUSY_PID goto full_be
echo [1/4] 启动前端服务（端口 %FE_PORT%）...
start "前端服务" /d "%FE_DIR%" cmd /k "npm run dev"
:full_be
if exist "%BE_DIR%\start.bat" goto full_be_spawn
echo [2/4] 未找到后端启动脚本，跳过
goto full_wait_fe
:full_be_spawn
call :check_port %BE_PORT%
if defined BUSY_PID goto full_wait_fe
echo [2/4] 启动后端服务（端口 %BE_PORT%）...
start "后端服务" /d "%BE_DIR%" cmd /k "call start.bat"
set "BE_STARTED=1"
:full_wait_fe
echo [3/4] 等待前端就绪...
call :wait_port %FE_PORT% 40
if not defined READY goto full_timeout
echo       前端已就绪
if not defined BE_STARTED goto full_open
echo [4/4] 等待后端就绪（Spring Boot 首次启动约需 20-40 秒）...
call :wait_port %BE_PORT% 60
if not defined READY goto full_be_fail
echo       后端已就绪
goto full_open
:full_be_fail
echo.
echo   [警告] 后端未能启动，最常见的原因是 MySQL 或 Redis 没有运行。
echo          此时页面可能显示不全或停留在加载状态，建议改用纯演示模式：
echo              start-all.bat demo
echo          后端具体报错请查看「后端服务」窗口。
echo.
:full_open
echo       正在打开浏览器...
start "" "%SITE%"
goto full_banner
:full_timeout
echo       超时未就绪，请查看「前端服务」窗口的报错
:full_banner
echo.
echo   前端地址：%SITE%
echo   后端地址：http://localhost:%BE_PORT%
echo   停止服务：start-all.bat stop
echo ============================================================
goto end

:do_build
echo 正在打包静态站点（用于 GitHub Pages / 本地直接打开）...
if not exist "%FE_DIR%\package.json" goto err_no_fe
call :check_node
if errorlevel 1 goto err_no_node
pushd "%FE_DIR%"
if not exist "node_modules" call npm install
call npm run build:pages
set "BUILD_RC=!errorlevel!"
popd
if not "!BUILD_RC!"=="0" goto err_build
echo.
echo   [OK] 打包完成：%FE_DIR%\dist
echo   可把 dist 目录整个上传，或直接双击其中的 index.html 预览
goto end
:err_build
echo   [错误] 打包失败，请查看上方 npm 输出
goto end

:do_stop
echo 正在停止服务...
set "KILLED="
call :check_port %FE_PORT%
if not defined BUSY_PID goto stop_be
taskkill /f /pid !BUSY_PID! >nul 2>&1
echo [OK] 已停止前端进程 PID !BUSY_PID!
set "KILLED=1"
:stop_be
call :check_port %BE_PORT%
if not defined BUSY_PID goto stop_done
taskkill /f /pid !BUSY_PID! >nul 2>&1
echo [OK] 已停止后端进程 PID !BUSY_PID!
set "KILLED=1"
:stop_done
if not defined KILLED echo [提示] 没有检测到运行中的服务
echo 完成。
goto end

:do_status
echo 端口占用情况：
call :check_port %FE_PORT%
if defined BUSY_PID echo   前端 %FE_PORT%: 运行中 PID !BUSY_PID!
if not defined BUSY_PID echo   前端 %FE_PORT%: 未启动
call :check_port %BE_PORT%
if defined BUSY_PID echo   后端 %BE_PORT%: 运行中 PID !BUSY_PID!
if not defined BUSY_PID echo   后端 %BE_PORT%: 未启动
goto end

:err_no_fe
echo [错误] 未找到前端目录：%FE_DIR%
goto end

:check_node
call :node_ok_version
if not errorlevel 1 goto node_chk_deps
call :detect_node
if not defined NODE_DIR exit /b 1
set "PATH=!NODE_DIR!;!PATH!"
call :node_ok_version
if errorlevel 1 exit /b 1
echo [提示] 已自动定位内置 Node.js: !NODE_DIR!
:node_chk_deps
if exist "%FE_DIR%\node_modules" goto node_ok
echo [提示] 首次运行，正在安装前端依赖（需要几分钟）...
pushd "%FE_DIR%"
call npm install
popd
if errorlevel 1 goto err_install
:node_ok
exit /b 0
:err_install
echo [错误] 前端依赖安装失败
exit /b 1
:err_no_node
echo [错误] 未检测到 Node.js 18 及以上版本，请先安装 Node.js
echo        下载地址: https://nodejs.org/zh-cn/download
echo        或在终端执行: winget install OpenJS.NodeJS.LTS
goto end
:detect_node
set "NODE_DIR="
call :try_dir "%LOCALAPPDATA%\Programs\nodejs"
call :try_dir "%ProgramFiles%\nodejs"
call :try_dir "%USERPROFILE%\.workbuddy\binaries\node\versions"
call :try_dir "%APPDATA%\nvm"
call :try_dir "%LOCALAPPDATA%\nvm"
exit /b 0

:try_dir
if not exist "%~1" exit /b 0
set "OLD_PATH=!PATH!"
for /d %%v in ("%~1" "%~1\*") do (
  if exist "%%v\node.exe" (
    set "PATH=%%v;!PATH!"
    call :node_ok_version
    if not errorlevel 1 (
      set "NODE_DIR=%%v"
      exit /b 0
    )
    set "PATH=!OLD_PATH!"
  )
)
set "PATH=!OLD_PATH!"
exit /b 0

:node_ok_version
where node >nul 2>&1
if errorlevel 1 exit /b 1
set "NODE_VER="
for /f "delims=" %%v in ('node -v 2^>nul') do set "NODE_VER=%%v"
if not defined NODE_VER exit /b 1
set "NODE_VER=!NODE_VER:v=!"
set "NODE_MAJOR="
for /f "tokens=1 delims=." %%m in ("!NODE_VER!") do set "NODE_MAJOR=%%m"
if not defined NODE_MAJOR exit /b 1
echo !NODE_MAJOR!| findstr /r "^[0-9][0-9]*$" >nul 2>&1
if errorlevel 1 exit /b 1
if !NODE_MAJOR! GEQ 18 exit /b 0
exit /b 1

:check_port
set "BUSY_PID="
for /f "tokens=4,5" %%a in ('netstat -ano ^| findstr /c:":%1"') do (
  if /i "%%a"=="LISTENING" set "BUSY_PID=%%b"
)
exit /b 0

:wait_port
set "READY="
set /a _n=0
:wait_loop
call :check_port %1
if defined BUSY_PID set "READY=1"
if defined READY goto :eof
set /a _n+=1
if %_n% geq %2 goto :eof
ping -n 2 127.0.0.1 >nul 2>&1
goto wait_loop

:end
if "%~1"=="" pause
endlocal
