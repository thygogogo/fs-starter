package com.fs.starter.admin.controller;

import com.fs.starter.common.result.R;
import com.fs.starter.domain.entity.Role;
import com.fs.starter.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理接口
 */
@RestController
@RequestMapping("/admin/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public R<List<Role>> list() {
        return R.ok(roleService.listRoles());
    }

    /** 获取角色关联的菜单 ID 列表 */
    @GetMapping("/{code}/menus")
    public R<List<Long>> getRoleMenuIds(@PathVariable String code) {
        return R.ok(roleService.getRoleMenuIds(code));
    }

    /** 更新角色菜单关联 */
    @PutMapping("/{code}/menus")
    public R<Void> updateRoleMenus(@PathVariable String code, @RequestBody List<Long> menuIds) {
        roleService.updateRoleMenus(code, menuIds);
        return R.ok();
    }
}
