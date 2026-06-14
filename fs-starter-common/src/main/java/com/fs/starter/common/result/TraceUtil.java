package com.fs.starter.common.result;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * traceId 工具类，基于 SLF4J MDC 实现链路追踪
 */
public class TraceUtil {

    private static final String TRACE_ID_KEY = "traceId";

    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId == null) {
            traceId = generateTraceId();
            MDC.put(TRACE_ID_KEY, traceId);
        }
        return traceId;
    }

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static void clear() {
        MDC.remove(TRACE_ID_KEY);
    }

    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
