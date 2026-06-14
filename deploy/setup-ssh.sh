#!/bin/bash
set -e

#生产环境 DEPLOY_ENV=prod bash deploy/setup-ssh.sh
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

# 1. 生成密钥对（已存在则跳过）
if [ ! -f ~/.ssh/id_rsa ]; then
    echo ">>> 生成 SSH 密钥对..."
    ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa -N ""
else
    echo ">>> SSH 密钥已存在，跳过生成"
fi

# 2. 推送公钥到服务器
echo ">>> 推送公钥到 $SERVER（需要输入服务器密码）..."
ssh-copy-id -i ~/.ssh/id_rsa.pub "$SERVER"

echo ">>> 配置完成，后续 deploy 脚本可免密执行"
