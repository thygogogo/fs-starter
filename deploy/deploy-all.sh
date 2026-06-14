#!/bin/bash
# 一键部署 admin 后端、app 后端、admin 前端
#
# 用法:
#   bash deploy/deploy-all.sh              # 测试环境（默认）
#   bash deploy/deploy-all.sh prod         # 生产环境
#   bash deploy/deploy-all.sh --prod       # 生产环境
#   DEPLOY_ENV=prod bash deploy/deploy-all.sh
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

usage() {
  cat <<'EOF'
一键部署 admin 后端、app 后端、admin 前端

用法:
  bash deploy/deploy-all.sh [环境]

环境参数（可选，默认 test）:
  test, --test           测试环境（见 deploy/config.sh）
  prod, production, --prod   生产环境（见 deploy/config.sh）

示例:
  bash deploy/deploy-all.sh
  bash deploy/deploy-all.sh prod
  DEPLOY_ENV=prod bash deploy/deploy-all.sh
EOF
}

DEPLOY_ENV="${DEPLOY_ENV:-test}"

while [[ $# -gt 0 ]]; do
  case "$1" in
    prod|production|--prod)
      DEPLOY_ENV=prod
      shift
      ;;
    test|--test)
      DEPLOY_ENV=test
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "未知参数: $1"
      usage
      exit 1
      ;;
  esac
done

export DEPLOY_ENV
source "$SCRIPT_DIR/config.sh"

echo "========================================"
echo "  一键部署 ($DEPLOY_ENV → $SERVER)"
echo "  1. fs-starter-admin 后端"
echo "  2. frontend-admin 前端"
echo "  3. fs-starter-app 后端"
echo "========================================"

bash "$SCRIPT_DIR/deploy-admin.sh"
bash "$SCRIPT_DIR/deploy-admin-frontend.sh"
bash "$SCRIPT_DIR/deploy-app.sh"

echo ">>> 全部部署完成 ($DEPLOY_ENV → $SERVER)"
