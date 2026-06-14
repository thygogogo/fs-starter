#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR/.."
source "$SCRIPT_DIR/config.sh"

echo ">>> 部署环境: $DEPLOY_ENV ($SERVER, spring.profiles.active=$SPRING_PROFILE)"

REMOTE_DIR="/root/docker/manager-server"
JAR_PATH="soldier-admin/target/manager-server.jar"

echo ">>> 打包 soldier-admin..."
mvn clean package -DskipTests -pl soldier-admin -am -q

echo ">>> 推送 $JAR_PATH 到 $SERVER:$REMOTE_DIR/"
rsync -avz --progress "$JAR_PATH" "$SERVER:$REMOTE_DIR/manager-server.jar"

echo ">>> 执行 restart.sh..."
ssh "$SERVER" "cd $REMOTE_DIR && bash restart.sh"

echo ">>> 部署完成"
