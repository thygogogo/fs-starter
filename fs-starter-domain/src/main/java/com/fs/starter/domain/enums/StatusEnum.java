package com.fs.starter.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举（用户、管理员等）
 */
@Getter
@AllArgsConstructor
public enum StatusEnum implements IEnum<Integer> {

    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    @EnumValue
    private final Integer code;
    private final String desc;
}
