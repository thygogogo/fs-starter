package com.fs.starter.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILURE(500, "操作失败"),

    // 认证相关 401x
    UNAUTHORIZED(401, "未登录或登录已过期"),
    LOGIN_FAILED(4010, "账号或密码错误"),
    TOKEN_INVALID(4011, "token 无效"),
    TOKEN_EXPIRED(4012, "token 已过期"),
    ACCOUNT_DISABLED(4013, "账号已被禁用"),

    // 权限相关 403x
    FORBIDDEN(403, "无权限访问"),

    // 参数相关 400x
    PARAM_ERROR(400, "参数错误"),
    PARAM_MISSING(4001, "参数缺失"),

    // 业务相关 5xxx
    DATA_NOT_FOUND(5001, "数据不存在"),
    DATA_ALREADY_EXISTS(5002, "数据已存在"),
    OPERATION_FAILED(5003, "操作失败");

    private final int code;
    private final String msg;
}
