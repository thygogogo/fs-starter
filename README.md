# fs-starter

全栈项目脚手架：**管理后台 API + 微信小程序 API + Vue 管理端 + 微信小程序**，内置 RBAC 角色菜单权限与微信登录。

## 技术栈

| 端 | 技术 |
|----|------|
| 管理端 API | Spring Boot 3.3 + MyBatis-Plus + Sa-Token + MinIO（`:8889`） |
| 小程序 API | Spring Boot 3.3 + WxJava（`:8888`） |
| 管理前端 | Vue 3 + TypeScript + Element Plus + Tailwind + Vite+ |
| 小程序 | 原生微信小程序 |

## 目录结构

```
fs-starter/
├── fs-starter-common/      # 统一响应、异常、Filter
├── fs-starter-config/      # Jackson/MyBatis/Redis/Sa-Token/MinIO/微信
├── fs-starter-domain/      # 实体、DTO、VO
├── fs-starter-mapper/
├── fs-starter-service/
├── fs-starter-admin/       # 管理端 API
├── fs-starter-app/         # 小程序 API
├── frontend-admin/         # Vue 管理后台
├── frontend-mp/            # 微信小程序
├── sql/init.sql            # 建库 + RBAC + 微信用户 + 文件表
├── deploy/                 # 部署脚本模板
└── scripts/init-project.sh # 复制并初始化新项目
```

## 快速开始

### 1. 启动本地基础设施（可选）

```bash
docker compose up -d
mysql -u root -proot123 < sql/init.sql
```

### 2. 初始化数据库（无 Docker 时）

```bash
mysql -u root -p < sql/init.sql
```

默认超管：`admin` / `admin123`

### 2. 启动后端

配置 `fs-starter-admin/src/main/resources/application-test.yml` 中的 MySQL/Redis/MinIO。

```bash
cd fs-starter-admin && mvn spring-boot:run
cd fs-starter-app && mvn spring-boot:run
```

### 3. 启动管理前端

```bash
cd frontend-admin
bun install
bun dev
```

本地代理管理端 API：`http://localhost:8889`

### 4. 启动小程序

1. 修改 `frontend-mp/project.config.json` 的 AppID
2. 修改 `fs-starter-app/src/main/resources/application.yml` 的 `wechat.appid/secret`（开发可设 `wechat.mock: true`）
3. 用微信开发者工具打开 `frontend-mp/`

## 从脚手架创建新项目

```bash
./scripts/init-project.sh \
  --name soldier \
  --group com.soldier \
  --title "军优享" \
  --appid wxYOUR_APPID_HERE \
  --out ../soldier-new
```

脚本会替换包名、模块名、数据库名、标题与 AppID。

## 内置能力

- **RBAC**：`sys_admin` / `sys_role` / `sys_menu` / `sys_role_menu`
- **微信登录**：`wx_user` + `/app/wx/login`
- **文件上传**：MinIO + `sys_file`
- **管理端页面**：登录、工作台、账号管理、权限配置

## SQL 约定

- 增量迁移：`sql/V{N}__描述.sql`
- 全量部署：维护 `sql/init.sql`（合并所有增量）

## 默认端口

| 服务 | 端口 |
|------|------|
| fs-starter-admin | 8889 |
| fs-starter-app | 8888 |
