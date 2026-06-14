package com.fs.starter.domain.enums;


import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 通用枚举接口
 * 数据库存 code，前端展示 desc
 */
public interface IEnum<T> {

    /**
     * 数据库存储的值
     */
    @JsonValue
    T getCode();

    /**
     * 描述信息
     */
    String getDesc();
}
