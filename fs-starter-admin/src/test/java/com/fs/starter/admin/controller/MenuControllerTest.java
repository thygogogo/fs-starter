package com.fs.starter.admin.controller;

import com.fs.starter.admin.BaseControllerTest;
import com.fs.starter.common.exception.BusinessException;
import com.fs.starter.domain.entity.Menu;
import com.fs.starter.domain.enums.StatusEnum;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuControllerTest extends BaseControllerTest {

    @Test
    void list_success() throws Exception {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setParentId(0L);
        menu.setName("工作台");
        menu.setPermissionKey("dashboard");
        menu.setType("menu");
        menu.setPath("/dashboard");
        menu.setSort(1);
        menu.setStatus(StatusEnum.ENABLED);

        when(menuService.listAllMenus()).thenReturn(List.of(menu));
        mockLogin(1L);

        mockMvc.perform(withAuth(get("/admin/system/menu/list")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("工作台"));
    }

    @Test
    void tree_success() throws Exception {
        when(menuService.getMenuTree()).thenReturn(List.of(
                Map.of("id", 1, "name", "工作台", "children", List.of())
        ));
        mockLogin(1L);

        mockMvc.perform(withAuth(get("/admin/system/menu/tree")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void create_success() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(post("/admin/system/menu")
                        .contentType("application/json")
                        .content("{\"name\":\"新菜单\",\"type\":\"menu\",\"parentId\":0}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(menuService).createMenu(any());
    }

    @Test
    void update_success() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(put("/admin/system/menu")
                        .contentType("application/json")
                        .content("{\"id\":1,\"name\":\"修改后的菜单\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void delete_success() throws Exception {
        mockLogin(1L);

        mockMvc.perform(withAuth(delete("/admin/system/menu/1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void delete_hasChildren() throws Exception {
        doThrow(new BusinessException("该菜单下有子菜单，不能删除"))
                .when(menuService).deleteMenu(anyLong());
        mockLogin(1L);

        mockMvc.perform(withAuth(delete("/admin/system/menu/1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void list_notLoggedIn() throws Exception {
        mockMvc.perform(get("/admin/system/menu/list"))
                .andExpect(status().isUnauthorized());
    }
}
