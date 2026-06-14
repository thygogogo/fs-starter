package com.fs.starter.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应体
 */
@Data
public class R<T> implements Serializable {

    private int code;
    private String msg;
    private T data;
    private String traceId;
    private long timestamp;

    private R() {
        this.timestamp = System.currentTimeMillis();
        this.traceId = TraceUtil.getTraceId();
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMsg());
        r.setData(data);
        return r;
    }

    public static <T> R<T> fail(String msg) {
        return fail(ResultCode.FAILURE.getCode(), msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMsg());
    }
}
