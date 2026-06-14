package com.fs.starter.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
@AllArgsConstructor
public enum GenderEnum implements IEnum<Integer> {

    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

    @EnumValue
    private final Integer code;
    private final String desc;
}
