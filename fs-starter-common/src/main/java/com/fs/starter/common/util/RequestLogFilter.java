package com.fs.starter.common.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 接口请求日志过滤器
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestLogFilter extends OncePerRequestFilter {

    private static final int MAX_BODY_LOG_LENGTH = 1024;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        boolean streaming = isStreamingRequest(request);

        long start = System.currentTimeMillis();
        if (streaming) {
            // 流式响应不能包 ContentCachingResponseWrapper，否则会提前 copyBodyToResponse 导致空 body
            try {
                filterChain.doFilter(requestWrapper, response);
            } finally {
                logRequestResponse(request, requestWrapper, response.getStatus(),
                        System.currentTimeMillis() - start, "(streaming)");
            }
            return;
        }

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long cost = System.currentTimeMillis() - start;
            logRequestResponse(request, requestWrapper, responseWrapper.getStatus(), cost,
                    getResponseBody(responseWrapper));
            responseWrapper.copyBodyToResponse();
        }
    }

    private boolean isStreamingRequest(HttpServletRequest request) {
        if (request.getRequestURI() != null && request.getRequestURI().endsWith("/stream")) {
            return true;
        }
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("text/event-stream");
    }

    private void logRequestResponse(HttpServletRequest request,
                                    ContentCachingRequestWrapper requestWrapper,
                                    int status,
                                    long cost,
                                    String responseBody) {
        String queryString = request.getQueryString();
        log.info(">>> {} {} {}",
                request.getMethod(),
                request.getRequestURI(),
                queryString != null ? "?" + queryString : "");
        String requestBody = getRequestBody(requestWrapper);
        if (!requestBody.isEmpty()) {
            log.info(">>> body: {}", requestBody);
        }
        log.info("<<< {} {}ms {}", status, cost, responseBody);
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] body = request.getContentAsByteArray();
        if (body.length == 0) {
            return "";
        }
        String content = new String(body, StandardCharsets.UTF_8);
        return truncate(content);
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] body = response.getContentAsByteArray();
        if (body.length == 0) {
            return "";
        }
        String content = new String(body, StandardCharsets.UTF_8);
        return truncate(content);
    }

    private String truncate(String content) {
        if (content.length() > MAX_BODY_LOG_LENGTH) {
            return content.substring(0, MAX_BODY_LOG_LENGTH) + "...(truncated)";
        }
        return content;
    }
}
