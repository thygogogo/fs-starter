#!/bin/bash
# 部署配置模板 — 使用前请修改 SERVER 与 SPRING_PROFILE
DEPLOY_ENV="${DEPLOY_ENV:-test}"

case "$DEPLOY_ENV" in
  test)
    SERVER="root@YOUR_TEST_SERVER"
    SPRING_PROFILE="test"
    ;;
  prod)
    SERVER="root@YOUR_PROD_SERVER"
    SPRING_PROFILE="prod"
    ;;
  *)
    echo "未知环境: $DEPLOY_ENV（可选: test, prod）"
    exit 1
    ;;
esac
