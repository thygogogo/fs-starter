package com.fs.starter.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fs.starter.service.AdminService;
import com.fs.starter.service.MenuService;
import com.fs.starter.service.MinioService;
import com.fs.starter.service.RoleService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("unittest")
@Import(TestSaTokenDaoConfig.class)
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String currentToken;

    @MockBean
    protected RedisConnectionFactory redisConnectionFactory;

    @MockBean
    protected AdminService adminService;
    @MockBean
    protected MenuService menuService;
    @MockBean
    protected RoleService roleService;
    @MockBean
    protected MinioService minioService;

    protected void mockLogin(Long adminId) {
        StpUtil.login(adminId);
        currentToken = StpUtil.getTokenValue();
    }

    protected MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder builder) {
        if (currentToken != null) {
            builder.header("Authorization", currentToken);
        }
        return builder;
    }

    @AfterEach
    void cleanup() {
        currentToken = null;
        try {
            StpUtil.logout();
        } catch (Exception ignored) {
        }
    }
}
