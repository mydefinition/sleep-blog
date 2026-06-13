#!/bin/bash
set -e

APP_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR_NAME="sleep-blog-1.0.0.jar"
JAVA_OPTS="-Xmx256m -Xms128m"

cd "$APP_DIR"

case "$1" in
  start)
    if pgrep -f "$JAR_NAME" > /dev/null; then
      echo "服务已在运行中"
      exit 1
    fi
    echo "启动服务..."
    nohup java $JAVA_OPTS -jar "$JAR_NAME" --spring.profiles.active=prod > app.log 2>&1 &
    sleep 2
    if pgrep -f "$JAR_NAME" > /dev/null; then
      echo "启动成功，PID: $(pgrep -f $JAR_NAME)"
    else
      echo "启动失败，查看日志: tail -f $APP_DIR/app.log"
    fi
    ;;

  stop)
    if ! pgrep -f "$JAR_NAME" > /dev/null; then
      echo "服务未在运行"
      exit 0
    fi
    echo "停止服务..."
    pkill -f "$JAR_NAME"
    sleep 2
    echo "已停止"
    ;;

  restart)
    $0 stop
    sleep 1
    $0 start
    ;;

  status)
    if pgrep -f "$JAR_NAME" > /dev/null; then
      echo "运行中，PID: $(pgrep -f $JAR_NAME)"
    else
      echo "未运行"
    fi
    ;;

  *)
    echo "用法: ./run.sh {start|stop|restart|status}"
    exit 1
    ;;
esac