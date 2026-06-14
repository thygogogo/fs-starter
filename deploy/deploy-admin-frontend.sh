#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR/.."
source "$SCRIPT_DIR/config.sh"

echo ">>> 部署环境: $DEPLOY_ENV ($SERVER)"

REMOTE_DIR="/root/docker/manager-frontend"
DIST_PATH="frontend-soldier-admin/dist"

echo ">>> 打包前端 admin（mode=$([ "$DEPLOY_ENV" = "prod" ] && echo production || echo test)）..."
cd frontend-soldier-admin
npm install
if [ "$DEPLOY_ENV" = "prod" ]; then
  npm run build:prod
else
  npm run build:test
fi
cd ..

echo ">>> 上传 dist 到 $SERVER:$REMOTE_DIR/"
rsync -avz --progress --delete "$DIST_PATH/" "$SERVER:$REMOTE_DIR/dist/"

echo ">>> 执行 restart.sh..."
ssh "$SERVER" "cd $REMOTE_DIR && bash restart.sh"

echo ">>> 部署完成"
