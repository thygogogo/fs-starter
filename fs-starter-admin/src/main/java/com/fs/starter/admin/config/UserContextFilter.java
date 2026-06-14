package com.fs.starter.admin.config;

import cn.dev33.satoken.stp.StpUtil;
import com.fs.starter.common.context.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 管理端请求过滤器：将当前登录管理员写入 UserContext
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (StpUtil.isLogin()) {
                UserContext.set(StpUtil.getLoginIdAsLong(), UserContext.TYPE_ADMIN);
            }
        } catch (Exception ignored) {
            // 未登录，不设置
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
