package com.fs.starter.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fs.starter.domain.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单权限
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class Menu extends BaseDomain {

    /** 父菜单ID，0=顶级 */
    private Long parentId;

    /** 菜单名称 */
    private String name;

    /** 权限标识 */
    private String permissionKey;

    /** 类型：menu-菜单 button-按钮 */
    private String type;

    /** 路由路径 */
    private String path;

    /** 图标 */
    private String icon;

    /** 排序 */
    private Integer sort;

    /** 状态 */
    private StatusEnum status;
}
