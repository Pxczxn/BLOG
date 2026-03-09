#!/bin/bash

#================================================================
# 部署脚本 - back-blog
# 用法: ./deploy.sh {start|stop|restart|status|logs|build|health}
#================================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_debug() { echo -e "${BLUE}[DEBUG]${NC} $1"; }

# 配置
APP_NAME="back-blog"
JAR_FILE="target/${APP_NAME}-0.0.1-SNAPSHOT.jar"
PID_FILE="${APP_NAME}.pid"
LOG_DIR="logs"
SERVER_PORT="${SERVER_PORT:-8080}"

# 生产环境配置（可通过环境变量覆盖）
export DB_USERNAME="${DB_USERNAME:-pxczxn}"
export DB_PASSWORD="${DB_PASSWORD:-pxczxn}"
export DB_URL="${DB_URL:-jdbc:mysql://localhost:3306/pxczxn-blog?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true}"
export JWT_SECRET="${JWT_SECRET:-please-change-this-to-a-secure-secret-key-min-32-chars-in-production}"
export JWT_EXPIRATION="${JWT_EXPIRATION:-86400000}"
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"

# JVM 参数配置
JAVA_OPTS="${JAVA_OPTS:--Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$LOG_DIR/}"

# 停止现有进程
stop() {
    log_info "正在停止应用..."

    # 通过 PID 文件停止
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null 2>&1; then
            log_info "停止进程 (PID: $PID)..."
            kill $PID 2>/dev/null || true
            sleep 3

            # 强制停止
            if ps -p $PID > /dev/null 2>&1; then
                log_warn "强制停止进程..."
                kill -9 $PID 2>/dev/null || true
            fi
        fi
        rm -f "$PID_FILE"
    fi

    # 通过端口查找并停止
    PID=$(lsof -ti:$SERVER_PORT 2>/dev/null || true)
    if [ -n "$PID" ]; then
        log_warn "发现占用端口 $SERVER_PORT 的进程 (PID: $PID)，正在停止..."
        kill -9 $PID 2>/dev/null || true
    fi

    log_info "应用已停止"
}

# 启动应用
start() {
    log_info "正在启动应用..."

    # 创建日志目录
    mkdir -p "$LOG_DIR"

    # 检查 JAR 文件
    if [ ! -f "$JAR_FILE" ]; then
        log_error "JAR 文件不存在: $JAR_FILE"
        log_info "请先运行: mvn clean package -DskipTests"
        exit 1
    fi

    # 后台启动
    log_info "启动参数: Profile=$SPRING_PROFILES_ACTIVE, Port=$SERVER_PORT"
    nohup java $JAVA_OPTS \
        -Dspring.profiles.active="$SPRING_PROFILES_ACTIVE" \
        -Dserver.port=$SERVER_PORT \
        -Dlogging.file.name="$LOG_DIR/application.log" \
        -jar "$JAR_FILE" \
        > "$LOG_DIR/stdout.log" 2>&1 &

    # 保存 PID
    echo $! > "$PID_FILE"

    # 等待应用启动
    log_info "等待应用启动..."
    sleep 5

    # 检查进程状态
    if ps -p $(cat "$PID_FILE") > /dev/null 2>&1; then
        log_info "应用已启动 (PID: $(cat $PID_FILE))"
        log_info "日志文件: $LOG_DIR/application.log"
        log_info "健康检查: http://localhost:$SERVER_PORT/actuator/health"
    else
        log_error "应用启动失败，请检查日志"
        tail -20 "$LOG_DIR/stdout.log"
        exit 1
    fi
}

# 重启应用
restart() {
    stop
    sleep 2
    start
}

# 状态检查
status() {
    echo "=========================================="
    echo "  应用状态: $APP_NAME"
    echo "=========================================="

    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null 2>&1; then
            log_info "状态: 运行中"
            echo "  PID: $PID"
            echo "  端口: $SERVER_PORT"
            echo "  Profile: $SPRING_PROFILES_ACTIVE"

            # 健康检查
            HEALTH=$(curl -s http://localhost:$SERVER_PORT/actuator/health 2>/dev/null || echo '{"status":"unknown"}')
            HEALTH_STATUS=$(echo $HEALTH | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
            echo "  健康状态: $HEALTH_STATUS"
            echo ""
            echo "  端点地址:"
            echo "    - 健康检查: http://localhost:$SERVER_PORT/actuator/health"
            echo "    - 就绪探针: http://localhost:$SERVER_PORT/actuator/health/readiness"
            echo "    - 存活探针: http://localhost:$SERVER_PORT/actuator/health/liveness"
            return 0
        else
            log_warn "PID 文件存在但进程不存在"
            rm -f "$PID_FILE"
        fi
    fi

    # 检查端口占用
    PID=$(lsof -ti:$SERVER_PORT 2>/dev/null || true)
    if [ -n "$PID" ]; then
        log_warn "端口 $SERVER_PORT 被占用 (PID: $PID)"
    else
        log_info "状态: 未运行"
    fi
    echo "=========================================="
    return 1
}

# 显示日志
logs() {
    if [ -f "$LOG_DIR/application.log" ]; then
        tail -f "$LOG_DIR/application.log"
    elif [ -f "$LOG_DIR/stdout.log" ]; then
        tail -f "$LOG_DIR/stdout.log"
    else
        log_error "日志文件不存在"
    fi
}

# 构建项目
build() {
    log_info "正在构建项目..."
    mvn clean package -DskipTests
    if [ $? -eq 0 ]; then
        log_info "构建成功: $JAR_FILE"
    else
        log_error "构建失败"
        exit 1
    fi
}

# 健康检查
health() {
    echo "=========================================="
    echo "  健康检查"
    echo "=========================================="

    # 检查进程
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null 2>&1; then
            log_info "进程状态: 运行中 (PID: $PID)"
        else
            log_error "进程状态: 未运行"
        fi
    else
        log_error "进程状态: 未运行"
    fi

    echo ""

    # 检查 HTTP 端点
    log_info "检查 HTTP 端点..."

    # 健康检查
    HEALTH_RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:$SERVER_PORT/actuator/health 2>/dev/null || echo "000")
    HTTP_CODE=$(echo "$HEALTH_RESPONSE" | tail -n1)
    BODY=$(echo "$HEALTH_RESPONSE" | head -n-1)

    if [ "$HTTP_CODE" = "200" ]; then
        log_info "  /actuator/health: OK (200)"
        echo "$BODY" | grep -o '"status":"[^"]*"' | head -1
    else
        log_error "  /actuator/health: FAILED ($HTTP_CODE)"
    fi

    # 就绪探针
    READINESS_RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:$SERVER_PORT/actuator/health/readiness 2>/dev/null || echo "000")
    READINESS_CODE=$(echo "$READINESS_RESPONSE" | tail -n1)

    if [ "$READINESS_CODE" = "200" ]; then
        log_info "  /actuator/health/readiness: OK (200)"
    else
        log_error "  /actuator/health/readiness: FAILED ($READINESS_CODE)"
    fi

    # 存活探针
    LIVENESS_RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:$SERVER_PORT/actuator/health/liveness 2>/dev/null || echo "000")
    LIVENESS_CODE=$(echo "$LIVENESS_RESPONSE" | tail -n1)

    if [ "$LIVENESS_CODE" = "200" ]; then
        log_info "  /actuator/health/liveness: OK (200)"
    else
        log_error "  /actuator/health/liveness: FAILED ($LIVENESS_CODE)"
    fi

    # API 健康检查
    API_RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:$SERVER_PORT/api/health 2>/dev/null || echo "000")
    API_CODE=$(echo "$API_RESPONSE" | tail -n1)

    if [ "$API_CODE" = "200" ]; then
        log_info "  /api/health: OK (200)"
    else
        log_warn "  /api/health: $API_CODE"
    fi

    echo "=========================================="
}

# 显示帮助信息
show_help() {
    echo "用法: $0 {start|stop|restart|status|logs|build|health}"
    echo ""
    echo "命令说明:"
    echo "  start   - 启动应用"
    echo "  stop    - 停止应用"
    echo "  restart - 重启应用"
    echo "  status  - 查看运行状态"
    echo "  logs    - 查看实时日志"
    echo "  build   - 构建项目 (mvn clean package)"
    echo "  health  - 执行健康检查"
    echo ""
    echo "环境变量:"
    echo "  SERVER_PORT         - 服务端口 (默认: 8080)"
    echo "  SPRING_PROFILES_ACTIVE - Spring Profile (默认: prod)"
    echo "  DB_URL              - 数据库连接 URL"
    echo "  DB_USERNAME         - 数据库用户名"
    echo "  DB_PASSWORD         - 数据库密码"
    echo "  JWT_SECRET          - JWT 密钥"
    echo "  JAVA_OPTS           - JVM 参数"
}

# 主函数
main() {
    case "$1" in
        start)
            start
            ;;
        stop)
            stop
            ;;
        restart)
            restart
            ;;
        status)
            status
            ;;
        logs)
            logs
            ;;
        build)
            build
            ;;
        health)
            health
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            show_help
            exit 1
            ;;
    esac
}

main "$@"
