@echo off
REM ================================================================
REM 部署脚本 - back-blog (Windows)
REM 用法: deploy.bat {start^|stop^|restart^|status^|logs^|build^|health}
REM ================================================================

setlocal enabledelayedexpansion

REM 配置
set APP_NAME=back-blog
set JAR_FILE=target\%APP_NAME%-0.0.1-SNAPSHOT.jar
set PID_FILE=%APP_NAME%.pid
set LOG_DIR=logs
set SERVER_PORT=8080

REM 环境变量（根据实际情况修改）
set DB_USERNAME=%DB_USERNAME:~0,-1%%pxczxn%
set DB_PASSWORD=%DB_PASSWORD:~0,-1%%pxczxn%
set DB_URL=%DB_URL:~0,-1%jdbc:mysql://localhost:3306/pxczxn-blog?useUnicode=true^&characterEncoding=utf8^&useSSL=false^&serverTimezone=Asia/Shanghai^&allowPublicKeyRetrieval=true
set JWT_SECRET=%JWT_SECRET:~0,-1%please-change-this-to-a-secure-secret-key-min-32-chars-in-production
set JWT_EXPIRATION=%JWT_EXPIRATION:~0,-1%86400000
set SPRING_PROFILES_ACTIVE=%SPRING_PROFILES_ACTIVE:~0,-1%prod

REM JVM 参数
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200

if "%1"=="" goto help
if /i "%1"=="start" goto start
if /i "%1"=="stop" goto stop
if /i "%1"=="restart" goto restart
if /i "%1"=="status" goto status
if /i "%1"=="logs" goto logs
if /i "%1"=="build" goto build
if /i "%1"=="health" goto health
if /i "%1"=="help" goto help
if /i "%1"=="--help" goto help
if /i "%1"=="-h" goto help
echo 未知命令: %1
goto help

:start
    echo [INFO] ========================================
    echo [INFO] 启动应用: %APP_NAME%
    echo [INFO] ========================================

    REM 创建日志目录
    if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

    REM 检查 JAR 文件
    if not exist "%JAR_FILE%" (
        echo [ERROR] JAR 文件不存在: %JAR_FILE%
        echo [INFO] 请先运行: deploy.bat build
        goto end
    )

    REM 检查端口占用
    netstat -ano | findstr ":%SERVER_PORT%" | findstr "LISTENING" >nul 2>&1
    if %errorlevel% equ 0 (
        echo [WARN] 端口 %SERVER_PORT% 已被占用
        echo [INFO] 请先运行: deploy.bat stop
        goto end
    )

    REM 启动应用
    echo [INFO] 启动参数: Profile=%SPRING_PROFILES_ACTIVE%, Port=%SERVER_PORT%
    start "back-blog" /B java %JAVA_OPTS% ^
        -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE% ^
        -Dserver.port=%SERVER_PORT% ^
        -Dlogging.file.name=%LOG_DIR%\application.log ^
        -jar "%JAR_FILE%" ^
        >> "%LOG_DIR%\stdout.log" 2>&1

    REM 获取 PID（Windows 需要通过端口查找）
    timeout /t 3 /nobreak >nul
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%SERVER_PORT%" ^| findstr "LISTENING"') do (
        set APP_PID=%%a
        goto pid_found
    )
    :pid_found

    if defined APP_PID (
        echo !APP_PID! > "%PID_FILE%"
        echo [INFO] 应用已启动 (PID: !APP_PID!)
    ) else (
        echo [WARN] 无法获取进程 PID
    )

    echo [INFO] 日志目录: %CD%\%LOG_DIR%
    echo [INFO] 健康检查: http://localhost:%SERVER_PORT%/actuator/health
    echo [INFO] 等待应用启动中...
    timeout /t 5 /nobreak >nul
    goto end

:stop
    echo [INFO] ========================================
    echo [INFO] 停止应用: %APP_NAME%
    echo [INFO] ========================================

    REM 通过 PID 文件停止
    if exist "%PID_FILE%" (
        set /p STOP_PID=<%PID_FILE%
        tasklist /FI "PID eq !STOP_PID!" 2>nul | findstr !STOP_PID! >nul
        if !errorlevel! equ 0 (
            echo [INFO] 停止进程: !STOP_PID!
            taskkill /F /PID !STOP_PID! >nul 2>&1
        )
        del "%PID_FILE%" 2>nul
    )

    REM 通过端口查找并停止
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%SERVER_PORT%" ^| findstr "LISTENING"') do (
        echo [INFO] 停止占用端口的进程: %%a
        taskkill /F /PID %%a >nul 2>&1
    )

    REM 通过 Java 进程名称停止
    for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" ^| findstr java.exe') do (
        taskkill /F /PID %%a >nul 2>&1
    )

    if exist "%PID_FILE%" del "%PID_FILE%" 2>nul
    echo [INFO] 应用已停止
    goto end

:restart
    call :stop
    timeout /t 2 /nobreak >nul
    call :start
    goto end

:status
    echo [INFO] ========================================
    echo [INFO] 应用状态: %APP_NAME%
    echo [INFO] ========================================

    set STATUS=DOWN
    set APP_PID=

    REM 检查 PID 文件
    if exist "%PID_FILE%" (
        set /p CHECK_PID=<%PID_FILE%
        tasklist /FI "PID eq !CHECK_PID!" 2>nul | findstr !CHECK_PID! >nul
        if !errorlevel! equ 0 (
            set STATUS=UP
            set APP_PID=!CHECK_PID!
        ) else (
            del "%PID_FILE%" 2>nul
        )
    )

    REM 检查端口
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%SERVER_PORT%" ^| findstr "LISTENING"') do (
        if not defined APP_PID set APP_PID=%%a
        set STATUS=UP
    )

    echo   状态: %STATUS%
    if defined APP_PID echo   PID: %APP_PID%
    echo   端口: %SERVER_PORT%
    echo   Profile: %SPRING_PROFILES_ACTIVE%

    REM 健康检查
    echo.
    echo [INFO] 健康检查端点:
    echo   - 健康检查: http://localhost:%SERVER_PORT%/actuator/health
    echo   - 就绪探针: http://localhost:%SERVER_PORT%/actuator/health/readiness
    echo   - 存活探针: http://localhost:%SERVER_PORT%/actuator/health/liveness

    echo [INFO] ========================================
    goto end

:logs
    if not exist "%LOG_DIR%\application.log" (
        if not exist "%LOG_DIR%\stdout.log" (
            echo [ERROR] 日志文件不存在
            goto end
        )
        powershell -Command "Get-Content '%LOG_DIR%\stdout.log' -Wait -Tail 50"
    ) else (
        powershell -Command "Get-Content '%LOG_DIR%\application.log' -Wait -Tail 50"
    )
    goto end

:build
    echo [INFO] ========================================
    echo [INFO] 构建项目
    echo [INFO] ========================================
    call mvn clean package -DskipTests
    if %errorlevel% equ 0 (
        echo [INFO] 构建成功: %JAR_FILE%
    ) else (
        echo [ERROR] 构建失败
        exit /b 1
    )
    goto end

:health
    echo [INFO] ========================================
    echo [INFO] 健康检查
    echo [INFO] ========================================

    REM 检查进程
    set STATUS=DOWN
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%SERVER_PORT%" ^| findstr "LISTENING"') do (
        set STATUS=UP
        echo [INFO] 进程状态: 运行中 (PID: %%a)
    )
    if "%STATUS%"=="DOWN" echo [ERROR] 进程状态: 未运行

    echo.
    echo [INFO] HTTP 端点检查:
    echo   注意: 需要安装 curl 或使用浏览器访问

    goto end

:help
    echo.
    echo 用法: deploy.bat {start^|stop^|restart^|status^|logs^|build^|health^|help}
    echo.
    echo 命令说明:
    echo   start   - 启动应用
    echo   stop    - 停止应用
    echo   restart - 重启应用
    echo   status  - 查看运行状态
    echo   logs    - 查看实时日志
    echo   build   - 构建项目 (mvn clean package)
    echo   health  - 执行健康检查
    echo   help    - 显示帮助信息
    echo.
    echo 环境变量:
    echo   SERVER_PORT         - 服务端口 (默认: 8080)
    echo   SPRING_PROFILES_ACTIVE - Spring Profile (默认: prod)
    echo   DB_URL              - 数据库连接 URL
    echo   DB_USERNAME         - 数据库用户名
    echo   DB_PASSWORD         - 数据库密码
    echo   JWT_SECRET          - JWT 密钥
    echo.

:end
    endlocal
