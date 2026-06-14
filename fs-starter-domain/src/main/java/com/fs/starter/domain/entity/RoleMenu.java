package com.fs.starter.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色菜单关联
 */
@Data
@TableName("sys_role_menu")
public class RoleMenu implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 角色编码 */
    private String roleCode;

    /** 菜单ID */
    private Long menuId;

    /** 创建时间 */
    private LocalDateTime createTime;
}
