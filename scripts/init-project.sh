#!/bin/bash
# 从 fs-starter 脚手架复制并初始化新业务项目
#
# 用法:
#   ./scripts/init-project.sh --name sleep-guard --group com.sleep.guard --title "睡眠系统"
# 默认输出到脚手架上一级目录（../sleep-guard），可用 --out 自定义
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
[[ -z "$TARGET_DIR" ]] && TARGET_DIR="$(dirname "$SCAFFOLD_DIR")/$NAME"
if [[ "$TARGET_DIR" != /* ]]; then
  TARGET_DIR="$(cd "$(dirname "$TARGET_DIR")" && pwd)/$(basename "$TARGET_DIR")"
fi

if [[ -e "$TARGET_DIR" ]]; then
  echo "目标目录已存在: $TARGET_DIR"
  exit 1
fi

PKG_PATH="${GROUP//./\/}"
ARTIFACT="$NAME"

echo ">>> 复制脚手架到 $TARGET_DIR"
mkdir -p "$TARGET_DIR"
RSYNC_EXCLUDES=(
  --exclude '.git'
  --exclude '.idea'
  --exclude '.cursor'
  --exclude 'scripts'
  --exclude 'node_modules'
  --exclude 'target'
  --exclude 'bun.lock'
  --exclude '.DS_Store'
)
# 目标在脚手架内时，避免 rsync 把输出目录复制进自身
if [[ "$TARGET_DIR" == "$SCAFFOLD_DIR"/* ]]; then
  RSYNC_EXCLUDES+=(--exclude "${TARGET_DIR#$SCAFFOLD_DIR/}")
fi
# 脚手架内残留的上次生成目录（含 pom.xml）不再复制进新项目
if [[ -f "$SCAFFOLD_DIR/$NAME/pom.xml" ]]; then
  RSYNC_EXCLUDES+=(--exclude "$NAME")
fi
rsync -a "${RSYNC_EXCLUDES[@]}" "$SCAFFOLD_DIR/" "$TARGET_DIR/"

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

echo ">>> 重命名 Maven 模块目录"
for mod in common domain mapper config service admin app; do
  src_dir="$TARGET_DIR/fs-starter-${mod}"
  dst_dir="$TARGET_DIR/${ARTIFACT}-${mod}"
  if [[ -d "$src_dir" ]]; then
    mv "$src_dir" "$dst_dir"
  fi
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
find "$TARGET_DIR" -path '*/src/main/resources/application*.yml' -print0 \
  | while IFS= read -r -d '' f; do
    sed_inplace "s/wxYOUR_APPID_HERE/${APPID}/g" "$f"
  done

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
