# CLAUDE.md — fs-starter 脚手架

全栈脚手架：Admin API + App API + Vue 管理端 + 微信小程序。

## 模块

| 模块 | 端口 | 说明 |
|------|------|------|
| fs-starter-admin | 8889 | 管理端 API |
| fs-starter-app | 8888 | 小程序 API |
| frontend-admin | — | Vue 3 管理后台 |
| frontend-mp | — | 微信小程序 |

## 命令

### 后端（项目根目录）

```bash
mvn -pl fs-starter-admin -am spring-boot:run
mvn -pl fs-starter-app -am spring-boot:run
mvn test
```

### 管理前端

```bash
cd frontend-admin
bun install
bun dev
bun run build:prod
```

### 小程序

微信开发者工具打开 `frontend-mp/`。

### 从脚手架创建新项目

```bash
./scripts/init-project.sh --name myapp --group com.example.myapp --title "My App"
```

## 架构约定

- Java 包名：`com.fs.starter`
- 雪花 ID 在 JSON 中为字符串，前端禁止 `Number(id)` 处理业务主键
- SQL 增量：`sql/V{N}__描述.sql`；全量：`sql/init.sql`
- 权限：Sa-Token + RBAC（sys_admin / sys_role / sys_menu）
- 微信登录：`/app/wx/login`，开发可设 `wechat.mock: true`

## 本地基础设施

```bash
docker compose up -d   # MySQL + Redis + MinIO
mysql -u root -p < sql/init.sql
```

默认超管：`admin` / `admin123`
