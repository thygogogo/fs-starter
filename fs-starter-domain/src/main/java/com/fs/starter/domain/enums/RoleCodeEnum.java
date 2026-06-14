package com.fs.starter.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色编码枚举
 */
@Getter
@AllArgsConstructor
public enum RoleCodeEnum implements IEnum<String> {

    SUPER_ADMIN("super_admin", "超级管理员"),
    ADMIN("admin", "管理员");

    @EnumValue
    private final String code;
    private final String desc;
}
