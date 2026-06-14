package com.fs.starter.admin.controller;

import com.fs.starter.admin.BaseControllerTest;
import com.fs.starter.common.exception.BusinessException;
import com.fs.starter.common.result.ResultCode;
import com.fs.starter.domain.dto.AdminLoginDTO;
import com.fs.starter.domain.vo.LoginVO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminAuthControllerTest extends BaseControllerTest {

    @Test
    void login_success() throws Exception {
        LoginVO loginVO = LoginVO.builder()
                .token("test-token-123")
                .userId(1L)
                .username("admin")
                .nickname("超级管理员")
                .roleCode("super_admin")
                .permissions(List.of("dashboard", "user:list"))
                .build();
        when(adminService.login(any(AdminLoginDTO.class))).thenReturn(loginVO);

        mockMvc.perform(post("/admin/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("test-token-123"))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    void login_wrongPassword() throws Exception {
        when(adminService.login(any(AdminLoginDTO.class)))
                .thenThrow(new BusinessException(ResultCode.LOGIN_FAILED));

        mockMvc.perform(post("/admin/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.LOGIN_FAILED.getCode()));
    }

    @Test
    void login_missingUsername() throws Exception {
        mockMvc.perform(post("/admin/auth/login")
                        .contentType("application/json")
                        .content("{\"password\":\"admin123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.PARAM_ERROR.getCode()));
    }

    @Test
    void login_missingPassword() throws Exception {
        mockMvc.perform(post("/admin/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.PARAM_ERROR.getCode()));
    }

    @Test
    void info_success() throws Exception {
        LoginVO loginVO = LoginVO.builder()
                .userId(1L)
                .username("admin")
                .nickname("超级管理员")
                .roleCode("super_admin")
                .permissions(List.of("dashboard"))
                .build();
        when(adminService.getCurrentUserInfo()).thenReturn(loginVO);
        mockLogin(1L);

        mockMvc.perform(withAuth(get("/admin/auth/info")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    void info_notLoggedIn() throws Exception {
        mockMvc.perform(get("/admin/auth/info"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ResultCode.UNAUTHORIZED.getCode()));
    }

    @Test
    void logout_success() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(post("/admin/auth/logout")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
