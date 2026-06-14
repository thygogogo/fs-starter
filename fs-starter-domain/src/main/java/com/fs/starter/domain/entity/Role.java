package com.fs.starter.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fs.starter.domain.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseDomain {

    /** 角色编码（唯一标识） */
    private String code;

    /** 角色名称 */
    private String name;

    /** 描述 */
    private String description;

    /** 状态 */
    private StatusEnum status;
}
