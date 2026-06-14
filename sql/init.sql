-- fs-starter 数据库初始化脚本

CREATE DATABASE IF NOT EXISTS fs_starter_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE fs_starter_db;

-- 管理员表
CREATE TABLE IF NOT EXISTS `sys_admin` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role_code` VARCHAR(50) DEFAULT NULL COMMENT '角色编码',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `create_by_type` TINYINT DEFAULT NULL COMMENT '创建者类型：1-管理员 2-微信用户',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者ID',
    `update_by_type` TINYINT DEFAULT NULL COMMENT '更新者类型：1-管理员 2-微信用户',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `create_by_type` TINYINT DEFAULT NULL COMMENT '创建者类型：1-管理员 2-微信用户',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者ID',
    `update_by_type` TINYINT DEFAULT NULL COMMENT '更新者类型：1-管理员 2-微信用户',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单权限表
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID，0=顶级',
    `name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `permission_key` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `type` VARCHAR(10) NOT NULL DEFAULT 'menu' COMMENT '类型：menu-菜单 button-按钮',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `create_by_type` TINYINT DEFAULT NULL COMMENT '创建者类型：1-管理员 2-微信用户',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者ID',
    `update_by_type` TINYINT DEFAULT NULL COMMENT '更新者类型：1-管理员 2-微信用户',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_role_code` (`role_code`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 微信用户表
CREATE TABLE IF NOT EXISTS `wx_user` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `open_id` VARCHAR(64) NOT NULL COMMENT '微信openId',
    `union_id` VARCHAR(64) DEFAULT NULL COMMENT '微信unionId',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `create_by_type` TINYINT DEFAULT NULL COMMENT '创建者类型：1-管理员 2-微信用户',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者ID',
    `update_by_type` TINYINT DEFAULT NULL COMMENT '更新者类型：1-管理员 2-微信用户',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_open_id` (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信用户表';

-- 文件记录表
CREATE TABLE IF NOT EXISTS `sys_file` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `file_hash` VARCHAR(64) NOT NULL COMMENT '文件MD5',
    `url` VARCHAR(512) NOT NULL COMMENT '文件访问地址',
    `original_name` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `create_by_type` TINYINT DEFAULT NULL COMMENT '创建者类型：1-管理员 2-微信用户',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者ID',
    `update_by_type` TINYINT DEFAULT NULL COMMENT '更新者类型：1-管理员 2-微信用户',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_file_hash` (`file_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件记录表';

-- 超级管理员（密码 admin123）
INSERT INTO `sys_admin` (`id`, `username`, `password`, `nickname`, `role_code`, `status`)
VALUES (1, 'admin', '$2a$10$EqKcp1WFKVQIShMPC7B3kuznX9gAZMsVnSNjN0ABNuHVBCpzqABae', '超级管理员', 'super_admin', 1);

INSERT INTO `sys_role` (`id`, `code`, `name`, `description`, `status`, `create_time`, `update_time`, `version`, `deleted`)
VALUES
(1, 'super_admin', '超级管理员', '拥有所有权限', 1, NOW(), NOW(), 0, 0),
(2, 'admin', '管理员', '普通管理员，权限可配置', 1, NOW(), NOW(), 0, 0);

-- 脚手架菜单：工作台 + 系统管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `permission_key`, `type`, `path`, `icon`, `sort`, `status`, `version`, `deleted`) VALUES
(1, 0, '工作台', 'dashboard', 'menu', '/dashboard', 'HomeIcon', 1, 1, 0, 0),
(11, 0, '系统管理', NULL, 'menu', NULL, 'Cog6ToothIcon', 2, 1, 0, 0),
(13, 11, '账号管理', 'system:account', 'menu', '/system/account', NULL, 1, 1, 0, 0),
(14, 11, '权限配置', 'system:permission', 'menu', '/system/permission', NULL, 2, 1, 0, 0),
(28, 13, '创建账号', 'system:account:create', 'button', NULL, NULL, 1, 1, 0, 0),
(29, 13, '编辑账号', 'system:account:edit', 'button', NULL, NULL, 2, 1, 0, 0),
(30, 13, '重置密码', 'system:account:reset-password', 'button', NULL, NULL, 3, 1, 0, 0),
(31, 13, '禁用账号', 'system:account:disable', 'button', NULL, NULL, 4, 1, 0, 0);

INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES
(1, 'admin', 1),
(2, 'admin', 11),
(3, 'admin', 13),
(4, 'admin', 14),
(5, 'admin', 29),
(6, 'admin', 30),
(7, 'admin', 31);
