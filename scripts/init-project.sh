#!/bin/bash
# 从 fs-starter 脚手架复制并初始化新业务项目
#
# 用法:
#   ./scripts/init-project.sh --name myapp --group com.mycompany.myapp --title "我的项目"
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SCAFFOLD_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
TARGET_DIR=""

NAME=""
GROUP=""
TITLE="My App"
APPID="wxYOUR_APPID_HERE"

sed_inplace() {
  if sed --version >/dev/null 2>&1; then
    sed -i "$@"
  else
    sed -i '' "$@"
  fi
}

usage() {
  echo "用法: $0 --name <项目名> --group <Java包名> [--title 标题] [--appid 微信AppID] [--out 输出目录]"
  exit 1
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --name) NAME="$2"; shift 2 ;;
    --group) GROUP="$2"; shift 2 ;;
    --title) TITLE="$2"; shift 2 ;;
    --appid) APPID="$2"; shift 2 ;;
    --out) TARGET_DIR="$2"; shift 2 ;;
    *) usage ;;
  esac
done

[[ -z "$NAME" || -z "$GROUP" ]] && usage
[[ -z "$TARGET_DIR" ]] && TARGET_DIR="$(pwd)/$NAME"

if [[ -e "$TARGET_DIR" ]]; then
  echo "目标目录已存在: $TARGET_DIR"
  exit 1
fi

PKG_PATH="${GROUP//./\/}"
ARTIFACT="$NAME"

echo ">>> 复制脚手架到 $TARGET_DIR"
mkdir -p "$TARGET_DIR"
rsync -a \
  --exclude '.git' \
  --exclude '.idea' \
  --exclude 'node_modules' \
  --exclude 'target' \
  --exclude 'bun.lock' \
  --exclude '.DS_Store' \
  "$SCAFFOLD_DIR/" "$TARGET_DIR/"

echo ">>> 替换 Java 包名: com.fs.starter -> $GROUP"
find "$TARGET_DIR" -type f \( -name '*.java' -o -name '*.xml' -o -name '*.yml' -o -name '*.yaml' \) -print0 \
  | while IFS= read -r -d '' f; do
    sed_inplace "s/com\.fs\.starter/${GROUP}/g" "$f"
  done

echo ">>> 替换 Maven 模块名: fs-starter -> $ARTIFACT"
find "$TARGET_DIR" -type f \( -name 'pom.xml' -o -name '*.md' -o -name '*.sh' -o -name '*.ps1' -o -name '*.yml' -o -name '*.xml' -o -name '.env*' \) -print0 \
  | while IFS= read -r -d '' f; do
    sed_inplace "s/fs-starter/${ARTIFACT}/g" "$f"
  done

echo ">>> 重命名 Java 目录"
if [[ -d "$TARGET_DIR/${ARTIFACT}-common/src/main/java/com/fs/starter" ]]; then
  for mod in common config domain mapper service admin app; do
    src="$TARGET_DIR/${ARTIFACT}-${mod}/src/main/java/com/fs/starter"
    dst="$TARGET_DIR/${ARTIFACT}-${mod}/src/main/java/${PKG_PATH}"
    if [[ -d "$src" ]]; then
      mkdir -p "$(dirname "$dst")"
      mv "$src" "$dst"
      rm -rf "$TARGET_DIR/${ARTIFACT}-${mod}/src/main/java/com/fs" 2>/dev/null || true
    fi
    test_src="$TARGET_DIR/${ARTIFACT}-${mod}/src/test/java/com/fs/starter"
    test_dst="$TARGET_DIR/${ARTIFACT}-${mod}/src/test/java/${PKG_PATH}"
    if [[ -d "$test_src" ]]; then
      mkdir -p "$(dirname "$test_dst")"
      mv "$test_src" "$test_dst"
      rm -rf "$TARGET_DIR/${ARTIFACT}-${mod}/src/test/java/com/fs" 2>/dev/null || true
    fi
  done
fi

echo ">>> 替换前端标题与 AppID"
sed_inplace "s/FS Starter Admin/${TITLE} Admin/g" "$TARGET_DIR/frontend-admin/index.html"
sed_inplace "s/FS Starter/${TITLE}/g" "$TARGET_DIR/frontend-mp/app.json"
sed_inplace "s/wxYOUR_APPID_HERE/${APPID}/g" "$TARGET_DIR/frontend-mp/project.config.json"
sed_inplace "s/wxYOUR_APPID_HERE/${APPID}/g" "$TARGET_DIR/${ARTIFACT}-app/src/main/resources/application.yml"

echo ">>> 替换数据库名"
sed_inplace "s/fs_starter_db/${NAME}_db/g" "$TARGET_DIR/sql/init.sql"
find "$TARGET_DIR" -name 'application-*.yml' -print0 | while IFS= read -r -d '' f; do
  sed_inplace "s/fs_starter_db/${NAME}_db/g" "$f"
done

echo ">>> 初始化 git 仓库"
(cd "$TARGET_DIR" && git init -q)

echo ">>> 完成: $TARGET_DIR"
echo "下一步:"
echo "  1. mysql < sql/init.sql"
echo "  2. cd ${ARTIFACT}-admin && mvn spring-boot:run"
echo "  3. cd ${ARTIFACT}-app && mvn spring-boot:run"
echo "  4. cd frontend-admin && bun install && bun dev"
echo "  5. 用微信开发者工具打开 frontend-mp"
