package com.fs.starter.admin.controller;

import com.fs.starter.admin.BaseControllerTest;
import com.fs.starter.domain.entity.Role;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleControllerTest extends BaseControllerTest {

    @Test
    void list_success() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setCode("admin");
        role.setName("管理员");
        when(roleService.listRoles()).thenReturn(List.of(role));
        mockLogin(1L);

        mockMvc.perform(withAuth(get("/admin/system/role/list")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].code").value("admin"));
    }

    @Test
    void getRoleMenuIds_success() throws Exception {
        when(roleService.getRoleMenuIds("admin")).thenReturn(List.of(1L, 2L, 3L));
        mockLogin(1L);

        mockMvc.perform(withAuth(get("/admin/system/role/admin/menus")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    void updateRoleMenus_success() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(put("/admin/system/role/admin/menus")
                        .contentType("application/json")
                        .content("[1,2,3,4]")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(roleService).updateRoleMenus(anyString(), any());
    }

    @Test
    void list_notLoggedIn() throws Exception {
        mockMvc.perform(get("/admin/system/role/list"))
                .andExpect(status().isUnauthorized());
    }
}
