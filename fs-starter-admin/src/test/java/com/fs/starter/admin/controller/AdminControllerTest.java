package com.fs.starter.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fs.starter.admin.BaseControllerTest;
import com.fs.starter.common.exception.BusinessException;
import com.fs.starter.common.result.ResultCode;
import com.fs.starter.domain.entity.Admin;
import com.fs.starter.domain.enums.RoleCodeEnum;
import com.fs.starter.domain.enums.StatusEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest extends BaseControllerTest {

    private Admin mockAdmin() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setUsername("testuser");
        admin.setNickname("测试用户");
        admin.setPhone("13800138000");
        admin.setRoleCode(RoleCodeEnum.ADMIN);
        admin.setStatus(StatusEnum.ENABLED);
        admin.setCreateTime(LocalDateTime.now());
        return admin;
    }

    @Test
    void list_success() throws Exception {
        Page<Admin> page = new Page<>(1, 10);
        page.setRecords(List.of(mockAdmin()));
        page.setTotal(1);
        when(adminService.listAdmins(anyInt(), anyInt(), any(), any(), any())).thenReturn(page);
        mockLogin(1L);

        mockMvc.perform(withAuth(get("/admin/system/admin/list")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void list_notLoggedIn() throws Exception {
        mockMvc.perform(get("/admin/system/admin/list"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_success() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(post("/admin/system/admin")
                        .contentType("application/json")
                        .content("{\"username\":\"newuser\",\"password\":\"123456\",\"roleCode\":\"admin\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).createAdmin(any());
    }

    @Test
    void create_missingUsername() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(post("/admin/system/admin")
                        .contentType("application/json")
                        .content("{\"password\":\"123456\",\"roleCode\":\"admin\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.PARAM_ERROR.getCode()));
    }

    @Test
    void create_duplicateUsername() throws Exception {
        doThrow(new BusinessException(ResultCode.DATA_ALREADY_EXISTS))
                .when(adminService).createAdmin(any());
        mockLogin(1L);

        mockMvc.perform(withAuth(post("/admin/system/admin")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"123456\",\"roleCode\":\"admin\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.DATA_ALREADY_EXISTS.getCode()));
    }

    @Test
    void update_success() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(put("/admin/system/admin")
                        .contentType("application/json")
                        .content("{\"id\":1,\"nickname\":\"新昵称\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void update_missingId() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(put("/admin/system/admin")
                        .contentType("application/json")
                        .content("{\"nickname\":\"新昵称\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.PARAM_ERROR.getCode()));
    }

    @Test
    void delete_success() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(delete("/admin/system/admin/100")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void delete_notFound() throws Exception {
        doThrow(new BusinessException(ResultCode.DATA_NOT_FOUND))
                .when(adminService).deleteAdmin(anyLong());
        mockLogin(1L);

        mockMvc.perform(withAuth(delete("/admin/system/admin/999")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.DATA_NOT_FOUND.getCode()));
    }

    @Test
    void resetPassword_success() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(put("/admin/system/admin/1/reset-password")
                        .contentType("application/json")
                        .content("{\"password\":\"newpass123\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void resetPassword_missingPassword() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(put("/admin/system/admin/1/reset-password")
                        .contentType("application/json")
                        .content("{}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.PARAM_ERROR.getCode()));
    }

    @Test
    void toggleStatus_success() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(put("/admin/system/admin/1/toggle-status")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
